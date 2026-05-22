package com.example.lab3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lab3.databinding.ItemPostBinding

class PostAdapter(
    private val posts: List<Post>,
    private val onClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(
        private val binding: ItemPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.tvId.text = "Запись №${post.id}"
            binding.tvTitle.text = "Информация по элементу ${post.id}"
            binding.tvBody.text = buildShortDescription(post)
            binding.tvExtra.text = "Нажмите, чтобы открыть подробную информацию"

            binding.root.setOnClickListener {
                onClick(post)
            }
        }

        private fun buildShortDescription(post: Post): String {
            return "Краткое описание: запись получена с удаленного сервера. " +
                    "Элемент относится к пользователю ${post.userId} и содержит загруженные JSON-данные."
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}