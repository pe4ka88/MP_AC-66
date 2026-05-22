package com.example.a3lab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    public PostAdapter(List<Post> posts, OnItemClickListener listener) {
        this.posts = posts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.textViewTitle.setText(post.getTitle());
        holder.textViewId.setText(holder.itemView.getContext().getString(R.string.post_id, post.getId()));
        
        String imageUrl = "https://picsum.photos/seed/" + post.getId() + "/200";
        
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.imageViewThumbnail);
                
        holder.itemView.setOnClickListener(v -> listener.onItemClick(post));
    }

    @Override
    public int getItemCount() {
        return posts == null ? 0 : posts.size();
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewId;
        ImageView imageViewThumbnail;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewId = itemView.findViewById(R.id.textViewId);
            imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
        }
    }
}