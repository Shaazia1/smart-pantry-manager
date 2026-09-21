package com.richfield.smartpantry;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.richfield.smartpantry.ui.PantryListFragment;
import com.richfield.smartpantry.ui.SuggestedRecipesFragment;

// Holds the screens. The toolbar button swaps between the pantry and the
// suggested recipes for now, the bottom navigation bar comes next.
public class MainActivity extends AppCompatActivity {

    private static final String STATE_SHOWING_RECIPES = "showing_recipes";

    private MaterialToolbar toolbar;
    private boolean showingRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_switch_screen) {
                    showScreen(!showingRecipes);
                    return true;
                }
                return false;
            }
        });

        if (savedInstanceState != null) {
            showingRecipes = savedInstanceState.getBoolean(STATE_SHOWING_RECIPES, false);
        }
        showScreen(showingRecipes);
    }

    // remembers which screen was open when the phone is rotated
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SHOWING_RECIPES, showingRecipes);
    }

    private void showScreen(boolean recipes) {
        showingRecipes = recipes;

        Fragment fragment = recipes ? new SuggestedRecipesFragment() : new PantryListFragment();
        toolbar.setTitle(recipes ? R.string.title_suggested : R.string.title_pantry);
        toolbar.getMenu().findItem(R.id.action_switch_screen)
                .setIcon(recipes ? R.drawable.ic_kitchen_white : R.drawable.ic_restaurant);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
