package com.example.marcoliva.inventoryapp.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by ThinkSoft on 11/12/2017.
 */
@Dao
public interface DaoProduct {

    @Query("select * from products")
    LiveData<List<Product>> getAllProducts();

    @Query("select * from products where idProduct in (:tid)")
    Product getProductById(long tid);

    @Query("delete from products")
    void deleteAllProducts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProduct(Product... product);

    @Query("UPDATE products SET stock = :newStock  WHERE idProduct = :tid")
    void updateStockProduct(long tid, int newStock);

    @Update
    void updateProduct(Product... products);

    @Delete
    void delete(Product... products);

}
