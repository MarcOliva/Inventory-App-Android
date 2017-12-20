package com.example.marcoliva.inventoryapp.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by ThinkSoft on 11/12/2017.
 */
@Database(entities = {Product.class},version = 1,exportSchema = false)
public abstract class ProductDb extends RoomDatabase {
    private static ProductDb INSTANCE;

    public static ProductDb getDataBase(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ProductDb.class,"product-db").allowMainThreadQueries().build();
        }
        return INSTANCE;
    }
    public abstract DaoProduct daoProduct();
}
