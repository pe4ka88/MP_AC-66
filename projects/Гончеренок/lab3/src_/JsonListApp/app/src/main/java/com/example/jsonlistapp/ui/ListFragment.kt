package com.example.jsonlistapp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jsonlistapp.databinding.FragmentListBinding
import com.example.jsonlistapp.model.Post
import com.example.jsonlistapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListFragment : Fragment() {

    interface OnPostClickListener {
        fun onPostClick(postId: Int, userId: Int, title: String, body: String)
    }

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val postList = mutableListOf<Post>()
    private lateinit var adapter: PostAdapter
    private var listener: OnPostClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPostClickListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PostAdapter(postList) {
            listener?.onPostClick(it.id, it.userId, it.title, it.body)
        }

        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPosts.adapter = adapter

        binding.btnLoadData.setOnClickListener {
            loadPosts()
        }

        binding.btnClear.setOnClickListener {
            postList.clear()
            adapter.notifyDataSetChanged()
        }
    }

    private fun loadPosts() {
        binding.progressBar.visibility = View.VISIBLE

        RetrofitClient.apiService.getPosts().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    postList.clear()
                    postList.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Данные загружены", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Ошибка сети", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}