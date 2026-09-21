package com.richfield.smartpantry.model;

import java.util.ArrayList;
import java.util.List;

// What came out of testing one recipe against the pantry. A recipe can only be
// suggested when nothing at all is missing.
public class MatchResult {

    private final Recipe recipe;
    private final List<Ingredient> missing;

    public MatchResult(Recipe recipe, List<Ingredient> missing) {
        this.recipe = recipe;
        this.missing = missing == null ? new ArrayList<Ingredient>() : missing;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public List<Ingredient> getMissing() {
        return missing;
    }

    public boolean isCookable() {
        return missing.isEmpty();
    }

    public int getMissingCount() {
        return missing.size();
    }

    // names of what is missing, used by the almost there section
    public String getMissingNames() {
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < missing.size(); i++) {
            if (i > 0) {
                names.append(", ");
            }
            names.append(missing.get(i).getName());
        }
        return names.toString();
    }
}
