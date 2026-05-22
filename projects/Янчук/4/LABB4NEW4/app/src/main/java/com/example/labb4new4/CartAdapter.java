package com.example.labb4new4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
    public int getCount() { return products.size(); }

    @Override
    public Object getItem(int i) { return products.get(i); }

    @Override
    public long getItemId(int i) { return products.get(i).id; }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null)
            v = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);

        Product p = products.get(i);

        TextView tvName = v.findViewById(R.id.tvName);
        TextView tvPrice = v.findViewById(R.id.tvPrice);

        tvName.setText(p.name);
        tvPrice.setText(p.price + " $");

        return v;
    }
}
