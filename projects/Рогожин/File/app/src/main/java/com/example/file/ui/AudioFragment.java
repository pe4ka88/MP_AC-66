package com.example.file.ui;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.file.R;

import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AudioFragment extends Fragment {

    private Uri currentAudioUri;
    private MediaPlayer mediaPlayer;

    private SeekBar seekAudioProgress;
    private SeekBar seekVolume;
    private WaveformView waveformView;
    private ImageButton btnPlayPause;
    private Button btnTranscribe;
    private ProgressBar progressBar;
    private ImageView imageCover;

    private Handler handler = new Handler();

    private byte[] audioData; // только для waveform
    private int audioLength;

    private static final String TAG = "AudioCover";

    public AudioFragment() {
        super(R.layout.fragment_audio);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        waveformView = view.findViewById(R.id.waveformView);
        seekAudioProgress = view.findViewById(R.id.seekAudioProgress);
        btnPlayPause = view.findViewById(R.id.btnAudioPlayPause);
        btnTranscribe = view.findViewById(R.id.btnTranscribe);
        progressBar = view.findViewById(R.id.progressBar);
        seekVolume = view.findViewById(R.id.seekVolume);
        imageCover = view.findViewById(R.id.imageCover);

        if (getArguments() != null) {
            String uriStr = getArguments().getString("fileUri");
            if (uriStr != null) {
                currentAudioUri = Uri.parse(uriStr);
                loadAudio();
            }
        }

        btnPlayPause.setOnClickListener(v -> toggleAudio());
        btnTranscribe.setOnClickListener(v -> transcribeAudio());
    }

    // =========================
    // 🎵 LOAD
    // =========================

    private void loadAudio() {
        try {
            // 🎵 MediaPlayer (оригинальный файл)
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(requireContext(), currentAudioUri);
            mediaPlayer.prepare();

            // 📊 WAV только для waveform
            File wavFile = new File(requireContext().getCacheDir(), "temp_audio.wav");
            AudioUtils.convertTo16kMono(currentAudioUri, wavFile, requireContext());

            FileInputStream fis = new FileInputStream(wavFile);
            byte[] fullData = new byte[fis.available()];
            fis.read(fullData);
            fis.close();

            int headerSize = 44;
            audioData = new byte[fullData.length - headerSize];
            System.arraycopy(fullData, headerSize, audioData, 0, audioData.length);

            audioLength = audioData.length;

            waveformView.post(() -> {
                float[] amps = generateWaveform(audioData, 200);
                waveformView.setAmplitudes(amps);
            });

            setupSeek();
            setupVolume();
            startProgressUpdater();
            loadCoverArt();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка загрузки аудио", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // =========================
    // ▶ PLAY
    // =========================

    private void toggleAudio() {
        if (mediaPlayer == null) return;

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlayPause.setImageResource(R.drawable.exo_icon_play);
        } else {
            mediaPlayer.start();
            btnPlayPause.setImageResource(R.drawable.exo_icon_pause);
        }
    }

    // =========================
    // ⏩ SEEK
    // =========================

    private void setupSeek() {
        seekAudioProgress.setMax(100);

        seekAudioProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser || mediaPlayer == null) return;

                int duration = mediaPlayer.getDuration();
                int newPos = (duration * progress) / 100;

                mediaPlayer.seekTo(newPos);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // =========================
    // 🔊 VOLUME
    // =========================

    private void setupVolume() {
        seekVolume.setMax(100);
        seekVolume.setProgress(70);

        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(volume, volume);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // =========================
    // 📊 PROGRESS
    // =========================

    private void startProgressUpdater() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.getDuration() > 0) {
                    int progress = (mediaPlayer.getCurrentPosition() * 100)
                            / mediaPlayer.getDuration();

                    seekAudioProgress.setProgress(progress);
                    waveformView.setProgress(progress);
                }
                handler.postDelayed(this, 100);
            }
        }, 0);
    }

    // =========================
    // 🖼️ COVER
    // =========================

    private void loadCoverArt() {
        try {
            android.util.Log.d(TAG, "Начинаем загрузку обложки");

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(requireContext(), currentAudioUri);

            byte[] art = mmr.getEmbeddedPicture();

            if (art != null) {
                android.util.Log.d(TAG, "Обложка найдена: " + art.length);

                android.graphics.Bitmap bitmap =
                        android.graphics.BitmapFactory.decodeByteArray(art, 0, art.length);

                imageCover.setImageBitmap(bitmap);
            } else {
                android.util.Log.w(TAG, "Обложка отсутствует");
                imageCover.setImageResource(R.drawable.ic_placeholder_cover);
            }

            mmr.release();

        } catch (Exception e) {
            android.util.Log.e(TAG, "Ошибка обложки", e);
            imageCover.setImageResource(R.drawable.ic_placeholder_cover);
        }
    }

    // =========================
    // 🧠 TRANSCRIBE
    // =========================

    private void transcribeAudio() {
        if (currentAudioUri == null) return;

        progressBar.setVisibility(View.VISIBLE);
        btnTranscribe.setEnabled(false);

        new Thread(() -> {
            try {
                File modelDir = new File(requireContext().getFilesDir(), "vosk-model-small-ru-0.22");

                if (!modelDir.exists()) {
                    unzipAsset("vosk-model-small-ru-0.22.zip", modelDir);
                }

                File actualModelDir = new File(modelDir, "vosk-model-small-ru-0.22");

                Model model = new Model(actualModelDir.getAbsolutePath());
                Recognizer recognizer = new Recognizer(model, 16000.0f);

                File wavFile = new File(requireContext().getCacheDir(), "temp_audio.wav");
                AudioUtils.convertTo16kMono(currentAudioUri, wavFile, requireContext());

                FileInputStream fis = new FileInputStream(wavFile);
                fis.skip(44);

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) > 0) {
                    recognizer.acceptWaveForm(buffer, bytesRead);
                }

                fis.close();

                String result = recognizer.getFinalResult();

                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnTranscribe.setEnabled(true);

                    new AlertDialog.Builder(requireContext())
                            .setTitle("Результат")
                            .setMessage(result)
                            .setPositiveButton("OK", null)
                            .show();
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnTranscribe.setEnabled(true);
                    Toast.makeText(getContext(), "Ошибка", Toast.LENGTH_SHORT).show();
                });

                e.printStackTrace();
            }
        }).start();
    }

    // =========================
    // 🌊 WAVEFORM
    // =========================

    private float[] generateWaveform(byte[] audioData, int samplesCount) {
        int totalSamples = audioData.length / 2;
        int samplesPerBucket = Math.max(1, totalSamples / samplesCount);

        float[] amplitudes = new float[samplesCount];

        for (int i = 0; i < samplesCount; i++) {
            int start = i * samplesPerBucket * 2;
            int end = Math.min(start + samplesPerBucket * 2, audioData.length);

            int max = 0;

            for (int j = start; j < end; j += 2) {
                short sample = (short) ((audioData[j] & 0xff) | (audioData[j + 1] << 8));
                int abs = Math.abs(sample);
                if (abs > max) max = abs;
            }

            amplitudes[i] = max;
        }

        float maxAmp = 1f;
        for (float a : amplitudes) {
            if (a > maxAmp) maxAmp = a;
        }

        for (int i = 0; i < amplitudes.length; i++) {
            amplitudes[i] = (amplitudes[i] / maxAmp) * getHeightSafe();
        }

        return amplitudes;
    }

    private float getHeightSafe() {
        return waveformView != null && waveformView.getHeight() > 0
                ? waveformView.getHeight()
                : 200f;
    }

    // =========================
    // 📦 UNZIP
    // =========================

    private void unzipAsset(String assetName, File outDir) throws Exception {
        if (!outDir.exists()) outDir.mkdirs();

        try (InputStream is = requireContext().getAssets().open(assetName);
             ZipInputStream zis = new ZipInputStream(is)) {

            ZipEntry entry;
            byte[] buffer = new byte[4096];

            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(outDir, entry.getName());

                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    File parent = outFile.getParentFile();
                    if (!parent.exists()) parent.mkdirs();

                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
}