package com.example.minishop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.minishop.R;
import com.example.minishop.interfaces.OnItemCheckedListener;
import com.example.minishop.models.Product;

import java.util.List;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private List<Product> productList;
    private LayoutInflater inflater;
    private OnItemCheckedListener checkedListener;

    public ProductAdapter(Context context, List<Product> productList, OnItemCheckedListener listener) {
        this.context = context;
        this.productList = productList;
        this.inflater = LayoutInflater.from(context);
        this.checkedListener = listener;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_product, parent, false);
            holder = new ViewHolder();
            holder.tvEmoji = convertView.findViewById(R.id.textViewEmoji);
            holder.tvId = convertView.findViewById(R.id.textViewId);
            holder.tvName = convertView.findViewById(R.id.textViewName);
            holder.tvPrice = convertView.findViewById(R.id.textViewPrice);
            holder.checkBox = convertView.findViewById(R.id.checkBoxProduct);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Product product = productList.get(position);

        holder.tvId.setText(String.valueOf(product.getId()));
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format("%.2f руб", product.getPrice()));

        String emoji;
        switch (product.getId()) {
            case 1: emoji = "🍎"; break;
            case 2: emoji = "🍌"; break;
            case 3: emoji = "🍓"; break;
            case 4: emoji = "🍇"; break;
            case 5: emoji = "🍊"; break;
            case 6: emoji = "🍋"; break;
            case 7: emoji = "🍐"; break;
            case 8: emoji = "🍉"; break;
            default: emoji = "🍎";
        }
        holder.tvEmoji.setText(emoji);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(product.isSelected());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                product.setSelected(isChecked);
                if (checkedListener != null) {
                    checkedListener.onItemChecked(product, isChecked);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView tvEmoji;
        TextView tvId;
        TextView tvName;
        TextView tvPrice;
        CheckBox checkBox;
    }
}