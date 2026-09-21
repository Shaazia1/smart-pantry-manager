package com.richfield.smartpantry.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.richfield.smartpantry.data.RecipeSeeder;
import com.richfield.smartpantry.db.PantryContract.PantryTable;
import com.richfield.smartpantry.logic.IngredientNormalizer;

// Creates the SQLite database file and the three tables.
// The whole app shares one helper so we don't open the database twice.
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    public static final String DATABASE_NAME = "smart_pantry.db";

    // version 2: name keys are built by the normaliser instead of just lower casing
    public static final int DATABASE_VERSION = 2;

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

    // Runs when DATABASE_VERSION goes up.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "upgrading database from " + oldVersion + " to " + newVersion);

        if (oldVersion < 2) {
            // the recipes are built into the app, so they can just be rebuilt
            db.execSQL("DROP TABLE IF EXISTS " + PantryContract.RecipeIngredientTable.NAME);
            db.execSQL("DROP TABLE IF EXISTS " + PantryContract.RecipeTable.NAME);
            db.execSQL(PantryContract.RecipeTable.CREATE_SQL);
            db.execSQL(PantryContract.RecipeIngredientTable.CREATE_SQL);
            RecipeSeeder.seed(db);

            // the pantry belongs to the user, so it is kept and only the
            // matching keys are worked out again
            rebuildPantryNameKeys(db);
        }
    }

    // Reads every pantry row and writes its name key again using the normaliser.
    private void rebuildPantryNameKeys(SQLiteDatabase db) {
        Cursor cursor = db.query(PantryTable.NAME,
                new String[]{PantryTable.ID, PantryTable.ITEM_NAME},
                null, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String name = cursor.getString(1);

                ContentValues values = new ContentValues();
                values.put(PantryTable.NAME_KEY, IngredientNormalizer.normalize(name));
                db.update(PantryTable.NAME, values,
                        PantryTable.ID + " = ?", new String[]{String.valueOf(id)});
            }
        } finally {
            cursor.close();
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // needed for the ON DELETE CASCADE on recipe_ingredients
        db.setForeignKeyConstraintsEnabled(true);
    }
}
