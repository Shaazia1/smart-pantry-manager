package com.richfield.smartpantry;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.richfield.smartpantry.ui.PantryListFragment;
import com.richfield.smartpantry.ui.SettingsFragment;
import com.richfield.smartpantry.ui.SuggestedRecipesFragment;

// Holds the three main screens. The bottom navigation bar picks which fragment
// sits in the container and the toolbar title changes with it.
public class MainActivity extends AppCompatActivity {

    private static final String STATE_SELECTED_TAB = "selected_tab";

    private MaterialToolbar toolbar;
    private int selectedTab = R.id.nav_pantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // keeps the user on the same tab when the phone is rotated
        if (savedInstanceState != null) {
            selectedTab = savedInstanceState.getInt(STATE_SELECTED_TAB, R.id.nav_pantry);
        }

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectedTab = item.getItemId();
                showFragment(selectedTab);
                return true;
            }
        });

        bottomNav.setSelectedItemId(selectedTab);
        showFragment(selectedTab);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_TAB, selectedTab);
    }

    private void showFragment(int itemId) {
        Fragment fragment;
        int title;

        if (itemId == R.id.nav_recipes) {
            fragment = new SuggestedRecipesFragment();
            title = R.string.title_suggested;
        } else if (itemId == R.id.nav_settings) {
            fragment = new SettingsFragment();
            title = R.string.title_settings;
        } else {
            fragment = new PantryListFragment();
            title = R.string.title_pantry;
        }

        toolbar.setTitle(title);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
