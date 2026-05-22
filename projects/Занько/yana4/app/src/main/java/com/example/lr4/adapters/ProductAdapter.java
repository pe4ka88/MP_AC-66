package com.example.lr4.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.lr4.R;
import com.example.lr4.callback.OnProductCheckedChange;
import com.example.lr4.model.Product;

import java.util.List;
import java.util.Locale;

public class ProductAdapter extends BaseAdapter {

    private final Context context;
    private final List<Product> items;
    private final LayoutInflater inflater;
    private final OnProductCheckedChange callback;

    public ProductAdapter(Context context, List<Product> items, OnProductCheckedChange callback) {
        this.context = context;
        this.items = items;
        this.callback = callback;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() { return items.size(); }

    @Override
    public Object getItem(int position) { return items.get(position); }

    @Override
    public long getItemId(int position) { return items.get(position).getId(); }

    static class VH {
        TextView tvId, tvName, tvPrice, tvAuthor;
        CheckBox cb;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH h;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_product, parent, false);
            h = new VH();
            h.tvId = convertView.findViewById(R.id.tvId);
            h.tvName = convertView.findViewById(R.id.tvName);
            h.tvPrice = convertView.findViewById(R.id.tvPrice);
            h.tvAuthor = convertView.findViewById(R.id.tvAuthorRow);
            h.cb = convertView.findViewById(R.id.cbPick);
            convertView.setTag(h);
        } else {
            h = (VH) convertView.getTag();
        }

        Product p = items.get(position);

        h.tvId.setText(String.format(Locale.getDefault(), "ID: %d", p.getId()));
        h.tvName.setText("Товар: " + p.getName());
        h.tvPrice.setText(String.format(Locale.getDefault(), "Цена: %.2f BYN", p.getPrice()));

        // Признак авторства прямо в пункте списка
        h.tvAuthor.setText("Разработал: Занько Я.С., АС-66");

        // важно: снять старый listener, иначе будет дергаться при переиспользовании view
        h.cb.setOnCheckedChangeListener(null);
        h.cb.setChecked(p.isChecked());

        h.cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            p.setChecked(isChecked);
            if (callback != null) callback.onChanged(p, isChecked);
        });

        return convertView;
    }
}
