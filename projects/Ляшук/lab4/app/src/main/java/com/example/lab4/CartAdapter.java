package com.example.lab4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private List<Product> products;

    public CartAdapter(Context context, List<Product> products) {
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
        return products.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        }

        Product product = products.get(position);

        TextView tvId = convertView.findViewById(R.id.tvId);
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvPrice = convertView.findViewById(R.id.tvPrice);
        CheckBox checkBox = convertView.findViewById(R.id.checkBox);

        tvId.setText(String.valueOf(product.getId()));
        tvName.setText(product.getName());
        tvPrice.setText(String.valueOf(product.getPrice()));
        
        checkBox.setChecked(true);
        checkBox.setEnabled(false); // In the cart, items are already selected

        return convertView;
    }
}
