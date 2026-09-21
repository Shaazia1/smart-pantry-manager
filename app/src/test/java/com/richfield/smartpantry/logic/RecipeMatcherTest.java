package com.richfield.smartpantry.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.richfield.smartpantry.model.Ingredient;
import com.richfield.smartpantry.model.MatchResult;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.model.Recipe;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Tests for the strict rule. This is the part of the app the marker will try to
// break, so the awkward cases are all in here.
public class RecipeMatcherTest {

    private Recipe pastaRecipe() {
        Recipe recipe = new Recipe(1, "Tomato Pasta", "Main", 25, 4, "Boil. Fry. Mix.");
        recipe.addIngredient(new Ingredient("pasta", 250, "g"));
        recipe.addIngredient(new Ingredient("tomato", 4, "piece"));
        recipe.addIngredient(new Ingredient("garlic", 2, "clove"));
        return recipe;
    }

    private PantryItem item(String name, double quantity, String unit) {
        return new PantryItem(1, name, quantity, unit, null);
    }

    @Test
    public void suggestedWhenEveryIngredientIsThere() {
        List<PantryItem> pantry = Arrays.asList(
                item("pasta", 500, "g"),
                item("tomatoes", 6, "piece"),
                item("garlic", 5, "clove"));

        MatchResult result = RecipeMatcher.match(pastaRecipe(), pantry);

        assertTrue(result.isCookable());
        assertEquals(0, result.getMissingCount());
    }

    @Test
    public void notSuggestedWhenOneIngredientIsMissing() {
        List<PantryItem> pantry = Arrays.asList(
                item("pasta", 500, "g"),
                item("tomatoes", 6, "piece"));       // no garlic

        MatchResult result = RecipeMatcher.match(pastaRecipe(), pantry);

        assertFalse(result.isCookable());
        assertEquals(1, result.getMissingCount());
        assertEquals("garlic", result.getMissingNames());
    }

    @Test
    public void notSuggestedWhenThereIsNotEnoughOfSomething() {
        List<PantryItem> pantry = Arrays.asList(
                item("pasta", 100, "g"),             // the recipe needs 250 g
                item("tomato", 6, "piece"),
                item("garlic", 5, "clove"));

        MatchResult result = RecipeMatcher.match(pastaRecipe(), pantry);

        assertFalse(result.isCookable());
        assertEquals("pasta", result.getMissingNames());
    }

    @Test
    public void amountsAreComparedAcrossUnits() {
        List<PantryItem> pantry = Arrays.asList(
                item("Pasta", 1, "kg"),              // 1 kg covers the 250 g needed
                item("Tomatoes", 4, "pieces"),
                item("Garlic", 2, "cloves"));

        assertTrue(RecipeMatcher.match(pastaRecipe(), pantry).isCookable());
    }

    @Test
    public void twoRowsOfTheSameThingAreAddedTogether() {
        List<PantryItem> pantry = Arrays.asList(
                new PantryItem(1, "pasta", 150, "g", null),
                new PantryItem(2, "Pasta", 150, "g", null),   // 300 g altogether
                item("tomato", 4, "piece"),
                item("garlic", 2, "clove"));

        assertTrue(RecipeMatcher.match(pastaRecipe(), pantry).isCookable());
    }

    @Test
    public void almostThereOnlyListsRecipesMissingOneThing() {
        Recipe omelette = new Recipe(2, "Cheese Omelette", "Breakfast", 10, 1, "Beat. Fry.");
        omelette.addIngredient(new Ingredient("egg", 3, "piece"));
        omelette.addIngredient(new Ingredient("cheese", 50, "g"));

        List<Recipe> recipes = Arrays.asList(pastaRecipe(), omelette);
        // the omelette only misses cheese, the pasta misses all three
        List<PantryItem> pantry = Arrays.asList(item("eggs", 6, "piece"));

        List<MatchResult> all = RecipeMatcher.matchAll(recipes, pantry);

        assertTrue(RecipeMatcher.cookable(all).isEmpty());
        List<MatchResult> almost = RecipeMatcher.almostThere(all);
        assertEquals(1, almost.size());
        assertEquals("Cheese Omelette", almost.get(0).getRecipe().getName());
    }

    @Test
    public void anEmptyPantryMatchesNothing() {
        List<MatchResult> all = RecipeMatcher.matchAll(
                Arrays.asList(pastaRecipe()), new ArrayList<PantryItem>());

        assertTrue(RecipeMatcher.cookable(all).isEmpty());
        assertEquals(3, all.get(0).getMissingCount());
    }
}
