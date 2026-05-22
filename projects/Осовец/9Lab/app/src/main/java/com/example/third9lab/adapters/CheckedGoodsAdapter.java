package com.example.third9lab.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.third9lab.R;
import com.example.third9lab.models.Good;

import java.util.List;

public class CheckedGoodsAdapter extends BaseAdapter {

    private final Context context;
    private final List<Good> checkedGoods;

    public CheckedGoodsAdapter(Context context, List<Good> checkedGoods) {
        this.context = context;
        this.checkedGoods = checkedGoods;
    }

    @Override
    public int getCount() {
        return checkedGoods.size();
    }

    @Override
    public Good getItem(int position) {
        return checkedGoods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return checkedGoods.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_checked_good, parent, false);
            holder = new ViewHolder();
            holder.tvNumber = convertView.findViewById(R.id.tv_number);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvPrice = convertView.findViewById(R.id.tv_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Good good = getItem(position);

        holder.tvNumber.setText(String.valueOf(good.getId()));
        holder.tvName.setText(good.getName());
        holder.tvPrice.setText(String.format("%.2f ₽", good.getPrice()));

        return convertView;
    }

    private static class ViewHolder {
        TextView tvNumber;
        TextView tvName;
        TextView tvPrice;
    }
}
