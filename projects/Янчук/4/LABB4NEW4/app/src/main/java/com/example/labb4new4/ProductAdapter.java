package com.example.labb4new4;

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
    private Runnable onCheckedChanged;

    public ProductAdapter(Context context, List<Product> products, Runnable onCheckedChanged) {
        this.context = context;
        this.products = products;
        this.onCheckedChanged = onCheckedChanged;
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
            v = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);

        Product p = products.get(i);

        TextView tvName = v.findViewById(R.id.tvName);
        TextView tvPrice = v.findViewById(R.id.tvPrice);
        CheckBox cb = v.findViewById(R.id.cb);

        tvName.setText(p.name);
        tvPrice.setText(p.price + " $");
        cb.setChecked(p.checked);

        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            p.checked = isChecked;
            onCheckedChanged.run();
        });

        return v;
    }
}
