package com.example.marcoliva.inventoryapp;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marcoliva.inventoryapp.data.Product;

import java.util.List;

/**
 * Created by ThinkSoft on 11/12/2017.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.RecyclerViewHolder> {

    private List<Product> listProducts;
    private final OnItemClickListener listener;
    private CatalogActivity activity;

    public interface OnItemClickListener{
        void onItemClick(Product product);
    }

    public ProductAdapter(List<Product> products, CatalogActivity activity, OnItemClickListener listener) {
        this.listProducts = products;
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        final Product currentProduct = listProducts.get(position);

        String imageProduct = currentProduct.getImage();
        String nameProduct = currentProduct.getName();
        Integer stockProduct = currentProduct.getStock();
        Integer priceProduct = currentProduct.getPrice();
        holder.bind(listProducts.get(position),listener);

        if (imageProduct.equals("@mipmap/ic_empty_image_product")) {
            holder.mImageProduct.setImageResource(R.mipmap.ic_empty_image_product);
        } else {
            holder.mImageProduct.setImageURI(Uri.parse(imageProduct));
        }

        holder.mNameProduct.setText(nameProduct);
        holder.mStockProduct.setText(String.valueOf(stockProduct) + " Units");
        holder.mPriceProduct.setText(String.valueOf("$ " + priceProduct));

        holder.mSaleProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.sellProduct(listProducts.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProducts.size();
    }

    public void addProducts(List<Product> listProducts) {
        this.listProducts = listProducts;
        notifyDataSetChanged();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageProduct;
        private ImageView mSaleProduct;
        private TextView mNameProduct;
        private TextView mStockProduct;
        private TextView mPriceProduct;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mNameProduct = itemView.findViewById(R.id.product_name);
            mStockProduct = itemView.findViewById(R.id.product_stock);
            mPriceProduct = itemView.findViewById(R.id.product_price);
            mImageProduct = itemView.findViewById(R.id.product_image);
            mSaleProduct = itemView.findViewById(R.id.product_sale);

        }
        public void bind(final Product product , final OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(product);
                }
            });
        }
    }
}
