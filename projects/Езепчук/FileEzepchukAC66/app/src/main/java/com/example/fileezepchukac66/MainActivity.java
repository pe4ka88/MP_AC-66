package com.example.fileezepchukac66;

import static com.example.fileezepchukac66.FullscreenVideoActivity.EXTRA_SPEED;
import static com.example.fileezepchukac66.FullscreenVideoActivity.EXTRA_VOLUME;

import android.app.AlertDialog;
import android.content.*;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.*;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.material.card.MaterialCardView;


import org.json.JSONObject;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

public class MainActivity extends AppCompatActivity {

        // ===== HF CONFIG =====
        private static final String HF_SPACE_URL = "https://huggingface.co/spaces/openai/whisper-small/run/predict";

        // ===== UI =====
        private ImageView imageView;
        private PlayerView playerView;
        private TextView tvFileInfo;
        private Uri currentVideoUri;
        private MaterialCardView videoCard;
        private MaterialCardView imageCard;
        private MaterialCardView audioLayout;
        private Button btnAudioPlayPause;
        private Button btnTranscribe;
        private static final String TAG = "TRANSCRIBE_AUDIO";
        private ProgressBar progressBar;

        // ===== Players =====
        private ExoPlayer exoPlayer;
        private MediaPlayer mediaPlayer;

        // ===== Controls =====
        private AudioManager audioManager;
        private Spinner spinnerSpeed;
        private SeekBar seekVolume;
        private ImageButton btnFullscreen;
        private ImageButton btnPlayPause;
        private ImageButton btnRewind;
        private ImageButton btnForward;
        private DefaultTimeBar timeBar;
        private SeekBar seekAudioProgress;
        private TextView tvCurrentTime, tvDuration;
        private SharedPreferences prefs;
        private static final String LAST_FILE = "last_file";
        private static final int REQ_FULLSCREEN = 102;
        private boolean isFullscreen = false;
        private long startPosition = 0;
        private float startSpeed = 1f;
        private float startVolume = 1f;
        private boolean isAudioPlaying = false;
        private Uri currentAudioUri = null;
        private static final String HF_URL = "https://api-inference.huggingface.co/models/openai/whisper-large-v3";
        private static final String HF_API_KEY = "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";
        private OkHttpClient httpClient;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            videoCard = findViewById(R.id.videoCard);
            imageCard = findViewById(R.id.imageCard);
            prefs = getSharedPreferences("media_app", MODE_PRIVATE);

            httpClient = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();

            imageView = findViewById(R.id.imageView);
            playerView = findViewById(R.id.playerView);
            tvFileInfo = findViewById(R.id.tvFileInfo);
            seekAudioProgress = findViewById(R.id.seekAudioProgress);
            tvCurrentTime = findViewById(R.id.tvCurrentTime);
            tvDuration = findViewById(R.id.tvDuration);
            audioLayout = findViewById(R.id.audioLayout);
            btnAudioPlayPause = findViewById(R.id.btnAudioPlayPause);
            btnTranscribe = findViewById(R.id.btnTranscribe);

            progressBar = findViewById(R.id.progressBar);

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            spinnerSpeed = playerView.findViewById(R.id.spinnerSpeed);
            seekVolume = playerView.findViewById(R.id.seekVolume);
            btnFullscreen = playerView.findViewById(R.id.btnFullscreen);
            btnPlayPause = playerView.findViewById(R.id.exo_play_pause);
            btnRewind = playerView.findViewById(R.id.btnRewind);
            btnForward = playerView.findViewById(R.id.btnForward);
            timeBar = playerView.findViewById(R.id.exo_progress);
            startSpeed = getIntent().getFloatExtra(EXTRA_SPEED, 1f);
            startVolume = getIntent().getFloatExtra(EXTRA_VOLUME, 1f);
            setupSpeedControl();
            setupVolumeControl();

            btnFullscreen.setOnClickListener(v -> toggleFullscreen());
            btnAudioPlayPause.setOnClickListener(v -> toggleAudio());
            btnTranscribe.setOnClickListener(v -> transcribeAudio());

            findViewById(R.id.btnSelect).setOnClickListener(v -> openFilePicker());

            String last = prefs.getString(LAST_FILE, null);
            if (last != null) handleFile(Uri.parse(last));
        }

        // ================= WHISPER =================

        private void transcribeAudio() {
            if (currentAudioUri == null) {
                Toast.makeText(this, "Нет аудио файла", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Аудио файл не выбран");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            btnTranscribe.setEnabled(false);
            Log.d(TAG, "Начало транскрипции Vosk...");

            new Thread(() -> {
                try {
                    // 1. Распаковываем ZIP или копируем assets в filesDir
                    File modelDir = new File(getFilesDir(), "vosk-model-small-ru-0.22");
                    if (!modelDir.exists() || modelDir.listFiles() == null || modelDir.listFiles().length == 0) {
                        Log.d(TAG, "Копирование модели из assets...");
                        unzipAsset("vosk-model-small-ru-0.22", modelDir);
                        Log.d(TAG, "Модель скопирована в " + modelDir.getAbsolutePath());
                    } else {
                        Log.d(TAG, "Модель уже есть: " + modelDir.getAbsolutePath());
                    }

                    // 2. Учёт структуры архива: внутренняя папка с файлами модели
                    File actualModelDir = new File(modelDir, "vosk-model-small-ru-0.22");
                    if (!actualModelDir.exists() || !new File(actualModelDir, "am").exists()) {
                        throw new IOException("Не найдены файлы модели в " + actualModelDir.getAbsolutePath());
                    }

                    // 3. Создаём Model и Recognizer
                    org.vosk.Model model = new org.vosk.Model(actualModelDir.getAbsolutePath());
                    org.vosk.Recognizer recognizer = new org.vosk.Recognizer(model, 16000.0f);
                    Log.d(TAG, "Model и Recognizer инициализированы");

                    // 4. Конвертируем аудио в WAV 16kHz моно
                    File wavFile = new File(getCacheDir(), "temp_audio.wav");
                    convertToWav(currentAudioUri, wavFile);
                    Log.d(TAG, "Конвертация аудио завершена: " + wavFile.getAbsolutePath());

                    // 5. Читаем WAV и передаём в Recognizer
                    FileInputStream fis = new FileInputStream(wavFile);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) >= 0) {
                        if (!recognizer.acceptWaveForm(buffer, bytesRead)) {
                            Log.d(TAG, "Промежуточный результат: " + recognizer.getPartialResult());
                        }
                    }
                    fis.close();

                    String result = recognizer.getFinalResult();
                    Log.d(TAG, "Распознанный текст: " + result);

                    showResult(result);

                } catch (Exception e) {
                    Log.e(TAG, "Ошибка транскрипции Vosk", e);
                    showResult("Ошибка: " + e.getMessage());
                } finally {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnTranscribe.setEnabled(true);
                    });
                }
            }).start();
        }

        /**
         * Рекурсивно выводим все файлы папки в лог (для проверки)
         */
        private void logAllFiles(File dir, String indent) {
            if (dir.isDirectory()) {
                Log.d(TAG, indent + "[DIR] " + dir.getName());
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        logAllFiles(f, indent + "  ");
                    }
                }
            } else {
                Log.d(TAG, indent + dir.getName());
            }
        }

        /**
         * Распаковывает ZIP из assets в указанную папку
         */
        private void unzipAsset(String assetName, File outDir) throws IOException {
            if (!outDir.exists()) outDir.mkdirs();

            try (InputStream is = getAssets().open(assetName);
                 java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(is)) {

                java.util.zip.ZipEntry entry;
                byte[] buffer = new byte[4096];

                while ((entry = zis.getNextEntry()) != null) {
                    File outFile = new File(outDir, entry.getName());

                    if (entry.isDirectory()) {
                        outFile.mkdirs();
                    } else {
                        // создаём родительскую папку
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


        // Метод для конвертации любого аудио Uri в WAV с 16kHz моно
        private void convertToWav(Uri inputUri, File outputFile) throws IOException {
            MediaExtractor extractor = new MediaExtractor();
            extractor.setDataSource(this, inputUri, null);
            MediaFormat format = extractor.getTrackFormat(0);
            extractor.selectTrack(0);

            String mime = format.getString(MediaFormat.KEY_MIME);
            MediaCodec codec = MediaCodec.createDecoderByType(mime);
            codec.configure(format, null, null, 0);
            codec.start();

            FileOutputStream fos = new FileOutputStream(outputFile);
            writeWavHeader(fos, 1, 16000); // моно, 16 kHz

            ByteBuffer[] inputBuffers = codec.getInputBuffers();
            ByteBuffer[] outputBuffers = codec.getOutputBuffers();
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

            boolean isEOS = false;
            ByteArrayOutputStream decodedStream = new ByteArrayOutputStream();

            // Декодируем аудио в байты PCM (стерео/частота оригинала)
            while (!isEOS) {
                int inIndex = codec.dequeueInputBuffer(10000);
                if (inIndex >= 0) {
                    ByteBuffer buffer = inputBuffers[inIndex];
                    int sampleSize = extractor.readSampleData(buffer, 0);
                    if (sampleSize < 0) {
                        codec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        isEOS = true;
                    } else {
                        codec.queueInputBuffer(inIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                        extractor.advance();
                    }
                }

                int outIndex = codec.dequeueOutputBuffer(info, 10000);
                while (outIndex >= 0) {
                    ByteBuffer outBuffer = outputBuffers[outIndex];
                    byte[] chunk = new byte[info.size];
                    outBuffer.get(chunk);
                    outBuffer.clear();
                    decodedStream.write(chunk); // сохраняем декодированные байты
                    codec.releaseOutputBuffer(outIndex, false);
                    outIndex = codec.dequeueOutputBuffer(info, 0);
                }
            }

            codec.stop();
            codec.release();
            extractor.release();

            // Ресемпл и сведение в моно 16kHz
            byte[] decodedBytes = decodedStream.toByteArray();
            int channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            byte[] mono16k = resampleTo16kMono(decodedBytes, channels, sampleRate);

            // Запись ресемплированного WAV
            fos.write(mono16k);
            fos.close();
        }

        // Ресемпл и сведение в моно
        private byte[] resampleTo16kMono(byte[] input, int channels, int sampleRate) {
            long totalSamples = ((long) input.length) / 2 / channels;
            long targetSamplesLong = totalSamples * 16000L / sampleRate;

            if (targetSamplesLong > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Audio слишком длинное для обработки");
            }

            int targetSamples = (int) targetSamplesLong;
            byte[] output = new byte[targetSamples * 2];

            int idx = 0;
            for (int i = 0; i < totalSamples; i += sampleRate / 16000) {
                int sum = 0;
                for (int ch = 0; ch < channels; ch++) {
                    int val = (input[2 * (i*channels + ch)] & 0xFF) | (input[2 * (i*channels + ch) + 1] << 8);
                    sum += val;
                }
                short monoVal = (short)(sum / channels);

                if (idx + 1 < output.length) { // защита от выхода за пределы
                    output[idx++] = (byte)(monoVal & 0xFF);
                    output[idx++] = (byte)((monoVal >> 8) & 0xFF);
                } else {
                    break;
                }
            }
            return output;
        }

        // WAV хедер (16kHz, моно)
        private void writeWavHeader(FileOutputStream out, int channels, int sampleRate) throws IOException {
            byte[] header = new byte[44];
            // RIFF
            header[0] = 'R'; header[1] = 'I'; header[2] = 'F'; header[3] = 'F';
            // Остальные поля можно оставить нулями, Vosk не требует точной длины
            out.write(header, 0, 44);
        }
        private void showResult(String text) {
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                btnTranscribe.setEnabled(true);

                new AlertDialog.Builder(this)
                        .setTitle("Результат распознавания")
                        .setMessage(text)
                        .setPositiveButton("OK", null)
                        .show();
            });
        }

        // ================= VIDEO =================

        private void setupVideoPlayer(Uri uri) {
            videoCard.setVisibility(View.VISIBLE);
            currentVideoUri = uri;
            exoPlayer = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(exoPlayer);

            MediaItem item = MediaItem.fromUri(uri);
            exoPlayer.setMediaItem(item);
            exoPlayer.prepare();
            exoPlayer.seekTo(startPosition);
            exoPlayer.setPlaybackParameters(new PlaybackParameters(startSpeed));
            exoPlayer.setVolume(startVolume);
            exoPlayer.play();
            exoPlayer.play();

            setupPlayerControls();
        }

        private void setupPlayerControls() {

            btnPlayPause.setOnClickListener(v -> {
                if (exoPlayer.isPlaying()) {
                    exoPlayer.pause();
                } else {
                    exoPlayer.play();
                }
            });

            btnRewind.setOnClickListener(v ->
                    exoPlayer.seekTo(Math.max(exoPlayer.getCurrentPosition() - 10000, 0)));

            btnForward.setOnClickListener(v ->
                    exoPlayer.seekTo(exoPlayer.getCurrentPosition() + 10000));

            timeBar.addListener(new TimeBar.OnScrubListener() {
                @Override public void onScrubStart(TimeBar timeBar, long position) {}
                @Override public void onScrubMove(TimeBar timeBar, long position) {}
                @Override public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                    exoPlayer.seekTo(position);
                }
            });
        }
        private void setupSpeedControl() {

            if (spinnerSpeed == null) return;

            spinnerSpeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (exoPlayer == null) return;

                    try {
                        String speedText = parent.getItemAtPosition(position)
                                .toString()
                                .replace("x", "")
                                .trim();

                        float speed = Float.parseFloat(speedText);
                        exoPlayer.setPlaybackParameters(new PlaybackParameters(speed));

                    } catch (Exception ignored) {}
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
        private void setupVolumeControl() {

            if (seekVolume == null) return;

            int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            seekVolume.setMax(max);
            seekVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

            seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    float volume = progress / (float) max;

                    if (exoPlayer != null) {
                        exoPlayer.setVolume(volume);
                    }

                    if (mediaPlayer != null) {
                        mediaPlayer.setVolume(volume, volume);
                    }
                }

                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    private void toggleFullscreen() {

        if (currentVideoUri == null || exoPlayer == null) return;

        long currentPosition = exoPlayer.getCurrentPosition();
        float speed = exoPlayer.getPlaybackParameters().speed;
        float volume = exoPlayer.getVolume();

        exoPlayer.pause();

        Intent intent = new Intent(this, FullscreenVideoActivity.class);
        intent.putExtra(FullscreenVideoActivity.EXTRA_URI, currentVideoUri);
        intent.putExtra(FullscreenVideoActivity.EXTRA_POSITION, currentPosition);
        intent.putExtra(EXTRA_SPEED, speed);
        intent.putExtra(EXTRA_VOLUME, volume);

        startActivityForResult(intent, REQ_FULLSCREEN);
    }

    // Получаем позицию после выхода из fullscreen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            long position = data.getLongExtra(FullscreenVideoActivity.EXTRA_POSITION, 0);

            if (exoPlayer != null) {
                exoPlayer.seekTo(position);
                exoPlayer.play();
            }
        }
    }
        // ================= AUDIO =================

        private void setupAudioPlayer(Uri uri) {
            audioLayout.setVisibility(View.VISIBLE);
            currentAudioUri = uri;

            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(this, uri);
                mediaPlayer.prepare();
                seekAudioProgress.setMax(mediaPlayer.getDuration());
                tvDuration.setText(formatTime(mediaPlayer.getDuration()));

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null) {
                            seekAudioProgress.setProgress(mediaPlayer.getCurrentPosition());
                            tvCurrentTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
                            new android.os.Handler().postDelayed(this, 500);
                        }
                    }
                }, 0);
                mediaPlayer.start();
                isAudioPlaying = true;
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
            }
        }
        private String formatTime(int millis) {
            int seconds = millis / 1000;
            int minutes = seconds / 60;
            seconds = seconds % 60;
            return String.format("%d:%02d", minutes, seconds);
        }
        private void toggleAudio() {
            if (mediaPlayer == null) return;

            if (isAudioPlaying) mediaPlayer.pause();
            else mediaPlayer.start();

            isAudioPlaying = !isAudioPlaying;
        }

        // ================= FILE PICKER =================

        private void openFilePicker() {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            filePickerLauncher.launch(intent);
        }

        private final ActivityResultLauncher<Intent> filePickerLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                Uri uri = result.getData().getData();
                                handleFile(uri);
                            }
                        });

        // ================= HANDLE FILE =================

        private void handleFile(Uri uri) {
            resetViews();
            showFileInfo(uri);

            String type = getContentResolver().getType(uri);
            if (type == null) return;

            if (type.startsWith("image")) {
                setupImage(uri);
            }
            else if (type.startsWith("video")) {
                setupVideoPlayer(uri);
            }
            else if (type.startsWith("audio")) {
                setupAudioPlayer(uri);
            }
        }

        private void setupImage(Uri uri) {
            imageCard.setVisibility(View.VISIBLE);
            imageView.setImageURI(uri);
        }
        private void showFileInfo(Uri uri) {
            tvFileInfo.setText("URI: " + uri.toString());
        }

        private void resetViews() {
            imageCard.setVisibility(View.GONE);
            videoCard.setVisibility(View.GONE);
            audioLayout.setVisibility(View.GONE);

            if (exoPlayer != null) {
                exoPlayer.release();
                exoPlayer = null;
            }

            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (exoPlayer != null) exoPlayer.release();
            if (mediaPlayer != null) mediaPlayer.release();
        }
}