package com.example.lab4mp;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class CartAdapter extends ArrayAdapter<Product> {

    private ArrayList<Product> cart;
    private Activity context;

    public CartAdapter(Activity context, ArrayList<Product> cart) {
        super(context, R.layout.item_cart, cart);
        this.context = context;
        this.cart = cart;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {
            row = context.getLayoutInflater().inflate(R.layout.item_cart, null);
        }

        Product product = cart.get(position);

        TextView txtName = row.findViewById(R.id.txtName);
        TextView txtPrice = row.findViewById(R.id.txtPrice);
        CheckBox checkBox = row.findViewById(R.id.checkBox);
        Button btnDeleteOne = row.findViewById(R.id.btnDeleteOne);

        txtName.setText(product.getName());
        txtPrice.setText(product.getPrice() + " руб.");
        checkBox.setChecked(product.isChecked());

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            product.setChecked(isChecked);
        });

        btnDeleteOne.setOnClickListener(v -> {
            cart.remove(product);
            notifyDataSetChanged();

            if (context instanceof CartActivity) {
                ((CartActivity) context).updateTotal();
            }
        });

        return row;
    }
}
