package com.example.marcoliva.inventoryapp;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.marcoliva.inventoryapp.data.ProductContract;
import com.example.marcoliva.inventoryapp.data.ProductDbHelper;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by ThinkSoft on 1/11/2017.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private TextInputEditText mNameEditText;
    private TextInputEditText mStockEditText;
    private TextInputEditText mPriceEditText;
    private Button mChooseImageButton, mDecreaseStockButton, mIncreaseStockButton;
    private ImageView mProductImageView;

    private ProductDbHelper mDbHelper;
    private Uri currentUri;
    private Uri uriImage;
    private ContentValues values;

    final int REQUEST_CODE_GALLERY = 999;

    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        init();
        currentUri = getIntent().getData();
        if (currentUri == null) {
            setTitle("Add a Product");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Product");
            getLoaderManager().initLoader(0, null, this);
        }


        mChooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        EditorActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });
        mDecreaseStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stockValue = Integer.valueOf(mStockEditText.getText().toString().trim());
                stockValue--;
                if (stockValue >= 0) {
                    mStockEditText.setText(String.valueOf(stockValue));
                }
            }
        });
        mIncreaseStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stockValue = Integer.valueOf(mStockEditText.getText().toString().trim());
                stockValue++;
                mStockEditText.setText(String.valueOf(stockValue));
            }
        });

        mDbHelper = new ProductDbHelper(this);

    }

    private void init() {
        mNameEditText = findViewById(R.id.name_edit_text);
        mStockEditText = findViewById(R.id.stock_edit_text);
        mPriceEditText = findViewById(R.id.price_edit_text);
        mChooseImageButton = findViewById(R.id.choose_image_button);
        mProductImageView = findViewById(R.id.edit_image_product);
        mDecreaseStockButton = findViewById(R.id.decrease_stock_button);
        mIncreaseStockButton = findViewById(R.id.increase_stock_button);

        //Add listeners to the views
        mNameEditText.setOnTouchListener(mTouchListener);
        mStockEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mChooseImageButton.setOnTouchListener(mTouchListener);
        mDecreaseStockButton.setOnTouchListener(mTouchListener);
        mIncreaseStockButton.setOnTouchListener(mTouchListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_permission_gallery), Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            uriImage = data.getData();
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(uriImage);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if(bitmap.getWidth()<=128 && bitmap.getHeight()<=128){
                    mProductImageView.setImageURI(uriImage);
                }else{
                    Toast.makeText(this, getString(R.string.big_image_msg), Toast.LENGTH_SHORT).show();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }




        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //if this is a new product , hide the "Delete" menu item
        if (currentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if(!mProductHasChanged){
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    private void saveProduct() {
        String nameString = mNameEditText.getText().toString().trim();
        int stockInteger = Integer.valueOf(mStockEditText.getText().toString().trim());
        int priceInteger = Integer.valueOf(mPriceEditText.getText().toString().trim());
        String imageUri = null;
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.empty_name), Toast.LENGTH_LONG).show();
            return;
        }

        if (stockInteger < 0) {
            Toast.makeText(this, getString(R.string.negative_stock), Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(String.valueOf(stockInteger))) {
            Toast.makeText(this, getString(R.string.empty_stock), Toast.LENGTH_LONG).show();
            return;
        }

        if (priceInteger < 0) {
            Toast.makeText(this, getString(R.string.negative_price), Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(String.valueOf(priceInteger))) {
            Toast.makeText(this, getString(R.string.empty_price), Toast.LENGTH_LONG).show();
            return;
        }

        if (currentUri == null) {
            if (uriImage == null) {
                imageUri = "@mipmap/ic_empty_image_product";
            } else {
                imageUri = String.valueOf(uriImage);
            }

        }else{
            imageUri = String.valueOf(uriImage);
        }

        values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK, stockInteger);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, priceInteger);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE, imageUri);

        Uri newUri;
        int rowsUpdate = -1;
        if (currentUri == null) {
            newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.error_save_product), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.save_product), Toast.LENGTH_SHORT).show();
            }

        } else {
            if(mProductHasChanged){
                rowsUpdate = getContentResolver().update(currentUri, values, null, null);
                if (rowsUpdate != -1) {
                    Toast.makeText(this, getString(R.string.update_product), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.error_update_product), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void deleteProduct() {
        int rowsDelete = -1;
        if (currentUri != null) {
            rowsDelete = getContentResolver().delete(currentUri, null, null);
            if (rowsDelete == -1) {
                Toast.makeText(this, getString(R.string.error_delete_product), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_product), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE
        };
        return new CursorLoader(this, currentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int stockColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK);
            int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE);

            String nameProduct = cursor.getString(nameColumnIndex);
            Integer stockProduct = cursor.getInt(stockColumnIndex);
            Integer priceProduct = cursor.getInt(priceColumnIndex);
            String imageProduct = cursor.getString(imageColumnIndex);

            mNameEditText.setText(nameProduct);
            mStockEditText.setText(String.valueOf(stockProduct));
            mPriceEditText.setText(String.valueOf(priceProduct));

            if (imageProduct.equals("@mipmap/ic_empty_image_product")) {
                mProductImageView.setImageResource(R.mipmap.ic_empty_image_product);
                uriImage = Uri.parse(imageProduct);
            } else {
                uriImage = Uri.parse(imageProduct);
                mProductImageView.setImageURI(Uri.parse(imageProduct));
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mStockEditText.setText("");
        mPriceEditText.setText("");
        mProductImageView.setImageResource(R.mipmap.ic_empty_image_product);
    }
}
