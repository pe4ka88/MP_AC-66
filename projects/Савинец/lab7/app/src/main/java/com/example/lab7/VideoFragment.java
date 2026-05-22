package com.example.lab7;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VideoFragment extends Fragment {

    private TextView tvFileName, tvCurrentTime, tvTotalTime;
    private Button btnChooseVideo;
    private ImageButton btnPlayPause, btnRewind, btnForward, btnFullscreen;
    private SeekBar seekBar;
    private VideoView videoView;

    private Handler handler = new Handler();
    private Runnable runnable;

    private DatabaseHelper dbHelper;
    private boolean isPlaying = false;
    private boolean isFullscreen = false;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    playVideo(uri);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        initViews(view);
        setupClickListeners();

        dbHelper = new DatabaseHelper(requireContext());

        return view;
    }

    private void initViews(View view) {
        tvFileName = view.findViewById(R.id.tvVideoFileName);
        tvCurrentTime = view.findViewById(R.id.tvVideoCurrentTime);
        tvTotalTime = view.findViewById(R.id.tvVideoTotalTime);
        btnChooseVideo = view.findViewById(R.id.btnChooseVideo);
        btnPlayPause = view.findViewById(R.id.btnVideoPlayPause);
        btnRewind = view.findViewById(R.id.btnVideoRewind);
        btnForward = view.findViewById(R.id.btnVideoForward);
        btnFullscreen = view.findViewById(R.id.btnVideoFullscreen);
        seekBar = view.findViewById(R.id.videoSeekBar);
        videoView = view.findViewById(R.id.videoView);
    }

    private void setupClickListeners() {
        btnChooseVideo.setOnClickListener(v -> {
            filePickerLauncher.launch("video/*");
        });

        btnPlayPause.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                isPlaying = false;
                handler.removeCallbacks(runnable);
            } else {
                videoView.start();
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                isPlaying = true;
                updateSeekBar();
            }
        });

        btnRewind.setOnClickListener(v -> {
            int newPosition = videoView.getCurrentPosition() - 10000;
            if (newPosition < 0) newPosition = 0;
            videoView.seekTo(newPosition);
        });

        btnForward.setOnClickListener(v -> {
            int newPosition = videoView.getCurrentPosition() + 10000;
            if (newPosition > videoView.getDuration()) {
                newPosition = videoView.getDuration();
            }
            videoView.seekTo(newPosition);
        });

        btnFullscreen.setOnClickListener(v -> {
            if (isFullscreen) {
                requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                btnFullscreen.setImageResource(android.R.drawable.ic_menu_view);
            } else {
                requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                btnFullscreen.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            }
            isFullscreen = !isFullscreen;
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                    tvCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        videoView.setOnCompletionListener(mp -> {
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            isPlaying = false;
            seekBar.setProgress(0);
            tvCurrentTime.setText("00:00");
            handler.removeCallbacks(runnable);
        });
    }

    private void playVideo(Uri uri) {
        try {
            videoView.setVideoURI(uri);

            String fileName = getFileName(uri);
            tvFileName.setText("Файл: " + fileName);

            // Сохраняем в историю
            try {
                dbHelper.addToHistory("video", fileName, uri.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            videoView.setOnPreparedListener(mp -> {
                seekBar.setMax(videoView.getDuration());
                tvTotalTime.setText(formatTime(videoView.getDuration()));
                videoView.start();
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                isPlaying = true;
                updateSeekBar();
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка воспроизведения видео", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSeekBar() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (videoView != null && videoView.isPlaying()) {
                    int currentPosition = videoView.getCurrentPosition();
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
        String fileName = "video_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
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
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}