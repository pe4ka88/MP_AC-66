package com.example.media;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

public class PlayerActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;
    private TextView playerLabel;
    private ImageButton btnSelectFile;

    private final ActivityResultLauncher<String> filePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            showPlayer(uri);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playerView = findViewById(R.id.playerView);
        playerLabel = findViewById(R.id.playerLabel);
        btnSelectFile = findViewById(R.id.btnSelectFile);

        // Изначально скрываем плеер и подпись
        playerView.setVisibility(View.GONE);
        playerLabel.setVisibility(View.GONE);

        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        btnSelectFile.setOnClickListener(v -> filePicker.launch("*/*"));
    }

    private void showPlayer(Uri uri) {
        // Показать плеер и подпись
        playerView.setVisibility(View.VISIBLE);
        playerLabel.setVisibility(View.VISIBLE);
        playerLabel.setText("Воспроизведение файла: " + uri.getLastPathSegment());

        // Подготовка и воспроизведение
        player.setMediaItem(MediaItem.fromUri(uri));
        player.prepare();
        player.play();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}