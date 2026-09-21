package com.richfield.smartpantry.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.richfield.smartpantry.data.RecipeSeeder;
import com.richfield.smartpantry.db.PantryContract.RecipeIngredientTable;
import com.richfield.smartpantry.db.PantryContract.RecipeTable;
import com.richfield.smartpantry.model.Ingredient;
import com.richfield.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Reads the recipes out of the database. The recipe and its ingredients live in
// two tables, so they get joined back together into Recipe objects here.
public class RecipeRepository {

    private final DatabaseHelper helper;

    public RecipeRepository(Context context) {
        this.helper = DatabaseHelper.getInstance(context);
    }

    public List<Recipe> getAllWithIngredients() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Recipe> recipes = new ArrayList<>();
        Map<Long, Recipe> byId = new HashMap<>();

        Cursor recipeCursor = db.query(RecipeTable.NAME, null, null, null, null, null,
                RecipeTable.RECIPE_NAME + " COLLATE NOCASE ASC");
        try {
            while (recipeCursor.moveToNext()) {
                Recipe recipe = recipeFromCursor(recipeCursor);
                recipes.add(recipe);
                byId.put(recipe.getId(), recipe);
            }
        } finally {
            recipeCursor.close();
        }

        // read every ingredient line once and hand it to the recipe it belongs to,
        // instead of running a separate query for each of the 20 recipes
        Cursor ingredientCursor = db.query(RecipeIngredientTable.NAME, null, null, null,
                null, null, RecipeIngredientTable.ID + " ASC");
        try {
            while (ingredientCursor.moveToNext()) {
                long recipeId = ingredientCursor.getLong(
                        ingredientCursor.getColumnIndexOrThrow(RecipeIngredientTable.RECIPE_ID));
                Recipe recipe = byId.get(recipeId);
                if (recipe != null) {
                    recipe.addIngredient(ingredientFromCursor(ingredientCursor));
                }
            }
        } finally {
            ingredientCursor.close();
        }

        return recipes;
    }

    // One recipe with its ingredients, or null if the id doesn't exist
    public Recipe getById(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Recipe recipe = null;

        Cursor cursor = db.query(RecipeTable.NAME, null,
                RecipeTable.ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);
        try {
            if (cursor.moveToFirst()) {
                recipe = recipeFromCursor(cursor);
            }
        } finally {
            cursor.close();
        }

        if (recipe == null) {
            return null;
        }

        Cursor ingredientCursor = db.query(RecipeIngredientTable.NAME, null,
                RecipeIngredientTable.RECIPE_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, RecipeIngredientTable.ID + " ASC");
        try {
            while (ingredientCursor.moveToNext()) {
                recipe.addIngredient(ingredientFromCursor(ingredientCursor));
            }
        } finally {
            ingredientCursor.close();
        }
        return recipe;
    }

    // Empties the recipe tables and seeds them again. Used by the settings
    // screen. The pantry is left alone.
    public void resetRecipes() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(RecipeIngredientTable.NAME, null, null);
            db.delete(RecipeTable.NAME, null, null);
            RecipeSeeder.seed(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public int count() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + RecipeTable.NAME, null);
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    private Recipe recipeFromCursor(Cursor cursor) {
        return new Recipe(
                cursor.getLong(cursor.getColumnIndexOrThrow(RecipeTable.ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(RecipeTable.RECIPE_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(RecipeTable.CATEGORY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(RecipeTable.MINUTES)),
                cursor.getInt(cursor.getColumnIndexOrThrow(RecipeTable.SERVINGS)),
                cursor.getString(cursor.getColumnIndexOrThrow(RecipeTable.STEPS)));
    }

    private Ingredient ingredientFromCursor(Cursor cursor) {
        return new Ingredient(
                cursor.getLong(cursor.getColumnIndexOrThrow(RecipeIngredientTable.ID)),
                cursor.getLong(cursor.getColumnIndexOrThrow(RecipeIngredientTable.RECIPE_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(RecipeIngredientTable.ITEM_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(RecipeIngredientTable.QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(RecipeIngredientTable.UNIT)));
    }
}
