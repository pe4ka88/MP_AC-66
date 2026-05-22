package com.example.myapplication4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CartAdapter extends BaseAdapter {

    private final Context    context;
    private final List<Item> items;

    public CartAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items   = items;
    }

    @Override public int    getCount()        { return items.size(); }
    @Override public Object getItem(int pos)  { return items.get(pos); }
    @Override public long   getItemId(int pos){ return items.get(pos).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_cart, parent, false);

        Item item = items.get(position);

        ((TextView) convertView.findViewById(R.id.tvId))
                .setText("#" + item.getId());
        ((TextView) convertView.findViewById(R.id.tvName))
                .setText(item.getName());
        ((TextView) convertView.findViewById(R.id.tvPrice))
                .setText(item.getPrice() + " BYN");

        return convertView;
    }
}