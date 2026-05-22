package com.example.minishop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Locale;

public class GoodsAdapter extends BaseAdapter {

    public interface OnCheckedChangeListener {
        void onCheckedChanged();
    }

    private Context context;
    private ArrayList<Good> goodsList;
    private LayoutInflater inflater;
    private OnCheckedChangeListener listener;

    public GoodsAdapter(Context context, ArrayList<Good> goodsList, OnCheckedChangeListener listener) {
        this.context = context;
        this.goodsList = goodsList;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return goodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return goodsList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Good good = goodsList.get(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_good, parent, false);

            holder = new ViewHolder();
            holder.tvId = convertView.findViewById(R.id.tvGoodId);
            holder.tvName = convertView.findViewById(R.id.tvGoodName);
            holder.tvPrice = convertView.findViewById(R.id.tvGoodPrice);
            holder.checkBox = convertView.findViewById(R.id.checkBoxGood);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvId.setText("ID: " + good.getId());
        holder.tvName.setText("Название: " + good.getName());
        holder.tvPrice.setText(String.format(Locale.US, "Цена: %.2f руб.", good.getPrice()));

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(good.isChecked());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                good.setChecked(isChecked);
                if (listener != null) {
                    listener.onCheckedChanged();
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView tvId;
        TextView tvName;
        TextView tvPrice;
        CheckBox checkBox;
    }
}