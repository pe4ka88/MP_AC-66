package com.example.lab60,,,;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    // Константы
    private static final int REQUEST_CODE_PICK_FILE = 100;
    private static final int REQUEST_CODE_PERMISSION = 101;
    private static final String TAG = "MainActivity";

    // Координаты Бреста
    private static final double BREST_LAT = 52.0976;
    private static final double BREST_LON = 23.7341;

    // UI элементы
    private Button btnChooseFile;
    private TextView tvFileName, tvWeatherIcon, tvWeatherTemp, tvWeatherDesc;
    private CardView weatherCard;

    // Для изображений
    private ImageView imageView;
    private TextView tvImageLabel;

    // Для аудио
    private MediaPlayer mediaPlayer;
    private LinearLayout audioControls;
    private TextView tvAudioLabel;
    private Button btnPlayAudio, btnPauseAudio, btnStopAudio;

    // Для видео
    private VideoView videoView;
    private LinearLayout videoControls;
    private TextView tvVideoLabel;
    private Button btnPlayVideo, btnPauseVideo;

    // Сетевые клиенты
    private OkHttpClient client;
    private Handler mainHandler;

    // Текущий файл
    private Uri currentFileUri;
    private String currentFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация UI
        initViews();

        // Настройка сетевых клиентов
        client = new OkHttpClient();
        mainHandler = new Handler(Looper.getMainLooper());

        // Загрузка погоды
        loadWeatherForBrest();

        // Настройка кнопок
        setupButtons();
    }

    /**
     * Инициализация всех View элементов
     */
    private void initViews() {
        btnChooseFile = findViewById(R.id.btnChooseFile);
        tvFileName = findViewById(R.id.tvFileName);

        // Погода
        weatherCard = findViewById(R.id.weatherCard);
        tvWeatherIcon = findViewById(R.id.tvWeatherIcon);
        tvWeatherTemp = findViewById(R.id.tvWeatherTemp);
        tvWeatherDesc = findViewById(R.id.tvWeatherDesc);

        // Изображения
        imageView = findViewById(R.id.imageView);
        tvImageLabel = findViewById(R.id.tvImageLabel);

        // Аудио
        audioControls = findViewById(R.id.audioControls);
        tvAudioLabel = findViewById(R.id.tvAudioLabel);
        btnPlayAudio = findViewById(R.id.btnPlayAudio);
        btnPauseAudio = findViewById(R.id.btnPauseAudio);
        btnStopAudio = findViewById(R.id.btnStopAudio);

        // Видео
        videoView = findViewById(R.id.videoView);
        videoControls = findViewById(R.id.videoControls);
        tvVideoLabel = findViewById(R.id.tvVideoLabel);
        btnPlayVideo = findViewById(R.id.btnPlayVideo);
        btnPauseVideo = findViewById(R.id.btnPauseVideo);
    }

    /**
     * Настройка обработчиков кнопок
     */
    private void setupButtons() {
        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndOpenFileChooser();
            }
        });

        // Аудио кнопки
        btnPlayAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });

        btnPauseAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseAudio();
            }
        });

        btnStopAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAudio();
            }
        });

        // Видео кнопки
        btnPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo();
            }
        });

        btnPauseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseVideo();
            }
        });
    }

    // РАБОТА С РАЗРЕШЕНИЯМИ И ВЫБОРОМ ФАЙЛА


    /**
     * Проверка разрешений и открытие файлового менеджера
     */
    private void checkPermissionAndOpenFileChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ - разные разрешения для разных типов
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_AUDIO)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_VIDEO)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.READ_MEDIA_IMAGES,
                        android.Manifest.permission.READ_MEDIA_AUDIO,
                        android.Manifest.permission.READ_MEDIA_VIDEO
                }, REQUEST_CODE_PERMISSION);
            } else {
                openFileChooser();
            }
        } else {
            // Android 12 и ниже
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSION);
            } else {
                openFileChooser();
            }
        }
    }

    /**
     * Открытие системного файлового менеджера для выбора файла
     */
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Все типы файлов
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Выберите файл (Головкина В.Д.)"),
                    REQUEST_CODE_PICK_FILE);
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка открытия файлового менеджера", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Обработка результата запроса разрешений
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                openFileChooser();
            } else {
                Toast.makeText(this, "Необходимы разрешения для чтения файлов", Toast.LENGTH_LONG).show();
                // Предложить открыть настройки
                openAppSettings();
            }
        }
    }

    /**
     * Открытие настроек приложения (если пользователь отказал в разрешениях)
     */
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    /**
     * Обработка выбранного файла
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                currentFileUri = uri;
                currentFilePath = uri.toString();

                // Показываем имя файла
                String fileName = getFileName(uri);
                tvFileName.setText("Выбран файл: " + fileName);

                // Определяем тип файла и обрабатываем
                processFile(uri, fileName);
            }
        }
    }

    // Получение имени файла из URI

    private String getFileName(Uri uri) {
        String fileName = "unknown";
        String[] projection = {android.provider.MediaStore.MediaColumns.DISPLAY_NAME};

        try {
            android.database.Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.MediaColumns.DISPLAY_NAME);
                fileName = cursor.getString(nameIndex);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка получения имени файла: " + e.getMessage());
        }

        return fileName;
    }

    // ОБРАБОТКА ФАЙЛОВ ПО ТИПАМ


    //Определение типа файла и вызов соответствующей обработки

    private void processFile(Uri uri, String fileName) {
        // Скрываем все области предпросмотра
        hideAllPreviews();

        String mimeType = getContentResolver().getType(uri);
        Log.d(TAG, "MIME тип: " + mimeType + ", имя: " + fileName);

        if (mimeType != null) {
            if (mimeType.startsWith("image/")) {
                // Изображение
                showImage(uri);
            } else if (mimeType.startsWith("audio/")) {
                // Аудио
                setupAudio(uri);
            } else if (mimeType.startsWith("video/")) {
                // Видео
                setupVideo(uri);
            } else {
                Toast.makeText(this, "Неподдерживаемый тип файла: " + mimeType, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Пробуем определить по расширению
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                    fileName.endsWith(".png") || fileName.endsWith(".gif")) {
                showImage(uri);
            } else if (fileName.endsWith(".mp3") || fileName.endsWith(".wav") ||
                    fileName.endsWith(".ogg")) {
                setupAudio(uri);
            } else if (fileName.endsWith(".mp4") || fileName.endsWith(".avi") ||
                    fileName.endsWith(".mkv") || fileName.endsWith(".3gp")) {
                setupVideo(uri);
            } else {
                Toast.makeText(this, "Неподдерживаемый тип файла", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Скрыть все области предпросмотра
     */
    private void hideAllPreviews() {
        // Изображение
        imageView.setVisibility(View.GONE);
        tvImageLabel.setVisibility(View.GONE);

        // Аудио
        audioControls.setVisibility(View.GONE);
        tvAudioLabel.setVisibility(View.GONE);

        // Видео
        videoView.setVisibility(View.GONE);
        videoControls.setVisibility(View.GONE);
        tvVideoLabel.setVisibility(View.GONE);
    }

    // РАБОТА С ИЗОБРАЖЕНИЯМИ


    private void showImage(Uri uri) {
        try {
            imageView.setImageURI(uri);
            imageView.setVisibility(View.VISIBLE);
            tvImageLabel.setVisibility(View.VISIBLE);

            Toast.makeText(this, "✅ Изображение загружено", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка загрузки изображения: " + e.getMessage());
            Toast.makeText(this, "❌ Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
        }
    }

    // РАБОТА С АУДИО (MediaPlayer)


    private void setupAudio(Uri uri) {
        // Освобождаем предыдущий MediaPlayer
        releaseMediaPlayer();

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();

            // Показываем элементы управления
            audioControls.setVisibility(View.VISIBLE);
            tvAudioLabel.setVisibility(View.VISIBLE);

            // Обработка завершения воспроизведения
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Toast.makeText(MainActivity.this, "Аудио завершено", Toast.LENGTH_SHORT).show();
                }
            });

            Toast.makeText(this, "✅ Аудио загружено, нажмите Play", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.e(TAG, "Ошибка загрузки аудио: " + e.getMessage());
            Toast.makeText(this, "❌ Ошибка загрузки аудио", Toast.LENGTH_SHORT).show();
        }
    }

    private void playAudio() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Toast.makeText(this, "▶️ Воспроизведение начато", Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Toast.makeText(this, "⏸️ Воспроизведение приостановлено", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare(); // Подготовка к повторному воспроизведению
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "⏹️ Воспроизведение остановлено", Toast.LENGTH_SHORT).show();
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // РАБОТА С ВИДЕО (VideoView)


    private void setupVideo(Uri uri) {
        try {
            videoView.setVideoURI(uri);

            // Показываем элементы управления
            videoView.setVisibility(View.VISIBLE);
            videoControls.setVisibility(View.VISIBLE);
            tvVideoLabel.setVisibility(View.VISIBLE);

            // Обработка завершения видео
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Toast.makeText(MainActivity.this, "Видео завершено", Toast.LENGTH_SHORT).show();
                }
            });

            Toast.makeText(this, "✅ Видео загружено, нажмите Play", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Ошибка загрузки видео: " + e.getMessage());
            Toast.makeText(this, "❌ Ошибка загрузки видео", Toast.LENGTH_SHORT).show();
        }
    }

    private void playVideo() {
        if (!videoView.isPlaying()) {
            videoView.start();
            Toast.makeText(this, "▶️ Видео запущено", Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseVideo() {
        if (videoView.isPlaying()) {
            videoView.pause();
            Toast.makeText(this, "⏸️ Видео приостановлено", Toast.LENGTH_SHORT).show();
        }
    }

    // ПОГОДА В БРЕСТЕ (API Open-Meteo)


    private void loadWeatherForBrest() {
        String url = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=" + BREST_LAT +
                "&longitude=" + BREST_LON +
                "&current_weather=true" +
                "&timezone=Europe/Moscow";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Ошибка загрузки погоды: " + e.getMessage());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvWeatherDesc.setText("Ошибка загрузки");
                        tvWeatherIcon.setText("⚠️");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
                        JsonObject currentWeather = json.getAsJsonObject("current_weather");

                        double temperature = currentWeather.get("temperature").getAsDouble();
                        int weatherCode = currentWeather.get("weathercode").getAsInt();

                        WeatherData weather = new WeatherData(temperature, weatherCode);

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateWeatherUI(weather);
                            }
                        });

                    } catch (Exception e) {
                        Log.e(TAG, "Ошибка парсинга: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Ошибка ответа: " + response.code());
                }
            }
        });
    }

    private void updateWeatherUI(WeatherData weather) {
        tvWeatherIcon.setText(weather.getWeatherIcon());
        tvWeatherTemp.setText(String.format("%.1f°C", weather.getTemperature()));
        tvWeatherDesc.setText(weather.getWeatherDescription());
    }

    //ЖИЗНЕННЫЙ ЦИКЛ ACTIVITY


    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer(); // Освобождаем ресурсы MediaPlayer
    }
}