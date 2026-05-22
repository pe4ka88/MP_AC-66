package com.example.thirdlab9;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;
import java.util.Locale;

/**
 * Кастомный адаптер для отображения списка товаров с изображениями из FakeStore API
 *
 * @author Пекун Марк Сергеевич, группа АС-66
 * Лабораторная работа №9
 */
public class ProductAdapter extends BaseAdapter {

    private final Context context;
    private final List<Product> products;
    private final OnProductCheckedListener listener;

    public interface OnProductCheckedListener {
        void onProductChecked(int position, boolean isChecked);
    }

    public ProductAdapter(Context context, List<Product> products, OnProductCheckedListener listener) {
        this.context  = context;
        this.products = products;
        this.listener = listener;
    }

    @Override public int    getCount()              { return products.size(); }
    @Override public Object getItem(int position)   { return products.get(position); }
    @Override public long   getItemId(int position) { return products.get(position).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.list_item_product, parent, false);
            holder            = new ViewHolder();
            holder.cardRoot   = convertView.findViewById(R.id.cardRoot);
            holder.ivImage    = convertView.findViewById(R.id.ivProductImage);
            holder.tvId       = convertView.findViewById(R.id.tvProductId);
            holder.tvName     = convertView.findViewById(R.id.tvProductName);
            holder.tvCategory = convertView.findViewById(R.id.tvProductCategory);
            holder.tvPrice    = convertView.findViewById(R.id.tvProductPrice);
            holder.checkbox   = convertView.findViewById(R.id.cbProductSelect);
            convertView.setTag(holder);

            // Listener ставим ОДИН РАЗ при создании view,
            // берём product из holder — всегда актуальный объект
            holder.cardRoot.setOnClickListener(v -> {
                Product p = holder.product;
                if (p == null) return;
                boolean newState = !p.isChecked();
                p.setChecked(newState);
                holder.checkbox.setChecked(newState);
                animateCheckbox(holder.checkbox, newState);
                animateCardPress(holder.cardRoot);
                if (listener != null) listener.onProductChecked(holder.boundPosition, newState);
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Привязываем актуальный product и позицию к holder
        holder.product       = products.get(position);
        holder.boundPosition = position;
        Product product      = holder.product;

        // Сбрасываем listener перед setChecked чтобы не было лишних срабатываний
        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setChecked(product.isChecked());

        holder.tvId.setText(String.valueOf(product.getId()));
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "%.2f BYN", product.getPrice()));

        String cat = product.getCategory();
        if (cat != null && !cat.isEmpty()) {
            holder.tvCategory.setText(cat.substring(0, 1).toUpperCase() + cat.substring(1));
            holder.tvCategory.setVisibility(View.VISIBLE);
        } else {
            holder.tvCategory.setVisibility(View.GONE);
        }

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(product.getImageUrl())
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

    private void animateCheckbox(View view, boolean checked) {
        float peak = checked ? 1.35f : 0.8f;
        ObjectAnimator sX = ObjectAnimator.ofFloat(view, "scaleX", 1f, peak, 1f);
        ObjectAnimator sY = ObjectAnimator.ofFloat(view, "scaleY", 1f, peak, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(sX, sY);
        set.setDuration(280);
        set.setInterpolator(new OvershootInterpolator(3f));
        set.start();
    }

    private void animateCardPress(View view) {
        ObjectAnimator up   = ObjectAnimator.ofFloat(view, "translationZ", 2f, 8f);
        ObjectAnimator down = ObjectAnimator.ofFloat(view, "translationZ", 8f, 2f);
        AnimatorSet set = new AnimatorSet();
        set.play(down).after(up);
        set.setDuration(220);
        set.start();
    }

    private static class ViewHolder {
        LinearLayout cardRoot;
        ImageView    ivImage;
        TextView     tvId;
        TextView     tvName;
        TextView     tvCategory;
        TextView     tvPrice;
        CheckBox     checkbox;
        Product      product;       // актуальный объект товара
        int          boundPosition; // актуальная позиция
    }
}
