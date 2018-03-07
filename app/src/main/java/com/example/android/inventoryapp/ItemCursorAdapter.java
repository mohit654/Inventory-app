package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

/**
 * Created by user on 27-06-2017.
 */

public class ItemCursorAdapter extends CursorAdapter {
    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameText = (TextView) view.findViewById(R.id.name);
        TextView priceText = (TextView) view.findViewById(R.id.price);
        TextView quantityText = (TextView) view.findViewById(R.id.quantity);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        Button soldButton = (Button) view.findViewById(R.id.sold);

        int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRODUCT_QUANTITY);
        int imageColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRODUCT_IMAGE);
        int idColumnIndex = cursor.getColumnIndex(ItemEntry._ID);

        final String name = cursor.getString(nameColumnIndex);
        final int price = cursor.getInt(priceColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        final int id = cursor.getInt(idColumnIndex);
        String image = cursor.getString(imageColumnIndex);
        if (quantity!=0){
            soldButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues values = new ContentValues();
                    values.put(ItemEntry.COLUMN_PRODUCT_QUANTITY,quantity-1);
                    Uri uri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI,id);
                    context.getContentResolver().update(uri,values,null,null);
                }
            });
        }
        String priceTxt = "Rs. "+String.valueOf(price);
        String quantityTxt = "Unavailable";
        if (quantity!=0){
            quantityTxt = String.valueOf(quantity)+" pcs available";
        }
        nameText.setText(name);
        priceText.setText(priceTxt);
        quantityText.setText(quantityTxt);
        imageView.setImageURI(Uri.parse(String.valueOf(image)));
    }
}
