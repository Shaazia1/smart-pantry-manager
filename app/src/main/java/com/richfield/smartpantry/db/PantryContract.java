package com.richfield.smartpantry.db;

// Table and column names kept in one place so a typo can't spread through the app.
public final class PantryContract {

    private PantryContract() {
    }

    // What the user has at home
    public static final class PantryTable {
        public static final String NAME = "pantry_items";
        public static final String ID = "_id";
        public static final String ITEM_NAME = "name";
        public static final String NAME_KEY = "name_key";   // cleaned up name, used for matching
        public static final String QUANTITY = "quantity";
        public static final String UNIT = "unit";
        public static final String EXPIRY_DATE = "expiry_date";

        public static final String CREATE_SQL =
                "CREATE TABLE " + NAME + " ("
                        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + ITEM_NAME + " TEXT NOT NULL, "
                        + NAME_KEY + " TEXT NOT NULL, "
                        + QUANTITY + " REAL NOT NULL DEFAULT 0, "
                        + UNIT + " TEXT NOT NULL DEFAULT '', "
                        + EXPIRY_DATE + " TEXT)";
    }

    // The built in recipes
    public static final class RecipeTable {
        public static final String NAME = "recipes";
        public static final String ID = "_id";
        public static final String RECIPE_NAME = "name";
        public static final String CATEGORY = "category";
        public static final String MINUTES = "minutes";
        public static final String SERVINGS = "servings";
        public static final String STEPS = "steps";

        public static final String CREATE_SQL =
                "CREATE TABLE " + NAME + " ("
                        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + RECIPE_NAME + " TEXT NOT NULL, "
                        + CATEGORY + " TEXT, "
                        + MINUTES + " INTEGER DEFAULT 0, "
                        + SERVINGS + " INTEGER DEFAULT 1, "
                        + STEPS + " TEXT)";
    }

    // Each recipe has many ingredient lines, so they get their own table
    public static final class RecipeIngredientTable {
        public static final String NAME = "recipe_ingredients";
        public static final String ID = "_id";
        public static final String RECIPE_ID = "recipe_id";
        public static final String ITEM_NAME = "name";
        public static final String NAME_KEY = "name_key";
        public static final String QUANTITY = "quantity";
        public static final String UNIT = "unit";

        public static final String CREATE_SQL =
                "CREATE TABLE " + NAME + " ("
                        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + RECIPE_ID + " INTEGER NOT NULL, "
                        + ITEM_NAME + " TEXT NOT NULL, "
                        + NAME_KEY + " TEXT NOT NULL, "
                        + QUANTITY + " REAL NOT NULL DEFAULT 0, "
                        + UNIT + " TEXT NOT NULL DEFAULT '', "
                        + "FOREIGN KEY (" + RECIPE_ID + ") REFERENCES "
                        + RecipeTable.NAME + "(" + RecipeTable.ID + ") ON DELETE CASCADE)";
    }
}
