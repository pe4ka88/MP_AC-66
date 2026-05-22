package com.example.thirdlab9;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;
import java.util.Locale;

/**
 * Адаптер для отображения товаров в корзине (read-only, без чекбоксов)
 *
 * @author Пекун Марк Сергеевич, группа АС-66
 * Лабораторная работа №9. Создание собственного адаптера
 */
public class CartAdapter extends BaseAdapter {

    private final Context       context;
    private final List<Product> products;

    public CartAdapter(Context context, List<Product> products) {
        this.context  = context;
        this.products = products;
    }

    @Override public int    getCount()              { return products.size(); }
    @Override public Object getItem(int position)   { return products.get(position); }
    @Override public long   getItemId(int position) { return products.get(position).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.list_item_cart, parent, false);
            holder            = new ViewHolder();
            holder.ivImage    = convertView.findViewById(R.id.ivCartProductImage);
            holder.tvId       = convertView.findViewById(R.id.tvCartProductId);
            holder.tvName     = convertView.findViewById(R.id.tvCartProductName);
            holder.tvCategory = convertView.findViewById(R.id.tvCartProductCategory);
            holder.tvPrice    = convertView.findViewById(R.id.tvCartProductPrice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product p = products.get(position);
        holder.tvId.setText(String.valueOf(p.getId()));
        holder.tvName.setText(p.getName());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "%.2f BYN", p.getPrice()));

        String cat = p.getCategory();
        if (cat != null && !cat.isEmpty()) {
            holder.tvCategory.setText(cat.substring(0, 1).toUpperCase() + cat.substring(1));
            holder.tvCategory.setVisibility(View.VISIBLE);
        } else {
            holder.tvCategory.setVisibility(View.GONE);
        }

        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(p.getImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(200))
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(holder.ivImage);
        } else {
            Glide.with(context).clear(holder.ivImage);
            holder.ivImage.setImageResource(R.drawable.ic_image_placeholder);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivImage;
        TextView  tvId;
        TextView  tvName;
        TextView  tvCategory;
        TextView  tvPrice;
    }
}
