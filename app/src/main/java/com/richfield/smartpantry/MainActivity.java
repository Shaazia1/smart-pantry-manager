package com.richfield.smartpantry;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.richfield.smartpantry.db.DatabaseHelper;

// Main screen of the app. For now it only shows a placeholder layout.
// Later this will hold the bottom navigation and swap the fragments.
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // temporary, just to check the database file gets created
        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        Log.d("MainActivity", "database ready: " + helper.getReadableDatabase().getPath());
    }
}
