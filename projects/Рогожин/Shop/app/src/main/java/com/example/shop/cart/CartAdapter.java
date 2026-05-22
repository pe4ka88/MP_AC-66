package com.example.shop.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shop.R;
import com.example.shop.product.Product;

import java.util.ArrayList;
import java.util.Locale;

public class CartAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Product> cartItems;

    public CartAdapter(Context context, ArrayList<Product> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    static class ViewHolder {
        ImageView imgProduct;
        TextView tvName;
        TextView tvDescription;
        TextView tvPrice;
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cartItems.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_cart, parent, false);

            holder = new ViewHolder();
            holder.imgProduct = convertView.findViewById(R.id.imgProduct);
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.tvDescription = convertView.findViewById(R.id.tvDescription);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = cartItems.get(position);

        holder.tvName.setText(product.title);

        if (product.description != null && !product.description.isEmpty()) {
            holder.tvDescription.setText(product.description);
        } else {
            holder.tvDescription.setText("");
        }

        holder.tvPrice.setText(String.format(Locale.US, "$%.2f", product.price));

        // Используем thumbnail из DummyJSON
        if (product.thumbnail != null && !product.thumbnail.isEmpty()) {

            Glide.with(context)
                    .load(product.thumbnail)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imgProduct);

        } else {
            holder.imgProduct.setImageResource(R.drawable.placeholder);
        }

        return convertView;
    }
}