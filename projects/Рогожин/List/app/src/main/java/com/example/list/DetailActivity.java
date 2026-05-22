package com.example.list;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_DESC = "desc";
    public static final String EXTRA_IMAGE = "image";
    public static final String EXTRA_SUMMARY = "summary";

    public static final String EXTRA_OVERALL = "overall";
    public static final String EXTRA_BODY = "body";
    public static final String EXTRA_HARDWARE = "hardware";
    public static final String EXTRA_SOUND = "sound";
    public static final String EXTRA_VALUE = "value";

    private ImageView imgCover;
    private TextView txtName, txtSummary, txtDesc;
    private TextView txtRatings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imgCover = findViewById(R.id.imgDetailCover);
        txtName = findViewById(R.id.txtDetailTitle);
        txtSummary = findViewById(R.id.txtDetailSummary);
        txtDesc = findViewById(R.id.txtDetailDesc);
        txtRatings = findViewById(R.id.txtDetailRatings);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        String name = getIntent().getStringExtra(EXTRA_NAME);
        String desc = getIntent().getStringExtra(EXTRA_DESC);
        String image = getIntent().getStringExtra(EXTRA_IMAGE);
        String summary = getIntent().getStringExtra(EXTRA_SUMMARY);

        float overall = getIntent().getFloatExtra(EXTRA_OVERALL, 0);
        float body = getIntent().getFloatExtra(EXTRA_BODY, 0);
        float hardware = getIntent().getFloatExtra(EXTRA_HARDWARE, 0);
        float sound = getIntent().getFloatExtra(EXTRA_SOUND, 0);
        float value = getIntent().getFloatExtra(EXTRA_VALUE, 0);

        txtName.setText(name);
        txtSummary.setText(summary);
        txtDesc.setText(desc);

        String ratingsText =
                "Общий рейтинг: " + overall + "\n" +
                        "Корпус: " + body + "\n" +
                        "Фурнитура: " + hardware + "\n" +
                        "Звук: " + sound + "\n" +
                        "Цена/качество: " + value;

        txtRatings.setText(ratingsText);

        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.placeholder)
                .into(imgCover);
    }
}