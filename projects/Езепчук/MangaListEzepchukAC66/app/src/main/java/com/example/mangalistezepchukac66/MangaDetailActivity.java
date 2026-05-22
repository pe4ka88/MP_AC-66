package com.example.mangalistezepchukac66;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MangaDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_DESC = "desc";
    public static final String EXTRA_COVER = "cover";
    public static final String EXTRA_GENRES = "genres";
    public static final String EXTRA_RATING = "rating";

    private ImageView imgCover;
    private TextView txtTitle, txtDesc, txtGenres, txtRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_detail);

        imgCover = findViewById(R.id.imgDetailCover);
        txtTitle = findViewById(R.id.txtDetailTitle);
        txtDesc = findViewById(R.id.txtDetailDesc);
        txtGenres = findViewById(R.id.txtDetailGenres);
        txtRating = findViewById(R.id.txtDetailRating);

        // Кнопка назад
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String desc = getIntent().getStringExtra(EXTRA_DESC);
        String cover = getIntent().getStringExtra(EXTRA_COVER);
        String genres = getIntent().getStringExtra(EXTRA_GENRES);
        String rating = getIntent().getStringExtra(EXTRA_RATING);

        txtTitle.setText(title);
        txtDesc.setText(desc);
        txtGenres.setText("Жанры: " + genres);
        txtRating.setText("Возрастной рейтинг: " + rating);

        Glide.with(this)
                .load(cover)
                .placeholder(R.drawable.placeholder)
                .into(imgCover);
    }
}
