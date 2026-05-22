package com.example.thirdlab8.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.thirdlab8.R;
import com.example.thirdlab8.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter для отображения списка пользователей в RecyclerView
 * Обновлён в стиле Т-Банка
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    
    private List<User> users = new ArrayList<>();
    private OnUserClickListener listener;
    
    public interface OnUserClickListener {
        void onUserClick(User user);
    }
    
    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }
    
    public void setUsers(List<User> users) {
        this.users = users != null ? users : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(users.get(position));
    }
    
    @Override
    public int getItemCount() {
        return users.size();
    }
    
    class UserViewHolder extends RecyclerView.ViewHolder {
        private final ImageView avatarImageView;
        private final TextView nameTextView;
        private final TextView emailTextView;
        private final TextView companyTextView;
        private final TextView idBadge;
        
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            companyTextView = itemView.findViewById(R.id.companyTextView);
            idBadge = itemView.findViewById(R.id.idBadge);
        }
        
        public void bind(User user) {
            // Имя пользователя
            nameTextView.setText(user.getName());
            
            // Email
            emailTextView.setText(user.getEmail());
            
            // Компания с проверкой на пустоту
            String company = user.getCompany();
            if (company != null && !company.isEmpty()) {
                companyTextView.setText("🏢 " + company);
                companyTextView.setVisibility(View.VISIBLE);
            } else {
                companyTextView.setVisibility(View.GONE);
            }
            
            // ID бейдж в стиле Т-Банка
            idBadge.setText("#" + user.getId());
            
            // Загрузка аватара через Glide
            String avatarUrl = user.getAvatar();
            
            Glide.with(itemView.getContext())
                    .load(avatarUrl)
                    .circleCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(avatarImageView);
            
            // Обработка клика
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClick(user);
                }
            });
        }
    }
}
