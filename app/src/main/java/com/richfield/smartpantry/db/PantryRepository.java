package com.richfield.smartpantry.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.richfield.smartpantry.db.PantryContract.PantryTable;
import com.richfield.smartpantry.model.PantryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// All the database work for the pantry table lives here, so the screens never
// have to deal with a Cursor themselves.
public class PantryRepository {

    private final DatabaseHelper helper;

    public PantryRepository(Context context) {
        this.helper = DatabaseHelper.getInstance(context);
    }

    // CREATE
    public long insert(PantryItem item) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long id = db.insert(PantryTable.NAME, null, toValues(item));
        item.setId(id);
        return id;
    }

    // UPDATE, returns how many rows changed
    public int update(PantryItem item) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.update(PantryTable.NAME, toValues(item),
                PantryTable.ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    // DELETE, returns how many rows were removed
    public int delete(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(PantryTable.NAME,
                PantryTable.ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // READ, everything in the pantry sorted by name
    public List<PantryItem> getAll() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(PantryTable.NAME, null, null, null, null, null,
                PantryTable.ITEM_NAME + " COLLATE NOCASE ASC");
        try {
            while (cursor.moveToNext()) {
                items.add(fromCursor(cursor));
            }
        } finally {
            cursor.close();
        }
        return items;
    }

    // READ one item, null if it isn't there any more
    public PantryItem getById(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(PantryTable.NAME, null,
                PantryTable.ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);
        try {
            return cursor.moveToFirst() ? fromCursor(cursor) : null;
        } finally {
            cursor.close();
        }
    }

    public int count() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + PantryTable.NAME, null);
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    private ContentValues toValues(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put(PantryTable.ITEM_NAME, item.getName());
        // lower case for now, the proper name cleaner replaces this later
        values.put(PantryTable.NAME_KEY, item.getName().toLowerCase(Locale.ROOT).trim());
        values.put(PantryTable.QUANTITY, item.getQuantity());
        values.put(PantryTable.UNIT, item.getUnit() == null ? "" : item.getUnit());
        values.put(PantryTable.EXPIRY_DATE, item.getExpiryDate());
        return values;
    }

    // Turns the row the cursor is sitting on into a PantryItem
    private PantryItem fromCursor(Cursor cursor) {
        PantryItem item = new PantryItem();
        item.setId(cursor.getLong(cursor.getColumnIndexOrThrow(PantryTable.ID)));
        item.setName(cursor.getString(cursor.getColumnIndexOrThrow(PantryTable.ITEM_NAME)));
        item.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(PantryTable.QUANTITY)));
        item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(PantryTable.UNIT)));

        int expiryColumn = cursor.getColumnIndexOrThrow(PantryTable.EXPIRY_DATE);
        item.setExpiryDate(cursor.isNull(expiryColumn) ? null : cursor.getString(expiryColumn));
        return item;
    }
}
