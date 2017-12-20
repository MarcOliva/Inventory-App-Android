package com.example.marcoliva.inventoryapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.marcoliva.inventoryapp.data.DaoProduct;
import com.example.marcoliva.inventoryapp.data.Product;
import com.example.marcoliva.inventoryapp.data.ProductDb;

import java.util.ArrayList;
import java.util.List;

public class CatalogActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener {

    private ProductAdapter recyclerViewAdapter;
    private ProductListViewModel viewModel;
    private RecyclerView productRecyclerView;
    private ProductDb db;
    public static DaoProduct daoProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        db = ProductDb.getDataBase(this);
        daoProduct = db.daoProduct();

        FloatingActionButton fab = findViewById(R.id.fab_add_new_product);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        productRecyclerView = findViewById(R.id.recycler_view_products);
        recyclerViewAdapter = new ProductAdapter(new ArrayList<Product>(), this, this);

        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        productRecyclerView.setAdapter(recyclerViewAdapter);

        viewModel = ViewModelProviders.of(this).get(ProductListViewModel.class);

        final View emptyView = findViewById(R.id.empty_view);
        viewModel.getListProduct().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(@Nullable List<Product> products) {
                recyclerViewAdapter.addProducts(products);
                if (recyclerViewAdapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    productRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        if (recyclerViewAdapter.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
            productRecyclerView.setVisibility(View.VISIBLE);
        }

    }

    public void sellProduct(Product sellProduct) {

        int newStock = 0;
        if (sellProduct.getStock() > 0) {
            newStock = sellProduct.getStock() - 1;
        }
        try {
            daoProduct.updateStockProduct(sellProduct.getId(), newStock);
        } catch (Exception e) {
            Toast.makeText(this, "No Update product", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                showDeleteAllPetsDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllProducts() {
        daoProduct.deleteAllProducts();
    }

    private void showDeleteAllPetsDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_products_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete all products.
                deleteAllProducts();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onItemClick(Product product) {
        Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
        intent.putExtra("itemId",product.getId());
        startActivity(intent);
    }
}
