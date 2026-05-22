package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.ArrayList;

public class BoxAdapter extends BaseAdapter {
    private Context ctx;
    private LayoutInflater lInflater;
    private ArrayList<Product> objects;
    private OnProductCheckedListener listener;

    public interface OnProductCheckedListener {
        void onProductChecked(int count);
    }

    public BoxAdapter(Context context, ArrayList<Product> products, OnProductCheckedListener listener) {
        ctx = context;
        objects = products;
        this.listener = listener;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return objects.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.activity_node, parent, false);
        }

        Product p = (Product) getItem(position);

        // 5. идентификационный номер, название, стоимость
        ((TextView) view.findViewById(R.id.tvDescr)).setText(p.id + ". " + p.name);
        ((TextView) view.findViewById(R.id.tvPrice)).setText("Цена: " + p.price);

        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
        cbBuy.setOnCheckedChangeListener(null);
        cbBuy.setChecked(p.box);
        
        // 6. динамически отображать общее текущее количество активированных товаров.
        cbBuy.setOnCheckedChangeListener((buttonView, isChecked) -> {
            p.box = isChecked;
            if (listener != null) {
                listener.onProductChecked(getBoxCount());
            }
        });

        return view;
    }

    private int getBoxCount() {
        int count = 0;
        for (Product p : objects) {
            if (p.box) count++;
        }
        return count;
    }

    public ArrayList<Product> getBox() {
        ArrayList<Product> box = new ArrayList<>();
        for (Product p : objects) {
            if (p.box) box.add(p);
        }
        return box;
    }
}
