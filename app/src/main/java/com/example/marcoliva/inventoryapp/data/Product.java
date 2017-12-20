package com.example.marcoliva.inventoryapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by ThinkSoft on 11/12/2017.
 */
@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idProduct")
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "stock")
    private Integer stock;

    @ColumnInfo(name = "price")
    private Integer price;

    @ColumnInfo(name = "image")
    private String image;

    public Product(long id, Integer price, Integer stock, String name , String image){
        this.id = id;
        this.price = price;
        this.stock = stock;
        this.name = name;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public Integer getStock() {
        return stock;
    }

    public Integer getPrice() {
        return price;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
