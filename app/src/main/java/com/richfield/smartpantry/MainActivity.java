package com.richfield.smartpantry;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.richfield.smartpantry.ui.PantryListFragment;

// Holds the screens. For now it just shows the pantry list, the bottom
// navigation and the other tabs get added later.
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // only add the fragment the first time, otherwise it gets added again
        // on top of itself when the screen is rotated
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new PantryListFragment())
                    .commit();
        }
    }
}
