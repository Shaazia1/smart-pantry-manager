package com.richfield.smartpantry.model;

import java.util.ArrayList;
import java.util.List;

// A recipe plus the ingredients it needs. The ingredients are stored in their
// own table and get attached to the recipe when it is read back.
public class Recipe {

    private long id;
    private String name;
    private String category;
    private int minutes;
    private int servings;
    private String steps;
    private List<Ingredient> ingredients = new ArrayList<>();

    public Recipe(long id, String name, String category, int minutes, int servings, String steps) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.minutes = minutes;
        this.servings = servings;
        this.steps = steps;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getServings() {
        return servings;
    }

    public String getSteps() {
        return steps;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    @Override
    public String toString() {
        return name;
    }
}
