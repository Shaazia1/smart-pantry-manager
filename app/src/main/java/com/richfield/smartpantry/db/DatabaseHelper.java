package com.richfield.smartpantry.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.richfield.smartpantry.data.RecipeSeeder;

// Creates the SQLite database file and the three tables.
// The whole app shares one helper so we don't open the database twice.
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    public static final String DATABASE_NAME = "smart_pantry.db";
    public static final int DATABASE_VERSION = 1;

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            // application context, otherwise an Activity could be leaked
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Only runs the first time the app is opened after installing
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "creating database " + DATABASE_NAME);
        db.execSQL(PantryContract.PantryTable.CREATE_SQL);
        db.execSQL(PantryContract.RecipeTable.CREATE_SQL);
        db.execSQL(PantryContract.RecipeIngredientTable.CREATE_SQL);

        // load the built in recipes so the suggestions screen has something to work with
        RecipeSeeder.seed(db);
    }

    // Runs when DATABASE_VERSION goes up. Nothing worth keeping yet so the
    // tables are just dropped and made again.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "upgrading database from " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + PantryContract.RecipeIngredientTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PantryContract.RecipeTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PantryContract.PantryTable.NAME);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // needed for the ON DELETE CASCADE on recipe_ingredients
        db.setForeignKeyConstraintsEnabled(true);
    }
}
