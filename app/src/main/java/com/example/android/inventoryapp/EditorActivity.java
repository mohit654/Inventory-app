package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText mNameEditText;

    private EditText mPriceEditText;

    private EditText mQuantityEditText;

    private EditText mSupplierEditText;

    private TextView mQuantityTextView;

    private Button addButton;

    private Button subButton;

    private LinearLayout layout;

    private static final int SELECT_PICTURE = 1;

    private ImageView mImageView;

    private String image;

    private Uri currentUri;


    private static final int EXISTING_PRODUCT = 1;

    private boolean mProductChange = false;

    private View.OnTouchListener mTouchListner = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductChange = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        layout = (LinearLayout) findViewById(R.id.sample);
        mQuantityEditText = (EditText) findViewById(R.id.quantity_text);

        Intent intent = getIntent();
        currentUri = intent.getData();
        if (currentUri == null){
            setTitle(R.string.add_data);
            invalidateOptionsMenu();
            layout.setVisibility(View.GONE);
        }
        else {
            setTitle(R.string.edit_data);
            getLoaderManager().initLoader(EXISTING_PRODUCT,null,this);
            mQuantityEditText.setVisibility(View.GONE);
        }
        mNameEditText = (EditText) findViewById(R.id.name_text);
        mPriceEditText = (EditText) findViewById(R.id.price_text);
        mSupplierEditText = (EditText) findViewById(R.id.supplier_text);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mQuantityTextView = (TextView) findViewById(R.id.quantity_view);
        addButton = (Button) findViewById(R.id.add);
        subButton = (Button) findViewById(R.id.sub);

        Button mButton = (Button) findViewById(R.id.browse_image);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_PICTURE);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(mQuantityTextView.getText().toString());
                quantity = quantity+1;
                mQuantityTextView.setText(String.valueOf(quantity));
            }
        });
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(mQuantityTextView.getText().toString());
                if (quantity>0){
                    quantity= quantity-1;
                }
                mQuantityTextView.setText(String.valueOf(quantity));
            }
        });


        mNameEditText.setOnTouchListener(mTouchListner);
        mPriceEditText.setOnTouchListener(mTouchListner);
        mQuantityEditText.setOnTouchListener(mTouchListner);
        mSupplierEditText.setOnTouchListener(mTouchListner);
        mButton.setOnTouchListener(mTouchListner);
        addButton.setOnTouchListener(mTouchListner);
        subButton.setOnTouchListener(mTouchListner);
    }
    @Override
    public void onActivityResult(int reqCode,int resCode,Intent intent)
    {
        if (reqCode == SELECT_PICTURE && resCode == RESULT_OK)
        {
            image = intent.getData().toString();
            mImageView.setImageURI(Uri.parse(String.valueOf(image)));
        }
        super.onActivityResult(reqCode,resCode,intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void saveProduct(){
        String name = mNameEditText.getText().toString().trim();
        String price = mPriceEditText.getText().toString().trim();
        String quantity;
        if (currentUri==null){
            quantity = mQuantityEditText.getText().toString().trim();
        }
        else {
            quantity = mQuantityTextView.getText().toString().trim();
        }
        String supplier = mSupplierEditText.getText().toString().trim();
        if (image!=null)
        {
            image = image.toString();
        }
        if (currentUri == null&&TextUtils.isEmpty(name)&&TextUtils.isEmpty(price)&&TextUtils.isEmpty(quantity)&&TextUtils.isEmpty(supplier)){
            return;
        }
        if (TextUtils.isEmpty(name)&&TextUtils.isEmpty(price)){
            Toast.makeText(this,"Require certain field",Toast.LENGTH_SHORT).show();
            return;
        }
        int prices = Integer.parseInt(price);
        int quantities;
        if (currentUri==null){
            if (!TextUtils.isEmpty(quantity)){
                quantities = Integer.parseInt(quantity);
            }
            else {
                quantities=0;
            }
        }
        else {
            quantities = Integer.parseInt(quantity);
        }
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_PRODUCT_NAME,name);
        values.put(ItemEntry.COLUMN_PRODUCT_PRICE,prices);
        values.put(ItemEntry.COLUMN_PRODUCT_QUANTITY,quantities);
        values.put(ItemEntry.COLUMN_PRODUCT_SUPPLIER,supplier);
        values.put(ItemEntry.COLUMN_PRODUCT_IMAGE,image);
        if (currentUri == null){
            Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if (!mProductChange){
                return;
            }
            int rowsUpdated = getContentResolver().update(currentUri,values,null,null);
            if (rowsUpdated == 0){
                Toast.makeText(this,R.string.editor_update_product_failed,Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this,R.string.editor_update_product_successful,Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.order:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                showConfirmationDialogBox();
                return true;
            case android.R.id.home:
                if (!mProductChange) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard,discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing,new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog!=null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showConfirmationDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_data);
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
        if (currentUri!=null){
            int rowsDeleted = getContentResolver().delete(currentUri,null,null);
            if (rowsDeleted == 0){
                Toast.makeText(this,R.string.editor_delete_product_failed,Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this,R.string.editor_delete_product_successful,Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
    @Override
    public void onBackPressed() {
        if (!mProductChange){
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_PRODUCT_NAME,
                ItemEntry.COLUMN_PRODUCT_PRICE,
                ItemEntry.COLUMN_PRODUCT_QUANTITY,
                ItemEntry.COLUMN_PRODUCT_SUPPLIER,
                ItemEntry.COLUMN_PRODUCT_IMAGE
        };
        return new CursorLoader(
                this,
                currentUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRODUCT_SUPPLIER);
            int imageColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRODUCT_IMAGE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            image = cursor.getString(imageColumnIndex);
            //For each of the textViews I’ll set the proper text.

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mSupplierEditText.setText(supplier);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityTextView.setText(Integer.toString(quantity));
            if(image.startsWith("content://media/external/image")){
                mImageView.setImageURI(Uri.parse(image));
            }
            else {
                mImageView.setImageResource(R.drawable.image);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
       // mImageView.setImageResource(R.drawable.noimage);
    }
}
