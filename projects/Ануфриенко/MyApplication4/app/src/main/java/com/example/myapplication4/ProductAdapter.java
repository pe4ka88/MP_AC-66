package com.example.myapplication4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class ProductAdapter extends BaseAdapter {

    private final Context    context;
    private final List<Item> items;
    private final Runnable   onChanged;  // вызывается при смене чекбокса

    public ProductAdapter(Context context, List<Item> items, Runnable onChanged) {
        this.context   = context;
        this.items     = items;
        this.onChanged = onChanged;
    }

    @Override public int    getCount()        { return items.size(); }
    @Override public Object getItem(int pos)  { return items.get(pos); }
    @Override public long   getItemId(int pos){ return items.get(pos).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_product, parent, false);

        Item item = items.get(position);

        ((TextView) convertView.findViewById(R.id.tvId))
                .setText("#" + item.getId());
        ((TextView) convertView.findViewById(R.id.tvName))
                .setText(item.getName());
        ((TextView) convertView.findViewById(R.id.tvPrice))
                .setText(item.getPrice() + " BYN");

        CheckBox cb = convertView.findViewById(R.id.cbItem);
        cb.setOnCheckedChangeListener(null);          // сброс перед setChecked
        cb.setChecked(item.isChecked());
        cb.setOnCheckedChangeListener((v, isChecked) -> {
            item.setChecked(isChecked);
            onChanged.run();
        });

        return convertView;
    }
}