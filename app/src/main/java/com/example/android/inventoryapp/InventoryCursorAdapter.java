package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.android.inventoryapp.data.InventoryDbHelper;
import com.example.android.inventoryapp.data.InventoryProvider;

import static com.example.android.inventoryapp.R.id.sellButton;

/**
 * Created by Niamh on 25/07/2017.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.quantity);
        Button sellButton = (Button) view.findViewById(R.id.sellButton);

        int itemIdColumnIndex = cursor.getColumnIndexOrThrow(InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_ITEM_QUANTITY);

        final int itemId = cursor.getInt(itemIdColumnIndex);
        final String itemName = cursor.getString(nameColumnIndex);
        final String itemPrice = cursor.getString(priceColumnIndex);
        final int itemQuantity = Integer.parseInt(cursor.getString(quantityColumnIndex));

        nameTextView.setText(itemName);
        summaryTextView.setText(itemPrice);

        sellButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                int updatedQuantity = (itemQuantity - 1);

                if (updatedQuantity < 0) {
                    return;
                }
                Uri currentItemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, itemId);
                Log.i("Adapter", "Uri we are updating: " + currentItemUri);
                ContentValues values = new ContentValues();
                values.put(InventoryEntry.COLUMN_ITEM_QUANTITY, Integer.toString(updatedQuantity));
                context.getContentResolver().update(currentItemUri, values, null, null);
            }
        });
    }
}
