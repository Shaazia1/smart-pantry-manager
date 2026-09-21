package com.richfield.smartpantry.logic;

import com.richfield.smartpantry.model.Ingredient;
import com.richfield.smartpantry.model.MatchResult;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// The main rule of the app. A recipe is only suggested when every single
// ingredient it needs is in the pantry, in at least the amount the recipe asks
// for. Four out of five ingredients is not good enough, that recipe stays out of
// the list and can only turn up in the separate almost there section.
//
// Names are compared through the normaliser and amounts through the unit
// converter, so 2 Tomatoes at home covers a recipe that wants 1 tomato.
public class RecipeMatcher {

    // doubles are never exactly equal, so allow a tiny bit of slack
    private static final double EPSILON = 0.0001;

    // Tests every recipe. Cookable ones come first, then the ones missing the
    // fewest ingredients.
    public static List<MatchResult> matchAll(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, List<PantryItem>> index = indexPantry(pantry);

        List<MatchResult> results = new ArrayList<>();
        for (Recipe recipe : recipes) {
            results.add(match(recipe, index));
        }

        Collections.sort(results, new Comparator<MatchResult>() {
            @Override
            public int compare(MatchResult first, MatchResult second) {
                if (first.getMissingCount() != second.getMissingCount()) {
                    return first.getMissingCount() - second.getMissingCount();
                }
                return first.getRecipe().getName()
                        .compareToIgnoreCase(second.getRecipe().getName());
            }
        });
        return results;
    }

    public static MatchResult match(Recipe recipe, Map<String, List<PantryItem>> index) {
        List<Ingredient> missing = new ArrayList<>();
        for (Ingredient needed : recipe.getIngredients()) {
            if (!isSatisfied(needed, index)) {
                missing.add(needed);
            }
        }
        return new MatchResult(recipe, missing);
    }

    // used by the recipe detail screen, which only has one recipe to check
    public static MatchResult match(Recipe recipe, List<PantryItem> pantry) {
        return match(recipe, indexPantry(pantry));
    }

    // the strict suggestions
    public static List<MatchResult> cookable(List<MatchResult> results) {
        List<MatchResult> cookable = new ArrayList<>();
        for (MatchResult result : results) {
            if (result.isCookable()) {
                cookable.add(result);
            }
        }
        return cookable;
    }

    // recipes short of exactly one ingredient, kept separate from the real
    // suggestions so the strict rule isn't watered down
    public static List<MatchResult> almostThere(List<MatchResult> results) {
        List<MatchResult> almost = new ArrayList<>();
        for (MatchResult result : results) {
            if (result.getMissingCount() == 1) {
                almost.add(result);
            }
        }
        return almost;
    }

    // Groups the pantry by cleaned up name. A list is kept per name so that two
    // rows of the same thing, say 500 g rice and 1 kg rice, can be added up.
    public static Map<String, List<PantryItem>> indexPantry(List<PantryItem> pantry) {
        Map<String, List<PantryItem>> index = new HashMap<>();
        if (pantry == null) {
            return index;
        }
        for (PantryItem item : pantry) {
            String key = IngredientNormalizer.normalize(item.getName());
            if (key.isEmpty()) {
                continue;
            }
            List<PantryItem> sameName = index.get(key);
            if (sameName == null) {
                sameName = new ArrayList<>();
                index.put(key, sameName);
            }
            sameName.add(item);
        }
        return index;
    }

    // Is this one ingredient covered by the pantry?
    private static boolean isSatisfied(Ingredient needed, Map<String, List<PantryItem>> index) {
        List<PantryItem> matches = index.get(IngredientNormalizer.normalize(needed.getName()));
        if (matches == null || matches.isEmpty()) {
            return false;                       // not in the pantry at all
        }

        double available = 0;
        boolean unitsMatch = false;
        for (PantryItem item : matches) {
            if (UnitConverter.isComparable(item.getUnit(), needed.getUnit())) {
                unitsMatch = true;
                available += UnitConverter.convert(
                        item.getQuantity(), item.getUnit(), needed.getUnit());
            }
        }

        // When the units can't be compared, like 1 can against 200 g, guessing a
        // conversion would be worse than trusting the user, so having any amount
        // of it counts.
        if (!unitsMatch) {
            for (PantryItem item : matches) {
                if (item.getQuantity() > 0) {
                    return true;
                }
            }
            return false;
        }

        return available + EPSILON >= needed.getQuantity();
    }
}
