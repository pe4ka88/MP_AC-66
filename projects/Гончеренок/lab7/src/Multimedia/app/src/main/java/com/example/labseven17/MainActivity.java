package com.example.labseven17;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labseven17.databinding.ActivityMainBinding;
import com.example.labseven17.ui.AboutAuthorActivity;
import com.example.labseven17.ui.AboutTaskActivity;
import com.example.labseven17.ui.AudioPlayerActivity;
import com.example.labseven17.ui.CameraActivity;
import com.example.labseven17.ui.VideoPlayerActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cardAudio.setOnClickListener(v -> open(AudioPlayerActivity.class));
        binding.cardVideo.setOnClickListener(v -> open(VideoPlayerActivity.class));
        binding.cardCamera.setOnClickListener(v -> open(CameraActivity.class));
        binding.cardTask.setOnClickListener(v -> open(AboutTaskActivity.class));
        binding.cardAuthor.setOnClickListener(v -> open(AboutAuthorActivity.class));

        playEntranceAnimation();
    }

    private void open(Class<?> destination) {
        try {
            startActivity(new Intent(this, destination));
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка открытия экрана: " + e.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
        }
    }

    private void playEntranceAnimation() {
        View[] cards = new View[]{
                binding.cardAudio,
                binding.cardVideo,
                binding.cardCamera,
                binding.cardTask,
                binding.cardAuthor
        };

        for (int i = 0; i < cards.length; i++) {
            View card = cards[i];
            card.setAlpha(0f);
            card.setTranslationY(70f);
            card.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(450)
                    .setStartDelay(i * 90L)
                    .start();
        }
    }
}