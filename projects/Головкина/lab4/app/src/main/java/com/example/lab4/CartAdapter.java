package com.example.lab4;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class CartAdapter extends BaseAdapter {

    private List<Product> selectedProducts;
    private LayoutInflater inflater;

    public CartAdapter(List<Product> selectedProducts, LayoutInflater inflater) {
        this.selectedProducts = selectedProducts;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return selectedProducts.size();
    }

    @Override
    public Object getItem(int position) {
        return selectedProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return selectedProducts.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_product, parent, false);
            holder = new ViewHolder();
            holder.tvId = convertView.findViewById(R.id.tvProductId);
            holder.tvName = convertView.findViewById(R.id.tvProductName);
            holder.tvPrice = convertView.findViewById(R.id.tvProductPrice);
            holder.cbSelected = convertView.findViewById(R.id.cbProduct);
            holder.cbSelected.setVisibility(View.GONE); // Скрываем чекбокс в корзине
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = selectedProducts.get(position);

        holder.tvId.setText("#" + product.getId());
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format("%.2f руб", product.getPrice()));

        return convertView;
    }

    static class ViewHolder {
        TextView tvId;
        TextView tvName;
        TextView tvPrice;
        CheckBox cbSelected;
    }
}
