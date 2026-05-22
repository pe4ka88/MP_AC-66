package com.example.a4lab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CartAdapter extends BaseAdapter {
    private List<Product> products;
    private LayoutInflater inflater;

    public CartAdapter(List<Product> products, LayoutInflater inflater) {
        this.products = products;
        this.inflater = inflater;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_product, parent, false);
            holder = new ViewHolder();
            holder.tvId = convertView.findViewById(R.id.tv_product_id);
            holder.tvName = convertView.findViewById(R.id.tv_product_name);
            holder.tvPrice = convertView.findViewById(R.id.tv_product_price);
            // Скрываем чекбокс в корзине
            convertView.findViewById(R.id.checkbox_product).setVisibility(View.GONE);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = products.get(position);

        holder.tvId.setText("ID: " + product.getId());
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText("$" + String.format("%.2f", product.getPrice()));

        return convertView;
    }

    static class ViewHolder {
        TextView tvId;
        TextView tvName;
        TextView tvPrice;
    }
}