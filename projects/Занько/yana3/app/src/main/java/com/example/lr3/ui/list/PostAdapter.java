package com.example.lr3.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lr3.R;
import com.example.lr3.model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.VH> {

    public interface OnItemClickListener {
        void onClick(Post post);
    }

    private final List<Post> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public PostAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submit(List<Post> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Post p = items.get(position);
        holder.tvId.setText("ID: " + p.getId());
        holder.tvTitle.setText(p.getTitle());
        String body = p.getBody() == null ? "" : p.getBody();
        holder.tvBodyPreview.setText(body);

        holder.itemView.setOnClickListener(v -> listener.onClick(p));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvId, tvTitle, tvBodyPreview;

        VH(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBodyPreview = itemView.findViewById(R.id.tvBodyPreview);
        }
    }
}
