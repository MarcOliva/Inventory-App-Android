package com.example.marcoliva.inventoryapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.marcoliva.inventoryapp.data.Product;
import com.example.marcoliva.inventoryapp.data.ProductDb;

import java.util.List;

/**
 * Created by ThinkSoft on 19/12/2017.
 */

public class ProductListViewModel extends AndroidViewModel {
    private final LiveData<List<Product>> listProduct;
    private ProductDb appDb;

    public ProductListViewModel(@NonNull Application application) {
        super(application);
        appDb = ProductDb.getDataBase(this.getApplication());
        listProduct = appDb.daoProduct().getAllProducts();
    }

    public LiveData<List<Product>> getListProduct() {
        return listProduct;
    }

    public void addProduct(Product product){
        new addAsyncTask(appDb).execute(product);
    }

    private static class addAsyncTask extends AsyncTask<Product,Void,Void>{
        private ProductDb db;
        addAsyncTask(ProductDb productDb){
            db = productDb;
        }
        @Override
        protected Void doInBackground(Product... products) {
            db.daoProduct().insertProduct(products[0]);
            return null;
        }
    }

}
