package com.example.myapplication7;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * GalleryActivity — просмотр изображений.
 * scaleType="fitCenter" — изображение занимает всю ширину, выровнено по центру.
 * Зум реализован через ViewCompat.setScaleX/Y на самом ImageView.
 */
public class GalleryActivity extends AppCompatActivity {

    private static final String TAG       = "GalleryActivity";
    private static final float  ZOOM_STEP = 0.25f;
    private static final float  ZOOM_MAX  = 4.0f;
    private static final float  ZOOM_MIN  = 1.0f;

    private ImageView        imageView;
    private TextView         tvCounter;
    private TextView         tvName;
    private TextView         tvLocation;
    private GestureDetector  gestureDetector;

    private final ArrayList<String> images       = new ArrayList<>();
    private int                     currentIndex = 0;
    private float                   scaleFactor  = 1.0f;

    private DatabaseHelper  db;
    private LocationHelper  locationHelper;

    // ──────────────────────── Lifecycle ────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gallery);

        db             = new DatabaseHelper(this);
        locationHelper = new LocationHelper(this);

        imageView  = findViewById(R.id.iv_gallery);
        tvCounter  = findViewById(R.id.tv_counter);
        tvName     = findViewById(R.id.tv_img_name);
        tvLocation = findViewById(R.id.tv_gallery_location);

        // fitCenter — растягивает до ширины экрана, сохраняя пропорции
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        tvLocation.setText(locationHelper.getLocationString());

        setupGesture();
        setupButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentIndex = 0;
        resetZoom();
        loadImages();
    }

    @Override
    protected void onPause() {
        super.onPause();
        images.clear();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    // ──────────────────────── Setup ────────────────────────

    private void setupButtons() {
        findViewById(R.id.btn_prev)        .setOnClickListener(v -> showPrevious());
        findViewById(R.id.btn_next)        .setOnClickListener(v -> showNext());
        findViewById(R.id.btn_zoom_in_g)   .setOnClickListener(v -> zoomIn());
        findViewById(R.id.btn_zoom_out_g)  .setOnClickListener(v -> zoomOut());
        findViewById(R.id.btn_gallery_back).setOnClickListener(v -> finish());
    }

    private void setupGesture() {
        gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    private static final int SWIPE_MIN_DISTANCE = 120;
                    private static final int SWIPE_MIN_VELOCITY = 100;

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velX, float velY) {
                        if (e1 == null || e2 == null) return false;
                        float dX = e2.getX() - e1.getX();
                        if (Math.abs(dX) > SWIPE_MIN_DISTANCE
                                && Math.abs(velX) > SWIPE_MIN_VELOCITY) {
                            if (dX < 0) showNext();      // свайп влево  → следующий
                            else        showPrevious();  // свайп вправо → предыдущий
                            return true;
                        }
                        return false;
                    }
                });
    }

    // ──────────────────────── Image loading ────────────────────────

    private void loadImages() {
        images.clear();

        File saveDir = new File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "TrainingMedia");

        Log.d(TAG, "Ищем снимки в: " + saveDir.getAbsolutePath());

        if (!saveDir.exists() || !saveDir.isDirectory()) {
            tvCounter.setText("Папка не найдена");
            tvName.setText(saveDir.getAbsolutePath());
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }

        File[] files = saveDir.listFiles();
        if (files != null) {
            for (File f : files) {
                String ext = getExtension(f.getName()).toLowerCase();
                if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png"))
                    images.add(f.getAbsolutePath());
            }
        }

        if (images.isEmpty()) {
            tvCounter.setText("Нет снимков");
            tvName.setText("Сделайте снимок в разделе «Камера»");
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        } else {
            showImage(0);
        }
    }

    private void showImage(int index) {
        if (images.isEmpty()) return;
        if (index < 0)             index = 0;
        if (index >= images.size()) index = images.size() - 1;
        currentIndex = index;

        String path = images.get(currentIndex);
        imageView.setImageURI(null);                          // сброс кэша URI
        imageView.setImageURI(Uri.parse("file://" + path));
        resetZoom();

        tvCounter.setText((currentIndex + 1) + " / " + images.size());
        tvName.setText(new File(path).getName());

        // Сохранить просмотр в историю
        db.addRecord("Галерея",
                "Просмотр: " + new File(path).getName(),
                locationHelper.getLatitude(),
                locationHelper.getLongitude());

        Log.d(TAG, "Показано: " + path);
    }

    private void showNext() {
        if (currentIndex + 1 < images.size()) {
            showImage(currentIndex + 1);
        } else {
            Toast.makeText(this, "Последнее изображение", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPrevious() {
        if (currentIndex > 0) {
            showImage(currentIndex - 1);
        } else {
            Toast.makeText(this, "Первое изображение", Toast.LENGTH_SHORT).show();
        }
    }

    // ──────────────────────── Zoom ────────────────────────
    // Используем View.setScaleX / setScaleY — работает поверх fitCenter

    private void zoomIn() {
        if (scaleFactor < ZOOM_MAX) {
            scaleFactor = Math.min(scaleFactor + ZOOM_STEP, ZOOM_MAX);
            applyZoom();
        }
    }

    private void zoomOut() {
        if (scaleFactor > ZOOM_MIN) {
            scaleFactor = Math.max(scaleFactor - ZOOM_STEP, ZOOM_MIN);
            applyZoom();
        }
    }

    private void applyZoom() {
        imageView.setScaleX(scaleFactor);
        imageView.setScaleY(scaleFactor);
    }

    private void resetZoom() {
        scaleFactor = 1.0f;
        imageView.setScaleX(1f);
        imageView.setScaleY(1f);
    }

    // ──────────────────────── Util ────────────────────────

    private static String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1) : "";
    }
}