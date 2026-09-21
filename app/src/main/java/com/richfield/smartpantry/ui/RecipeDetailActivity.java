package com.richfield.smartpantry.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.richfield.smartpantry.R;
import com.richfield.smartpantry.db.PantryRepository;
import com.richfield.smartpantry.db.RecipeRepository;
import com.richfield.smartpantry.logic.IngredientNormalizer;
import com.richfield.smartpantry.logic.RecipeMatcher;
import com.richfield.smartpantry.model.Ingredient;
import com.richfield.smartpantry.model.MatchResult;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.util.TextFormat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Shows one recipe with everything it needs and how to make it. The recipe id
// comes in through the intent. Each ingredient gets a tick if it is in the
// pantry and a cross if it isn't, so the matching rule is visible to the user.
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = new RecipeRepository(this).getById(recipeId);
        if (recipe == null) {
            Toast.makeText(this, R.string.error_item_missing, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<PantryItem> pantry = new PantryRepository(this).getAll();
        MatchResult result = RecipeMatcher.match(recipe, pantry);

        showRecipe(recipe, result);
    }

    private void showRecipe(Recipe recipe, MatchResult result) {
        ((TextView) findViewById(R.id.textName)).setText(recipe.getName());
        ((TextView) findViewById(R.id.textMeta)).setText(getString(R.string.recipe_meta,
                recipe.getIngredients().size(), recipe.getMinutes(), recipe.getServings()));

        TextView badge = findViewById(R.id.textBadge);
        if (result.isCookable()) {
            badge.setText(R.string.can_cook);
            badge.setBackgroundResource(R.drawable.bg_chip_green);
            badge.setTextColor(ContextCompat.getColor(this, R.color.green_700));
        } else {
            badge.setText(getString(R.string.badge_missing, result.getMissingCount()));
            badge.setBackgroundResource(R.drawable.bg_chip_amber);
            badge.setTextColor(ContextCompat.getColor(this, R.color.amber_600));
        }

        showIngredients(recipe, result);
        showSteps(recipe);
    }

    // The steps are stored as one block of text with a line per step, so they get
    // split up and given a numbered circle each. Much easier to follow while
    // actually cooking than one paragraph.
    private void showSteps(Recipe recipe) {
        LinearLayout container = findViewById(R.id.containerSteps);
        container.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        int number = 1;
        for (String line : recipe.getSteps().split("\n")) {
            String step = line.trim();
            if (step.isEmpty()) {
                continue;
            }
            // the seeded text already starts with "1. ", drop it and use our own
            step = step.replaceFirst("^\\d+\\.\\s*", "");

            View row = inflater.inflate(R.layout.item_recipe_step, container, false);
            ((TextView) row.findViewById(R.id.textStepNumber)).setText(String.valueOf(number));
            ((TextView) row.findViewById(R.id.textStepText)).setText(step);
            container.addView(row);
            number++;
        }
    }

    // Builds one row per ingredient. A RecyclerView isn't needed here because the
    // list is short and already inside a ScrollView.
    private void showIngredients(Recipe recipe, MatchResult result) {
        LinearLayout container = findViewById(R.id.containerIngredients);
        container.removeAllViews();

        Set<String> missingKeys = new HashSet<>();
        for (Ingredient missing : result.getMissing()) {
            missingKeys.add(IngredientNormalizer.normalize(missing.getName()));
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Ingredient ingredient : recipe.getIngredients()) {
            View row = inflater.inflate(R.layout.item_recipe_ingredient, container, false);
            boolean inPantry = !missingKeys.contains(
                    IngredientNormalizer.normalize(ingredient.getName()));

            TextView name = row.findViewById(R.id.textIngredient);
            name.setText(ingredient.getName());
            name.setTextColor(ContextCompat.getColor(this,
                    inPantry ? R.color.text_primary : R.color.text_secondary));

            ((TextView) row.findViewById(R.id.textAmount)).setText(
                    TextFormat.amount(ingredient.getQuantity(), ingredient.getUnit()));

            ((ImageView) row.findViewById(R.id.imageHave)).setImageResource(
                    inPantry ? R.drawable.ic_check : R.drawable.ic_close);

            container.addView(row);
        }
    }
}
