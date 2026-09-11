package com.richfield.smartpantry;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.richfield.smartpantry.db.RecipeRepository;
import com.richfield.smartpantry.model.Recipe;

import java.util.List;

// Main screen of the app. For now it only shows a placeholder layout.
// Later this will hold the bottom navigation and swap the fragments.
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // temporary check that the recipes actually got saved
        List<Recipe> recipes = new RecipeRepository(this).getAllWithIngredients();
        Log.d("MainActivity", "recipes in database: " + recipes.size());
        for (Recipe recipe : recipes) {
            Log.d("MainActivity", recipe.getName() + " needs "
                    + recipe.getIngredients().size() + " ingredients");
        }
    }
}
