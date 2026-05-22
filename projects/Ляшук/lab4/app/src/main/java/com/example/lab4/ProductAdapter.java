package com.example.lab4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private List<Product> products;
    private OnCheckedChangeListener listener;

    public interface OnCheckedChangeListener {
        void onCheckedChanged();
    }

    public ProductAdapter(Context context, List<Product> products, OnCheckedChangeListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
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
        
        // Remove previous listener to avoid triggering it when setting state
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(product.isChecked());

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            product.setChecked(isChecked);
            if (listener != null) {
                listener.onCheckedChanged();
            }
        });

        return convertView;
    }
}
