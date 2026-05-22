package com.example.json;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;
    private OnItemClickListener listener;
    private boolean showImages = false;

    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    public PostAdapter(List<Post> posts, OnItemClickListener listener) {
        this.posts = posts;
        this.listener = listener;
    }

    public void setShowImages(boolean showImages) {
        this.showImages = showImages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post, listener, showImages);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvTitle, tvPreview;
        ImageView ivImage;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvPostId);
            tvTitle = itemView.findViewById(R.id.tvPostTitle);
            tvPreview = itemView.findViewById(R.id.tvPostPreview);
            ivImage = itemView.findViewById(R.id.ivPostImage);
        }

        void bind(final Post post, final OnItemClickListener listener, boolean showImages) {
            tvId.setText("ID: " + post.getId() + " | User: " + post.getUserId());
            tvTitle.setText(post.getTitle());

            // Превью (первые 100 символов)
            String preview = post.getBody();
            if (preview.length() > 100) {
                preview = preview.substring(0, 100) + "...";
            }
            tvPreview.setText(preview);

            // Изображение (бонус)
            if (showImages && post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                ivImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(post.getImageUrl())
                        .centerCrop()
                        .into(ivImage);
            } else {
                ivImage.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(post);
                }
            });
        }
    }
}