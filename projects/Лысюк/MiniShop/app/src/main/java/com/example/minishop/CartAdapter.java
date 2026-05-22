package com.example.minishop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class CartAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Good> checkedGoods;
    private LayoutInflater inflater;

    public CartAdapter(Context context, ArrayList<Good> checkedGoods) {
        this.context = context;
        this.checkedGoods = checkedGoods;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return checkedGoods.size();
    }

    @Override
    public Object getItem(int position) {
        return checkedGoods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return checkedGoods.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CartViewHolder holder;
        Good good = checkedGoods.get(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_cart, parent, false);

            holder = new CartViewHolder();
            holder.tvCartId = convertView.findViewById(R.id.tvCartGoodId);
            holder.tvCartName = convertView.findViewById(R.id.tvCartGoodName);
            holder.tvCartPrice = convertView.findViewById(R.id.tvCartGoodPrice);

            convertView.setTag(holder);
        } else {
            holder = (CartViewHolder) convertView.getTag();
        }

        holder.tvCartId.setText("ID: " + good.getId());
        holder.tvCartName.setText("Название: " + good.getName());
        holder.tvCartPrice.setText(String.format(Locale.US, "Цена: %.2f руб.", good.getPrice()));

        return convertView;
    }

    private static class CartViewHolder {
        TextView tvCartId;
        TextView tvCartName;
        TextView tvCartPrice;
    }
}