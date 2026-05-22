package com.example.shopezepchukac66;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends BaseAdapter {

    Context context;
    List<Order> orders = new ArrayList<>();

    public HistoryAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int i) {
        return orders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_history, parent, false);
        }

        TextView txtNumber = view.findViewById(R.id.txtOrderNumber);
        TextView txtCount  = view.findViewById(R.id.txtCount);
        TextView txtTotal  = view.findViewById(R.id.txtTotal);

        Order order = orders.get(i);

        txtNumber.setText("Заказ #" + (i + 1));
        txtCount.setText(order.title);
        txtTotal.setText("Сумма: $" + String.format("%.2f", order.price));

        return view;
    }

    public void updateData(List<Order> newOrders) {
        orders.clear();
        orders.addAll(newOrders);
        notifyDataSetChanged();
    }
}