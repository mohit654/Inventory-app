package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 27-06-2017.
 */

public class ItemDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "storage.db";
    private static final int DATABASE_VERSION = 1;

    public ItemDbHelper(Context context) {
        super(context,DATABASE_NAME ,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE "+ ItemContract.ItemEntry.TABLE_NAME+"("
                + ItemContract.ItemEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemContract.ItemEntry.COLUMN_PRODUCT_NAME+" TEXT NOT NULL, "
                + ItemContract.ItemEntry.COLUMN_PRODUCT_PRICE+" INTEGER NOT NULL, "
                + ItemContract.ItemEntry.COLUMN_PRODUCT_QUANTITY+" INTEGER NOT NULL DEFAULT 0, "
                + ItemContract.ItemEntry.COLUMN_PRODUCT_SUPPLIER+" TEXT, "
                + ItemContract.ItemEntry.COLUMN_PRODUCT_IMAGE+" TEXT); ";
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
