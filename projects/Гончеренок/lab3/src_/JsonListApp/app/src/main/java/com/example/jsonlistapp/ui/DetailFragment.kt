package com.example.jsonlistapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jsonlistapp.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getInt(ARG_ID) ?: 0
        val user = arguments?.getInt(ARG_USER) ?: 0
        val title = arguments?.getString(ARG_TITLE).orEmpty()
        val body = arguments?.getString(ARG_BODY).orEmpty()

        binding.tvDetailId.text = "ID: $id"
        binding.tvDetailUser.text = "Пользователь: $user"
        binding.tvDetailTitle.text = title
        binding.tvDetailBody.text = body

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ID = "id"
        private const val ARG_USER = "user"
        private const val ARG_TITLE = "title"
        private const val ARG_BODY = "body"

        fun newInstance(id: Int, user: Int, title: String, body: String): DetailFragment {
            val fragment = DetailFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_ID, id)
                putInt(ARG_USER, user)
                putString(ARG_TITLE, title)
                putString(ARG_BODY, body)
            }
            return fragment
        }
    }
}