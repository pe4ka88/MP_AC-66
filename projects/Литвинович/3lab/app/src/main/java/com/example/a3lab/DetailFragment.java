package com.example.a3lab.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.a3lab.R;
import com.example.a3lab.models.ItemModel;

public class DetailFragment extends Fragment {
    private ImageView imageViewDetail;
    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewCategory;
    private TextView textViewPrice;
    private RatingBar ratingBar;
    private CardView cardViewShare;
    private ItemModel currentItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        imageViewDetail = view.findViewById(R.id.imageViewDetail);
        textViewTitle = view.findViewById(R.id.textViewDetailTitle);
        textViewDescription = view.findViewById(R.id.textViewDescription);
        textViewCategory = view.findViewById(R.id.textViewDetailCategory);
        textViewPrice = view.findViewById(R.id.textViewDetailPrice);
        ratingBar = view.findViewById(R.id.ratingBarDetail);
        cardViewShare = view.findViewById(R.id.cardViewShare);

        cardViewShare.setOnClickListener(v -> shareItemDetails());

        if (getArguments() != null) {
            currentItem = (ItemModel) getArguments().getSerializable("item");
            if (currentItem != null) {
                displayItem(currentItem);
            }
        }

        return view;
    }

    public void displayItem(ItemModel item) {
        this.currentItem = item;

        if (item != null && getView() != null) {
            textViewTitle.setText(item.getTitle());
            textViewDescription.setText(item.getDescription());
            textViewCategory.setText("Категория: " + item.getCategory());
            textViewPrice.setText(String.format("Цена: %.2f руб.", item.getPrice()));
            ratingBar.setRating((float) item.getRating());

            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(item.getImageUrl())
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_error)
                        .into(imageViewDetail);
            }
        }
    }

    private void shareItemDetails() {
        if (currentItem != null) {
            String shareText = "Товар: " + currentItem.getTitle() + "\n" +
                    "Описание: " + currentItem.getDescription() + "\n" +
                    "Категория: " + currentItem.getCategory() + "\n" +
                    "Цена: " + currentItem.getPrice() + " руб.\n" +
                    "Рейтинг: " + currentItem.getRating();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Поделиться через"));
        } else {
            Toast.makeText(getContext(), "Нет данных для отправки", Toast.LENGTH_SHORT).show();
        }
    }
}