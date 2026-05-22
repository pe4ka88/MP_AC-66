package com.example.lab3.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.lab3.R;
import com.example.lab3.model.Displayable;
import com.example.lab3.model.Photo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DetailFragment extends Fragment {

    private static final String ARG_ITEM = "item";
    private Displayable item;

    public static DetailFragment newInstance(Displayable item) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = (Displayable) getArguments().getSerializable(ARG_ITEM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        ImageView ivImage = view.findViewById(R.id.iv_detail_image);
        TextView tvTitle = view.findViewById(R.id.tv_detail_title);
        TextView tvDetails = view.findViewById(R.id.tv_detail_info);
        Button btnShare = view.findViewById(R.id.btn_detail_share);
        Button btnSave = view.findViewById(R.id.btn_detail_save);
        Button btnBack = view.findViewById(R.id.btn_back);

        if (item != null) {
            tvTitle.setText(item.getTitle());
            tvDetails.setText(item.getDetailInfo());

            String imageUrl = item.getImageUrl();
            if (item instanceof Photo) {
                imageUrl = ((Photo) item).getFullImageUrl();
            }

            if (imageUrl != null && !imageUrl.isEmpty()) {
                ivImage.setVisibility(View.VISIBLE);
                try {
                    Glide.with(requireContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_placeholder)
                            .error(R.drawable.ic_error)
                            .into(ivImage);
                } catch (Exception e) {
                    ivImage.setImageResource(R.drawable.ic_error);
                    Toast.makeText(requireContext(),
                            "Ошибка загрузки изображения: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                ivImage.setVisibility(View.GONE);
            }

            btnShare.setOnClickListener(v -> shareItem());
            btnSave.setOnClickListener(v -> saveItem());
        }

        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void shareItem() {
        if (item == null) return;
        try {
            String text = item.getTypeName() + ": " + item.getTitle() + "\n\n" +
                    item.getDetailInfo() +
                    "\n\nОтправлено из Lab3 (Осовец А.О.)";

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                    item.getTypeName() + " #" + item.getId() + " — Осовец");
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(shareIntent, "Поделиться (Осовец)"));
        } catch (Exception e) {
            Toast.makeText(requireContext(),
                    "Ошибка отправки: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveItem() {
        if (item == null) return;
        try {
            File dir = requireContext().getExternalFilesDir(null);
            if (dir == null) {
                Toast.makeText(requireContext(),
                        "Ошибка: хранилище недоступно", Toast.LENGTH_LONG).show();
                return;
            }
            File file = new File(dir,
                    item.getTypeName().toLowerCase() + "_" + item.getId() + ".txt");
            FileWriter writer = new FileWriter(file);
            writer.write("Данные элемента — Lab3 (Осовец А.О.)\n");
            writer.write("=================================\n\n");
            writer.write(item.getDetailInfo());
            writer.write("\n\n=================================\n");
            writer.write("Сохранено приложением Lab3 (Осовец А.О.)");
            writer.flush();
            writer.close();

            Toast.makeText(requireContext(),
                    "Сохранено: " + file.getName(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(),
                    "Ошибка сохранения: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(),
                    "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
