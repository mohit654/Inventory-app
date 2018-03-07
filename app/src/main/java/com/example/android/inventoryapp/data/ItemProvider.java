package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by user on 27-06-2017.
 */

public class ItemProvider extends ContentProvider {
    private ItemDbHelper mDbHelper;
    public static final String LOG_TAG = ItemProvider.class.getSimpleName();
    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY,ItemContract.PATH_ITEM,ITEMS);
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY,ItemContract.PATH_ITEM+"/#",ITEM_ID);
    }
    @Override
    public boolean onCreate() {
        mDbHelper = new ItemDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case ITEMS:
                cursor = db.query(ItemContract.ItemEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case ITEM_ID:
                selection = ItemContract.ItemEntry._ID+"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ItemContract.ItemEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown ure "+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ITEMS:
                return ItemContract.ItemEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return ItemContract.ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ITEMS:
                return insertPet(uri,values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for "+uri);
        }
    }
    private Uri insertPet(Uri uri,ContentValues values){
        String name = values.getAsString(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME);
        if (name == null){
            throw new IllegalArgumentException("Product requires a name");
        }
        Integer price = values.getAsInteger(ItemContract.ItemEntry.COLUMN_PRODUCT_PRICE);
        if (price == null){
            throw new IllegalArgumentException("Product requires a price");
        }
        Integer quantity = values.getAsInteger(ItemContract.ItemEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity!=null &&quantity<0){
            throw new IllegalArgumentException("Product requires valid quantity");
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(ItemContract.ItemEntry.TABLE_NAME,null,values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);
    }
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match){
            case ITEMS:
                rowsDeleted = db.delete(ItemContract.ItemEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case ITEM_ID:
                selection = ItemContract.ItemEntry._ID+"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(ItemContract.ItemEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for "+uri);
        }
        if (rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ITEMS:
                return updatePet(uri,values,selection,selectionArgs);
            case ITEM_ID:
                selection = ItemContract.ItemEntry._ID+"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Updation is not supported for "+uri);
        }
    }
    private int updatePet(Uri uri,ContentValues values,String selection,String[] selectionArgs){
        if (values.containsKey(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(ItemContract.ItemEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(ItemContract.ItemEntry.COLUMN_PRODUCT_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Product requires a price");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(ItemContract.ItemEntry.COLUMN_PRODUCT_QUANTITY)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer quantity = values.getAsInteger(ItemContract.ItemEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }

        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ItemContract.ItemEntry.TABLE_NAME, values, selection, selectionArgs);

        // Returns the number of database rows affected by the update statement
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
