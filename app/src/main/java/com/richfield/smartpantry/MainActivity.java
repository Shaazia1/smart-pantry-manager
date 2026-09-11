package com.richfield.smartpantry;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

// Main screen of the app. For now it only shows a placeholder layout.
// Later this will hold the bottom navigation and swap the fragments.
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
