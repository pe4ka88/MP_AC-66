package com.example.third9lab.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.third9lab.R;
import com.example.third9lab.interfaces.OnChangeListener;
import com.example.third9lab.models.Good;

import java.util.List;

public class GoodsAdapter extends BaseAdapter {

    private static final int COLOR_EVEN = Color.parseColor("#FFFFFF");
    private static final int COLOR_ODD = Color.parseColor("#E0F2F1");

    private final Context context;
    private final List<Good> goods;
    private final OnChangeListener onChangeListener;

    public GoodsAdapter(Context context, List<Good> goods, OnChangeListener onChangeListener) {
        this.context = context;
        this.goods = goods;
        this.onChangeListener = onChangeListener;
    }

    @Override
    public int getCount() {
        return goods.size();
    }

    @Override
    public Good getItem(int position) {
        return goods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return goods.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_good, parent, false);
            holder = new ViewHolder();
            holder.tvNumber = convertView.findViewById(R.id.tv_number);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvPrice = convertView.findViewById(R.id.tv_price);
            holder.cbChecked = convertView.findViewById(R.id.cb_checked);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Good good = getItem(position);

        holder.tvNumber.setText(String.valueOf(good.getId()));
        holder.tvName.setText(good.getName());
        holder.tvPrice.setText(String.format("%.2f ₽", good.getPrice()));

        // Alternating row colors
        convertView.setBackgroundColor(position % 2 == 0 ? COLOR_EVEN : COLOR_ODD);

        // Remove listener before setting checked to avoid triggering during recycling
        holder.cbChecked.setOnCheckedChangeListener(null);
        holder.cbChecked.setChecked(good.isChecked());

        holder.cbChecked.setOnCheckedChangeListener((buttonView, isChecked) -> {
            good.setChecked(isChecked);
            int checkedCount = 0;
            for (Good g : goods) {
                if (g.isChecked()) checkedCount++;
            }
            onChangeListener.onChange(checkedCount);
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView tvNumber;
        TextView tvName;
        TextView tvPrice;
        CheckBox cbChecked;
    }
}
