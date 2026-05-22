package com.example.mangalistapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mangalistezepchukac66.R;

import java.util.List;

public class MangaAdapter
        extends RecyclerView.Adapter<MangaAdapter.MangaViewHolder> {

    private List<Manga> mangaList;

    public MangaAdapter(List<Manga> mangaList) {
        this.mangaList = mangaList;
    }

    static class MangaViewHolder extends RecyclerView.ViewHolder {

        TextView titleText, descText;
        ImageView coverImage;

        public MangaViewHolder(@NonNull View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.txtTitle);
            descText = itemView.findViewById(R.id.txtDesc);
            coverImage = itemView.findViewById(R.id.imgCover);
        }
    }

    @NonNull
    @Override
    public MangaViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manga_card, parent, false);

        return new MangaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MangaViewHolder holder, int position) {

        Manga manga = mangaList.get(position);

        holder.titleText.setText(manga.getTitle());
        holder.descText.setText(manga.getDescription());

        Glide.with(holder.itemView.getContext())
                .load(manga.getCoverUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.coverImage);
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    holder.itemView.getContext(),
                    com.example.mangalistezepchukac66.MangaDetailActivity.class
            );

            intent.putExtra("title", manga.getTitle());
            intent.putExtra("desc", manga.getDescription());
            intent.putExtra("cover", manga.getCoverUrl());
            intent.putExtra("genres", manga.getGenres());
            intent.putExtra("rating", manga.getRating());

            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mangaList.size();
    }
}
