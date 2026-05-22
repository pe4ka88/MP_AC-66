package com.example.lab3

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab3.databinding.FragmentPostListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostListFragment : Fragment() {

    private var _binding: FragmentPostListBinding? = null
    private val binding get() = _binding!!

    private val postList = mutableListOf<Post>()
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PostAdapter(postList) { post ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("post_id", post.id)
            intent.putExtra("post_userId", post.userId)
            intent.putExtra("post_title", post.title)
            intent.putExtra("post_body", post.body)
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.btnLoad.setOnClickListener {
            loadPosts()
        }
    }

    private fun loadPosts() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvStatus.text = "Загрузка данных с сервера..."

        RetrofitClient.apiService.getPosts().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    postList.clear()
                    postList.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                    binding.tvStatus.text = "Данные успешно загружены. Элементов: ${postList.size}"
                } else {
                    binding.tvStatus.text = "Ошибка ответа сервера"
                    Toast.makeText(requireContext(), "Ошибка ответа сервера", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.tvStatus.text = "Не удалось загрузить данные"
                Toast.makeText(requireContext(), "Ошибка загрузки: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}