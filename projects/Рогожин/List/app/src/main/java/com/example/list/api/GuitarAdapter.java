package com.example.list.api;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.list.DetailActivity;
import com.example.list.R;

import java.util.List;

public class GuitarAdapter extends RecyclerView.Adapter<GuitarAdapter.ViewHolder> {

    private List<Guitar> list;

    public GuitarAdapter(List<Guitar> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title, rating;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgCover);
            title = itemView.findViewById(R.id.txtTitle);
            rating = itemView.findViewById(R.id.txtRating);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guitar_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Guitar g = list.get(position);

        holder.title.setText(g.getName());
        holder.rating.setText("⭐ " + g.getOverallRating());

        Glide.with(holder.itemView.getContext())
                .load(g.getImage())
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {

            Context context = v.getContext();

            Intent intent = new Intent(context, DetailActivity.class);

            intent.putExtra(DetailActivity.EXTRA_NAME, g.getName());
            intent.putExtra(DetailActivity.EXTRA_DESC, g.getDescription());
            intent.putExtra(DetailActivity.EXTRA_IMAGE, g.getImage());
            intent.putExtra(DetailActivity.EXTRA_SUMMARY, g.getSummary());

            intent.putExtra(DetailActivity.EXTRA_OVERALL, g.getOverallRating());
            intent.putExtra(DetailActivity.EXTRA_BODY, g.getBodyRating());
            intent.putExtra(DetailActivity.EXTRA_HARDWARE, g.getHardwareRating());
            intent.putExtra(DetailActivity.EXTRA_SOUND, g.getSoundRating());
            intent.putExtra(DetailActivity.EXTRA_VALUE, g.getValueRating());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}