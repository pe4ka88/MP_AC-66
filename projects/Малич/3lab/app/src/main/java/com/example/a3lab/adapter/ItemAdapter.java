package com.example.a3lab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.example.a3lab.R;
import com.example.a3lab.model.Item;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> items = new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Item> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);

        // Используем строки из resources
        String title = item.getTitle();
        holder.titleText.setText(title != null && !title.isEmpty() ? title : context.getString(R.string.no_title_short));

        String category = item.getCategory();
        if (category != null && !category.isEmpty()) {
            holder.categoryText.setText(category);
        } else {
            holder.categoryText.setText(R.string.category_unknown);
        }

        holder.ratingBar.setRating((float) item.getRating());

        String description = item.getDescription();
        String shortDesc;
        if (description != null && !description.isEmpty()) {
            shortDesc = description.length() > 100
                    ? description.substring(0, 100) + "..."
                    : description;
        } else {
            shortDesc = context.getString(R.string.no_description_short);
        }
        holder.descriptionText.setText(shortDesc);

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

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView titleText;
        TextView categoryText;
        TextView descriptionText;
        RatingBar ratingBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            titleText = itemView.findViewById(R.id.itemTitle);
            categoryText = itemView.findViewById(R.id.itemCategory);
            descriptionText = itemView.findViewById(R.id.itemDescription);
            ratingBar = itemView.findViewById(R.id.itemRating);
        }
    }
}