package com.example.marcoliva.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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

    private final CatalogActivity activity;

    public ProductCursorAdapter(CatalogActivity context, Cursor c) {
        super(context, c, 0);
        this.activity = context;
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
        ImageView mSaleProduct = view.findViewById(R.id.product_sale);

        int _idColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int stockColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE);

        final int _idProduct = cursor.getInt(_idColumnIndex);
        String nameProduct = cursor.getString(nameColumnIndex);
        final Integer stockProduct = cursor.getInt(stockColumnIndex);
        Integer priceProduct = cursor.getInt(priceColumnIndex);
        String imageProduct = cursor.getString(imageColumnIndex);

        if (imageProduct.equals("@mipmap/ic_empty_image_product")) {
            mImageProduct.setImageResource(R.mipmap.ic_empty_image_product);
        } else {
            mImageProduct.setImageURI(Uri.parse(imageProduct));

        }

        mNameProduct.setText(nameProduct);
        mStockProduct.setText(String.valueOf(stockProduct) + " Units");
        mPriceProduct.setText(String.valueOf("$ " + priceProduct));

        mSaleProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.sellProduct(_idProduct, stockProduct);
            }
        });


    }

}
