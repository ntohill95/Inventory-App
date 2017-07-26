package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.android.inventoryapp.data.InventoryDbHelper;
import java.security.Provider;

/**
 * Created by Niamh on 25/07/2017.
 */

public class InventoryProvider extends ContentProvider {

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private static final int ITEMS = 100;

    private static final int ITEM_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_ITEMS + "/#", ITEM_ID);
    }
    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    private Uri insertItem(Uri uri, ContentValues values) {

        if (values.containsKey(InventoryEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_ITEM_PRICE)) {
            String price = values.getAsString(InventoryEntry.COLUMN_ITEM_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Item requires a price");
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_ITEM_QUANTITY)) {
            String quantity = values.getAsString(InventoryEntry.COLUMN_ITEM_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Item requires a quantity");
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.size() == 0) {
            Log.i(LOG_TAG,"No values, nothing to update");
            return 0;
        }

        if (values.containsKey(InventoryEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_ITEM_PRICE)) {
            String price = values.getAsString(InventoryEntry.COLUMN_ITEM_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Item requires a price");
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_ITEM_QUANTITY)) {
            int quantity = values.getAsInteger(InventoryEntry.COLUMN_ITEM_QUANTITY);
            if (quantity < 0) {
                throw new IllegalArgumentException("Item requires a valid quantity");
            }
        }


        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            Log.i(LOG_TAG,"Successfully updated item");
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
