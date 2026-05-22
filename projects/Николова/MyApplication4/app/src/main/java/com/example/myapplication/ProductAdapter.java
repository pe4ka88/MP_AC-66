package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<Product> objects;
    Runnable onCheckChanged;

    public ProductAdapter(Context context, ArrayList<Product> products, Runnable onCheckChanged) {
        ctx = context;
        objects = products;
        this.onCheckChanged = onCheckChanged;
    }

    @Override
    public int getCount() { return objects.size(); }

    @Override
    public Object getItem(int i) { return objects.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) view = LayoutInflater.from(ctx).inflate(R.layout.item_product, viewGroup, false);

        Product p = (Product) getItem(i);
        ((TextView) view.findViewById(R.id.tvId)).setText(String.valueOf(p.id));
        ((TextView) view.findViewById(R.id.tvName)).setText(p.name);

        CheckBox cb = view.findViewById(R.id.cbSelect);
        cb.setChecked(p.checked);


        view.setBackgroundColor(i % 2 == 0 ? Color.parseColor("#D1D5DE") : Color.parseColor("#E1D5E7"));

        return view;
    }
}