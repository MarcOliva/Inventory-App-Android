package com.example.marcoliva.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;


/**
 * Created by ThinkSoft on 1/11/2017.
 */

public class ProductProvider extends ContentProvider {

    public static final int PRODUCTS = 100;
    public static final int PRODUCT_ID = 101;

    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    private ProductDbHelper mDbHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCT_ID);
    }


    @Override
    public boolean onCreate() {

        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //Get readable database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknow URI" + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
       final int match = sUriMatcher.match(uri);
       switch (match){
           case PRODUCTS:
               return insertProduct(uri,values);
           default:
               throw new IllegalArgumentException("Insertion is not supported for " + uri);

       }
    }

    private Uri insertProduct(Uri uri, ContentValues values){
        String name = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        Integer stock = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK);
        Integer price = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        byte[] image = values.getAsByteArray(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE);
        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException("Product requires a name");
        }
        if(stock<0 && stock!= null){
            throw new IllegalArgumentException("Product requires valid stock");
        }
        if(price<0 && price!=null){
            throw new IllegalArgumentException("Product require valid price");
        }
        //Get writeable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(ProductContract.ProductEntry.TABLE_NAME,null,values);
        if(id ==-1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);

    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        //Get writeable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                rowsDeleted = db.delete(ProductContract.ProductEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(ProductContract.ProductEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for "+uri);
        }

        if(rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                return updateProduct(uri,contentValues,selection,selectionArgs);
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri,contentValues,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for "+uri);


        }
    }

    private int updateProduct(Uri uri, ContentValues contentValues, String selection , String[] selectionArgs){
        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)){
            String name = contentValues.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null){
                throw new  IllegalArgumentException("Product requires a name");
            }
        }

        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK)){
            Integer stock = contentValues.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK);
            if(stock != null &&stock<0){
                throw new  IllegalArgumentException("Product requires valid a stock");
            }
        }
        if(contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE)){
            Integer price = contentValues.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price!= null && price <0){
                throw new  IllegalArgumentException("Product requires valid a price");
            }
        }

        if (contentValues.size() == 0){
            return 0;
        }

        //Get writeable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated = db.update(ProductContract.ProductEntry.TABLE_NAME,contentValues,selection,selectionArgs);

        if (rowsUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;

    }
}
