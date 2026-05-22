package com.example.lab7;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AudioFragment extends Fragment {

    private TextView tvFileName, tvCurrentTime, tvTotalTime;
    private Button btnChooseAudio;
    private ImageButton btnPlayPause, btnRewind, btnForward, btnVolumeDown, btnVolumeUp;
    private SeekBar seekBar, volumeSeekBar;

    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable runnable;

    private DatabaseHelper dbHelper;
    private boolean isPlaying = false;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    playAudio(uri);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio, container, false);

        initViews(view);
        setupClickListeners();

        dbHelper = new DatabaseHelper(requireContext());

        return view;
    }

    private void initViews(View view) {
        tvFileName = view.findViewById(R.id.tvAudioFileName);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        tvTotalTime = view.findViewById(R.id.tvTotalTime);
        btnChooseAudio = view.findViewById(R.id.btnChooseAudio);
        btnPlayPause = view.findViewById(R.id.btnPlayPause);
        btnRewind = view.findViewById(R.id.btnRewind);
        btnForward = view.findViewById(R.id.btnForward);
        btnVolumeDown = view.findViewById(R.id.btnVolumeDown);
        btnVolumeUp = view.findViewById(R.id.btnVolumeUp);
        seekBar = view.findViewById(R.id.seekBar);
        volumeSeekBar = view.findViewById(R.id.volumeSeekBar);
    }

    private void setupClickListeners() {
        btnChooseAudio.setOnClickListener(v -> {
            filePickerLauncher.launch("audio/*");
        });

        btnPlayPause.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    pauseAudio();
                } else {
                    playAudio();
                }
            } else {
                Toast.makeText(getContext(), "Сначала выберите аудиофайл", Toast.LENGTH_SHORT).show();
            }
        });

        btnRewind.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                int newPosition = mediaPlayer.getCurrentPosition() - 10000;
                if (newPosition < 0) newPosition = 0;
                mediaPlayer.seekTo(newPosition);
                updateSeekBar();
            }
        });

        btnForward.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                int newPosition = mediaPlayer.getCurrentPosition() + 10000;
                if (newPosition > mediaPlayer.getDuration()) {
                    newPosition = mediaPlayer.getDuration();
                }
                mediaPlayer.seekTo(newPosition);
                updateSeekBar();
            }
        });

        btnVolumeDown.setOnClickListener(v -> {
            int progress = volumeSeekBar.getProgress();
            volumeSeekBar.setProgress(Math.max(0, progress - 10));
        });

        btnVolumeUp.setOnClickListener(v -> {
            int progress = volumeSeekBar.getProgress();
            volumeSeekBar.setProgress(Math.min(100, progress + 10));
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    tvCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null) {
                    float volume = progress / 100f;
                    mediaPlayer.setVolume(volume, volume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void playAudio(Uri uri) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(requireContext(), uri);
            mediaPlayer.prepare();

            String fileName = getFileName(uri);
            tvFileName.setText("Файл: " + fileName);

            // Сохраняем в историю
            try {
                dbHelper.addToHistory("audio", fileName, uri.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            setupMediaPlayer();
            playAudio();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupMediaPlayer() {
        seekBar.setMax(mediaPlayer.getDuration());
        tvTotalTime.setText(formatTime(mediaPlayer.getDuration()));

        mediaPlayer.setOnCompletionListener(mp -> {
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            isPlaying = false;
            seekBar.setProgress(0);
            tvCurrentTime.setText("00:00");
            handler.removeCallbacks(runnable);
        });
    }

    private void playAudio() {
        mediaPlayer.start();
        btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
        isPlaying = true;
        updateSeekBar();
    }

    private void pauseAudio() {
        mediaPlayer.pause();
        btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
        isPlaying = false;
        handler.removeCallbacks(runnable);
    }

    private void updateSeekBar() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    tvCurrentTime.setText(formatTime(currentPosition));
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private String formatTime(int milliseconds) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    private String getFileName(Uri uri) {
        String fileName = "audio_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        try {
            android.database.Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(android.provider.MediaStore.MediaColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    fileName = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(runnable);
    }
}