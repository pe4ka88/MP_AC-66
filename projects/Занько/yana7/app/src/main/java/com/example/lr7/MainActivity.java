package com.example.lr7;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private VideoView videoView;
    private ImageView imageView;
    private MediaController mediaController;
    
    private static final int PICK_AUDIO = 101;
    private static final int PICK_VIDEO = 102;
    private static final int TAKE_PHOTO = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);
        imageView = findViewById(R.id.imageView);
        
        // Инициализация контроллера
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Аудио
        findViewById(R.id.btnPickAudio).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("audio/*");
            startActivityForResult(intent, PICK_AUDIO);
        });

        findViewById(R.id.btnPlayAudio).setOnClickListener(v -> {
            if (mediaPlayer != null) mediaPlayer.start();
        });

        findViewById(R.id.btnPauseAudio).setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
        });

        // Видео
        findViewById(R.id.btnPickVideo).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
            startActivityForResult(intent, PICK_VIDEO);
        });

        findViewById(R.id.btnPlayVideo).setOnClickListener(v -> {
            videoView.start();
        });

        findViewById(R.id.btnPauseVideo).setOnClickListener(v -> {
            videoView.pause();
        });

        // Фото
        findViewById(R.id.btnMakePhoto).setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, TAKE_PHOTO);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            
            if (requestCode == PICK_AUDIO) {
                try {
                    if (mediaPlayer != null) mediaPlayer.release();
                    mediaPlayer = MediaPlayer.create(this, uri);
                    Toast.makeText(this, "Аудио Занько Я.С.. готово", Toast.LENGTH_SHORT).show();
                } catch (Exception e) { e.printStackTrace(); }
            } 
            else if (requestCode == PICK_VIDEO) {
                videoView.setVideoURI(uri);
                videoView.setOnPreparedListener(mp -> {
                    // Автоматически подгоняем размер и стартуем первый кадр
                    videoView.seekTo(1); 
                    Toast.makeText(this, "Видео АС-66 загружено", Toast.LENGTH_SHORT).show();
                });
            } 
            else if (requestCode == TAKE_PHOTO) {
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bmp);
            }
        }
    }
}
