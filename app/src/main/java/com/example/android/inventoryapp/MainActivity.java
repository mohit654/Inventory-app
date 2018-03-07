package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private ItemCursorAdapter madapter;

    private static final int ITEM_LOADER = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                startActivity(intent);
            }
        });
        ListView itemListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);
        madapter = new ItemCursorAdapter(this,null);
        itemListView.setAdapter(madapter);
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                Uri currentUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI,id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(ITEM_LOADER,null,this);
    }
    private void insertProduct(){
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_PRODUCT_NAME,"Pencil");
        values.put(ItemEntry.COLUMN_PRODUCT_PRICE,5);
        values.put(ItemEntry.COLUMN_PRODUCT_QUANTITY,10);
        values.put(ItemEntry.COLUMN_PRODUCT_SUPPLIER,"Jai Stationary");
        Uri uri1 = Uri.parse("android.resource://com.example.android.inventoryapp/drawable/pencil");
        values.put(ItemEntry.COLUMN_PRODUCT_IMAGE,uri1.toString());
        Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI,values);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.insert_dummy:
                insertProduct();
                return true;
            case R.id.delete_all_data:
                showConfirmationDialogBox();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showConfirmationDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_data);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog!=null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDailog = builder.create();
        alertDailog.show();
    }
    private void deleteProduct(){
        int rowsDeleted = getContentResolver().delete(ItemEntry.CONTENT_URI,null,null);
        if (rowsDeleted == 0){
            Toast.makeText(this,R.string.editor_delete_product_failed,Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this,R.string.editor_delete_product_successful,Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection= {
                ItemEntry._ID,
                ItemEntry.COLUMN_PRODUCT_NAME,
                ItemEntry.COLUMN_PRODUCT_PRICE,
                ItemEntry.COLUMN_PRODUCT_QUANTITY,
                ItemEntry.COLUMN_PRODUCT_IMAGE
        };
        return new CursorLoader(this,
                ItemEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        madapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        madapter.swapCursor(null);
    }
}
