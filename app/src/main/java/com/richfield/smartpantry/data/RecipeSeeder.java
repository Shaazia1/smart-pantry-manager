package com.richfield.smartpantry.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.richfield.smartpantry.db.PantryContract.RecipeIngredientTable;
import com.richfield.smartpantry.db.PantryContract.RecipeTable;
import com.richfield.smartpantry.logic.IngredientNormalizer;

// Puts the built in recipes into the database the first time the app runs.
// Ingredient lines are written as "quantity|unit|name" to keep the list readable.
public class RecipeSeeder {

    public static void seed(SQLiteDatabase db) {

        insertRecipe(db, "Leftover Fried Rice", "Main", 20, 3,
                "1. Heat the oil in a large pan on medium heat.\n"
                        + "2. Fry the chopped onion and carrot until soft.\n"
                        + "3. Push them to one side and scramble the eggs in the pan.\n"
                        + "4. Add the cooked rice and soy sauce and stir fry for 3 minutes.\n"
                        + "5. Serve hot.",
                "300|g|rice", "2|piece|egg", "1|piece|onion", "1|piece|carrot",
                "2|tbsp|soy sauce", "2|tbsp|cooking oil");

        insertRecipe(db, "Cheese Omelette", "Breakfast", 10, 1,
                "1. Beat the eggs with a pinch of salt.\n"
                        + "2. Melt the butter in a non stick pan on medium heat.\n"
                        + "3. Pour in the eggs and cook until almost set.\n"
                        + "4. Put the cheese on one half and fold the omelette over.\n"
                        + "5. Slide it onto a plate.",
                "3|piece|egg", "50|g|cheese", "10|g|butter", "1|pinch|salt");

        insertRecipe(db, "Simple Tomato Pasta", "Main", 25, 4,
                "1. Boil the pasta in salted water until just soft.\n"
                        + "2. Fry the garlic in the oil for 30 seconds.\n"
                        + "3. Add the chopped tomatoes and simmer for 10 minutes.\n"
                        + "4. Drain the pasta, mix it into the sauce and season.",
                "250|g|pasta", "4|piece|tomato", "2|clove|garlic",
                "2|tbsp|cooking oil", "1|tsp|salt");

        insertRecipe(db, "Creamy Mashed Potato", "Side", 30, 4,
                "1. Peel and quarter the potatoes, then boil until soft.\n"
                        + "2. Drain them well and mash while still hot.\n"
                        + "3. Beat in the butter and the warm milk.\n"
                        + "4. Season with salt.",
                "600|g|potato", "40|g|butter", "100|ml|milk", "1|tsp|salt");

        insertRecipe(db, "Vegetable Stir Fry", "Main", 15, 2,
                "1. Slice all the vegetables into thin strips.\n"
                        + "2. Heat the oil in a wok until very hot.\n"
                        + "3. Stir fry the onion, carrot and pepper for 5 minutes.\n"
                        + "4. Add the soy sauce, toss once more and serve.",
                "2|piece|carrot", "1|piece|bell pepper", "1|piece|onion",
                "2|tbsp|soy sauce", "2|tbsp|cooking oil");

        insertRecipe(db, "Pap and Tomato Relish", "Main", 35, 4,
                "1. Boil 750 ml of salted water and stir in the maize meal.\n"
                        + "2. Cover and steam on low heat for 25 minutes, stirring now and then.\n"
                        + "3. Fry the onion in the oil, then add the chopped tomatoes.\n"
                        + "4. Simmer the relish for 10 minutes and spoon it over the pap.",
                "250|g|maize meal", "3|piece|tomato", "1|piece|onion",
                "1|tbsp|cooking oil", "1|tsp|salt");

        insertRecipe(db, "French Toast", "Breakfast", 15, 2,
                "1. Whisk the eggs, milk and sugar together in a shallow dish.\n"
                        + "2. Soak each slice of bread for a few seconds on each side.\n"
                        + "3. Fry in the butter until golden on both sides.",
                "4|slice|bread", "2|piece|egg", "100|ml|milk",
                "1|tbsp|sugar", "20|g|butter");

        insertRecipe(db, "Pancakes", "Breakfast", 25, 4,
                "1. Mix the flour, sugar and salt together.\n"
                        + "2. Beat in the eggs and milk until the batter is smooth.\n"
                        + "3. Let the batter stand for 10 minutes.\n"
                        + "4. Fry thin pancakes in a hot, lightly greased pan.",
                "250|g|flour", "500|ml|milk", "2|piece|egg",
                "2|tbsp|sugar", "1|pinch|salt");

        insertRecipe(db, "Lentil Soup", "Soup", 40, 4,
                "1. Fry the onion, carrot and garlic until soft.\n"
                        + "2. Add the lentils, the stock cube and 1 litre of water.\n"
                        + "3. Simmer for 30 minutes until the lentils are soft.",
                "200|g|lentils", "1|piece|onion", "2|piece|carrot",
                "2|clove|garlic", "1|piece|stock cube");

        insertRecipe(db, "One Pot Chicken and Rice", "Main", 45, 4,
                "1. Brown the chicken pieces in the oil.\n"
                        + "2. Add the chopped onion and fry until soft.\n"
                        + "3. Stir in the rice, the stock cube and 500 ml of water.\n"
                        + "4. Cover and simmer for 20 minutes until the rice is cooked.",
                "400|g|chicken", "250|g|rice", "1|piece|onion",
                "1|piece|stock cube", "2|tbsp|cooking oil");

        insertRecipe(db, "Scrambled Eggs on Toast", "Breakfast", 10, 2,
                "1. Toast the bread.\n"
                        + "2. Melt half the butter in a pan on low heat.\n"
                        + "3. Add the beaten eggs and stir gently until just set.\n"
                        + "4. Butter the toast and pile the eggs on top.",
                "3|piece|egg", "2|slice|bread", "15|g|butter", "1|pinch|salt");

        insertRecipe(db, "Quick Bean Curry", "Main", 30, 4,
                "1. Fry the onion in the oil until golden.\n"
                        + "2. Stir in the curry powder and cook for 1 minute.\n"
                        + "3. Add the chopped tomatoes and simmer for 10 minutes.\n"
                        + "4. Add the drained beans and heat through.",
                "400|g|beans", "1|piece|onion", "2|piece|tomato",
                "1|tbsp|curry powder", "2|tbsp|cooking oil");

        insertRecipe(db, "Garlic Butter Pasta", "Main", 20, 2,
                "1. Cook the pasta in salted water until soft.\n"
                        + "2. Melt the butter and fry the sliced garlic gently.\n"
                        + "3. Toss the drained pasta in the garlic butter.",
                "250|g|pasta", "50|g|butter", "3|clove|garlic", "1|tsp|salt");

        insertRecipe(db, "Banana Oat Smoothie", "Drink", 5, 2,
                "1. Peel the bananas and break them into pieces.\n"
                        + "2. Blend the banana, oats, milk and sugar until smooth.\n"
                        + "3. Pour into glasses and drink straight away.",
                "2|piece|banana", "40|g|oats", "250|ml|milk", "1|tbsp|sugar");

        insertRecipe(db, "Oven Potato Wedges", "Side", 40, 3,
                "1. Heat the oven to 200 degrees.\n"
                        + "2. Cut the potatoes into wedges and toss them in the oil.\n"
                        + "3. Sprinkle over the salt and paprika.\n"
                        + "4. Bake for 30 minutes, turning them once.",
                "500|g|potato", "3|tbsp|cooking oil", "1|tsp|salt", "1|tsp|paprika");

        insertRecipe(db, "Tuna Mayo Sandwich", "Lunch", 10, 2,
                "1. Drain the tuna and flake it into a bowl.\n"
                        + "2. Mix in the mayonnaise and the finely chopped onion.\n"
                        + "3. Spread it over two slices of bread and top with the others.",
                "4|slice|bread", "1|can|tuna", "1|piece|onion", "2|tbsp|mayonnaise");

        insertRecipe(db, "Cheesy Beans on Toast", "Lunch", 12, 2,
                "1. Heat the baked beans in a small pot.\n"
                        + "2. Toast the bread and butter it.\n"
                        + "3. Spoon the beans over the toast and scatter the cheese on top.\n"
                        + "4. Grill for 2 minutes until the cheese melts.",
                "400|g|baked beans", "2|slice|bread", "40|g|cheese", "10|g|butter");

        insertRecipe(db, "Carrot and Ginger Soup", "Soup", 35, 4,
                "1. Fry the chopped onion and grated ginger for 3 minutes.\n"
                        + "2. Add the sliced carrots, the stock cube and 750 ml of water.\n"
                        + "3. Simmer for 25 minutes then blend until smooth.",
                "500|g|carrot", "1|piece|onion", "10|g|ginger", "1|piece|stock cube");

        insertRecipe(db, "Potato and Onion Omelette", "Main", 30, 3,
                "1. Dice the potatoes and fry them slowly in the oil until soft.\n"
                        + "2. Add the sliced onion and cook until golden.\n"
                        + "3. Pour in the beaten, salted eggs and cook on low heat.\n"
                        + "4. Flip it once and cook until set all the way through.",
                "300|g|potato", "4|piece|egg", "1|piece|onion",
                "3|tbsp|cooking oil", "1|tsp|salt");

        insertRecipe(db, "Peanut Butter Oat Bites", "Snack", 15, 6,
                "1. Mix the oats, peanut butter and sugar into a stiff paste.\n"
                        + "2. Roll the mixture into small balls.\n"
                        + "3. Chill for 10 minutes before eating.",
                "150|g|oats", "120|g|peanut butter", "2|tbsp|sugar");
    }

    // Saves one recipe and then each of its ingredient lines
    private static void insertRecipe(SQLiteDatabase db, String name, String category,
                                     int minutes, int servings, String steps,
                                     String... ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(RecipeTable.RECIPE_NAME, name);
        recipeValues.put(RecipeTable.CATEGORY, category);
        recipeValues.put(RecipeTable.MINUTES, minutes);
        recipeValues.put(RecipeTable.SERVINGS, servings);
        recipeValues.put(RecipeTable.STEPS, steps);

        long recipeId = db.insert(RecipeTable.NAME, null, recipeValues);
        if (recipeId == -1) {
            return;
        }

        for (String line : ingredients) {
            String[] parts = line.split("\\|");
            if (parts.length != 3) {
                continue;
            }
            ContentValues values = new ContentValues();
            values.put(RecipeIngredientTable.RECIPE_ID, recipeId);
            values.put(RecipeIngredientTable.QUANTITY, Double.parseDouble(parts[0]));
            values.put(RecipeIngredientTable.UNIT, parts[1]);
            values.put(RecipeIngredientTable.ITEM_NAME, parts[2]);
            values.put(RecipeIngredientTable.NAME_KEY,
                    IngredientNormalizer.normalize(parts[2]));
            db.insert(RecipeIngredientTable.NAME, null, values);
        }
    }
}
