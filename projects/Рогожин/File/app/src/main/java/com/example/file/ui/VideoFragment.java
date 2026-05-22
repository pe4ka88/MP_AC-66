package com.example.file.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.file.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoFragment extends Fragment {

    private PlayerView playerView;
    private ExoPlayer player;

    public VideoFragment() {
        super(R.layout.fragment_video);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        exit();
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerView = view.findViewById(R.id.playerView);

        ImageButton btnBack = view.findViewById(R.id.btnFullscreen); // используем кнопку fullscreen как “свернуть”
        btnBack.setOnClickListener(v -> exit());

        String fileUri = getArguments() != null ? getArguments().getString("fileUri") : null;
        if (fileUri == null) return;

        Uri uri = Uri.parse(fileUri);
        setupPlayer(uri);
    }

    private void setupPlayer(Uri uri) {
        player = new ExoPlayer.Builder(requireContext()).build();
        playerView.setPlayer(player);

        MediaItem item = MediaItem.fromUri(uri);
        player.setMediaItem(item);
        player.prepare();
        player.play();

        setupController();
    }

    private void setupController() {
        ImageButton btnPlayPause = playerView.findViewById(R.id.exo_play_pause);
        ImageButton btnRewind = playerView.findViewById(R.id.btnRewind);
        ImageButton btnForward = playerView.findViewById(R.id.btnForward);
        Spinner spinnerSpeed = playerView.findViewById(R.id.spinnerSpeed);
        SeekBar seekVolume = playerView.findViewById(R.id.seekVolume);
        DefaultTimeBar timeBar = playerView.findViewById(R.id.exo_progress);
        TextView tvPosition = playerView.findViewById(R.id.exo_position);
        TextView tvDuration = playerView.findViewById(R.id.exo_duration);

        btnPlayPause.setOnClickListener(v -> {
            if (player.isPlaying()) player.pause();
            else player.play();
        });

        btnRewind.setOnClickListener(v ->
                player.seekTo(Math.max(player.getCurrentPosition() - 10000, 0)));

        btnForward.setOnClickListener(v ->
                player.seekTo(player.getCurrentPosition() + 10000));

        // Скорость
        if (spinnerSpeed != null) {
            spinnerSpeed.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    String speedText = parent.getItemAtPosition(position).toString().replace("x", "").trim();
                    try {
                        float speed = Float.parseFloat(speedText);
                        player.setPlaybackParameters(new PlaybackParameters(speed));
                    } catch (Exception ignored) {}
                }
                @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            });
        }

        // Volume
        if (seekVolume != null) {
            int max = 100;
            seekVolume.setMax(max);
            seekVolume.setProgress(max / 2);
            seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    float volume = progress / (float) max;
                    player.setVolume(volume);
                }
                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }

        // Обновление позиции и длительности
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (player != null) {
                    tvPosition.setText(formatTime(player.getCurrentPosition()));
                    tvDuration.setText(formatTime(player.getDuration()));
                    playerView.postDelayed(this, 500);
                }
            }
        }, 0);
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void exit() {
        // останавливаем плеер перед выходом
        if (player != null) {
            player.pause();
        }
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
        if (!navController.popBackStack()) {
            requireActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}