package com.example.lab3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lab3.databinding.FragmentPostDetailBinding

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getInt("post_id", 0) ?: 0
        val userId = arguments?.getInt("post_userId", 0) ?: 0

        binding.tvAuthor.text = "Цеван Константин АС-66"
        binding.tvId.text = "Номер записи: $id"
        binding.tvUserId.text = "Пользователь: $userId"
        binding.tvTitle.text = "Подробная информация по записи"
        binding.tvBody.text = buildFullDescription(id, userId)

        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun buildFullDescription(id: Int, userId: Int): String {
        return "Описание: данная запись была загружена с удаленного сервера в формате JSON " +
                "и преобразована в объект приложения.\n\n" +
                "Элемент списка имеет номер $id и относится к пользователю $userId.\n\n" +
                "На главном экране запись отображается в виде карточки, а при выборе открывается " +
                "отдельный экран с детальной информацией внутри фрагмента."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}