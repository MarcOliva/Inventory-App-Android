package com.example.marcoliva.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by ThinkSoft on 1/11/2017.
 */

public class ProductDbHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " (" +
                    ProductContract.ProductEntry._ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                    ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE + TEXT_TYPE + NOT_NULL
                    + " )";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
