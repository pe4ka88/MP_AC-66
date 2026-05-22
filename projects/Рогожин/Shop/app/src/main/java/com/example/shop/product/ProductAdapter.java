package com.example.shop.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shop.MainActivity;
import com.example.shop.R;

import java.util.ArrayList;
import java.util.Locale;

public class ProductAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Product> products;

    public ProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return products.get(position).id;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvDescription;
        TextView tvPrice;
        ImageView imgProduct;
        CheckBox checkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_product, parent, false);

            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.tvDescription = convertView.findViewById(R.id.tvDescription);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);
            holder.imgProduct = convertView.findViewById(R.id.imgProduct);
            holder.checkBox = convertView.findViewById(R.id.checkBox);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = products.get(position);

        holder.tvName.setText(product.title);
        holder.tvDescription.setText(product.description != null ? product.description : "");
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

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(product.checked);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            product.checked = isChecked;

            if (context instanceof MainActivity) {
                ((MainActivity) context).updateCounter();
            }
        });

        return convertView;
    }
}