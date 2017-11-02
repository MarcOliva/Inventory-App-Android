package com.example.marcoliva.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marcoliva.inventoryapp.data.ProductContract;

/**
 * Created by ThinkSoft on 1/11/2017.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView mNameProduct = view.findViewById(R.id.product_name);
        TextView mStockProduct = view.findViewById(R.id.product_stock);
        TextView mPriceProduct = view.findViewById(R.id.product_price);
        ImageView mImageProduct = view.findViewById(R.id.product_image);

        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int stockColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE);

        String nameProduct = cursor.getString(nameColumnIndex);
        Integer stockProduct = cursor.getInt(stockColumnIndex);
        Integer priceProduct = cursor.getInt(priceColumnIndex);
        byte[] imageProduct = cursor.getBlob(imageColumnIndex);

        mNameProduct.setText(nameProduct);
        mStockProduct.setText(String.valueOf(stockProduct));
        mPriceProduct.setText(String.valueOf(priceProduct));
        Bitmap bmProduct = BitmapFactory.decodeByteArray(imageProduct,0,imageProduct.length);
        mImageProduct.setImageBitmap(bmProduct);

    }
}
