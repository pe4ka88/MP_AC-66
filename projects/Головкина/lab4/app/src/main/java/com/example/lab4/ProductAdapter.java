package com.example.lab4;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

public class ProductAdapter extends BaseAdapter {

    private List<Product> productList;
    private LayoutInflater inflater;
    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void onItemSelectedChanged();
    }

    public ProductAdapter(List<Product> productList, LayoutInflater inflater, OnItemSelectedListener listener) {
        this.productList = productList;
        this.inflater = inflater;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productList.get(position).getId();
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Product product = productList.get(position);

        holder.tvId.setText("#" + product.getId());
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format("%.2f руб", product.getPrice()));
        holder.cbSelected.setChecked(product.isSelected());

        // Обработка выбора чекбокса
        holder.cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                product.setSelected(isChecked);
                if (listener != null) {
                    listener.onItemSelectedChanged();
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tvId;
        TextView tvName;
        TextView tvPrice;
        CheckBox cbSelected;
    }
}
