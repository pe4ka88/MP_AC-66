package com.example.lab3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lab3.R;
import com.example.lab3.model.Displayable;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private List<? extends Displayable> items = new ArrayList<>();
    private final Context context;
    private OnItemClickListener listener;
    private boolean showImages = true;

    public interface OnItemClickListener {
        void onItemClick(Displayable item);
    }

    public DataAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setShowImages(boolean showImages) {
        this.showImages = showImages;
        notifyDataSetChanged();
    }

    public void setItems(List<? extends Displayable> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_data, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        Displayable item = items.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());
        holder.tvId.setText("#" + item.getId());

        if (showImages && item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            holder.ivThumbnail.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .centerCrop()
                    .into(holder.ivThumbnail);
        } else {
            holder.ivThumbnail.setVisibility(View.VISIBLE);
            holder.ivThumbnail.setImageResource(R.drawable.ic_item_default);
        }

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView ivThumbnail;
        TextView tvTitle;
        TextView tvSubtitle;
        TextView tvId;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubtitle = itemView.findViewById(R.id.tv_subtitle);
            tvId = itemView.findViewById(R.id.tv_id);
        }
    }
}
