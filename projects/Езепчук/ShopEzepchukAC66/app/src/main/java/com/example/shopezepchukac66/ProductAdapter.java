package com.example.shopezepchukac66;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        }

        Product product = products.get(position);

        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvDescription = view.findViewById(R.id.tvDescription);
        TextView tvPrice = view.findViewById(R.id.tvPrice);
        ImageView imgProduct = view.findViewById(R.id.imgProduct);
        CheckBox checkBox = view.findViewById(R.id.checkBox);

        // Название, описание, цена
        tvName.setText(product.title);
        tvDescription.setText(product.description != null ? product.description : "");
        tvPrice.setText("$" + product.price);

        // Фото через Glide
        if (product.thumbnail != null && !product.thumbnail.isEmpty()) {
            Glide.with(context)
                    .load(product.thumbnail)
                    .placeholder(R.drawable.placeholder) // можно добавить заглушку
                    .into(imgProduct);
        } else {
            imgProduct.setImageResource(R.drawable.placeholder);
        }

        // Чекбокс — чтобы корректно обновлялся при скролле
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(product.checked);

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            product.checked = isChecked;
            // Обновляем счётчик в MainActivity
            if (context instanceof MainActivity) {
                ((MainActivity) context).updateCounter();
            }
        });

        return view;
    }
}
