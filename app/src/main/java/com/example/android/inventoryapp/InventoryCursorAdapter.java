package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.android.inventoryapp.data.InventoryDbHelper;

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
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView)view.findViewById(R.id.quantity);

        int nameColumnIndex =cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_ITEM_PRICE);

        String itemName = cursor.getString(nameColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);

        nameTextView.setText(itemName);
        summaryTextView.setText(itemPrice);
    }
}
