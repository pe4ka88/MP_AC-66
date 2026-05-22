package com.example.lab4mp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Product> products;
    private LayoutInflater inflater;

    public ProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
        this.inflater = LayoutInflater.from(context);
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
        View view = inflater.inflate(R.layout.item_product, null);

        TextView txtId = view.findViewById(R.id.txtId);
        TextView txtName = view.findViewById(R.id.txtName);
        TextView txtPrice = view.findViewById(R.id.txtPrice);
        CheckBox checkBox = view.findViewById(R.id.checkBox);

        Product p = products.get(position);

        txtId.setText("ID: " + p.getId());
        txtName.setText(p.getName());
        txtPrice.setText("Цена: " + p.getPrice());

        checkBox.setChecked(p.isChecked());

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            p.setChecked(isChecked);
        });

        return view;
    }
}
