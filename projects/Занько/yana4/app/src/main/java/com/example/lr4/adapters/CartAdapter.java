package com.example.lr4.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lr4.R;
import com.example.lr4.model.Product;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends BaseAdapter {

    private final List<Product> items;
    private final LayoutInflater inflater;

    public CartAdapter(Context context, List<Product> items) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() { return items.size(); }

    @Override
    public Object getItem(int position) { return items.get(position); }

    @Override
    public long getItemId(int position) { return items.get(position).getId(); }

    static class VH {
        TextView tvId, tvName, tvPrice, tvAuthor;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH h;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_cart, parent, false);
            h = new VH();
            h.tvId = convertView.findViewById(R.id.tvId);
            h.tvName = convertView.findViewById(R.id.tvName);
            h.tvPrice = convertView.findViewById(R.id.tvPrice);
            h.tvAuthor = convertView.findViewById(R.id.tvAuthorRow);
            convertView.setTag(h);
        } else {
            h = (VH) convertView.getTag();
        }

        Product p = items.get(position);
        h.tvId.setText(String.format(Locale.getDefault(), "ID: %d", p.getId()));
        h.tvName.setText("Товар: " + p.getName());
        h.tvPrice.setText(String.format(Locale.getDefault(), "Цена: %.2f BYN", p.getPrice()));
        h.tvAuthor.setText("Нажимает: Занько Я.С. (АС-66)");

        return convertView;
    }
}
