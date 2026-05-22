package com.example.myapplication7;

import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@SuppressWarnings("deprecation")
public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";

    private Camera camera;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private TextView tvZoom;
    private TextView tvLocation;

    private boolean previewing = false;

    private int currentZoom = 0;

    private DatabaseHelper db;
    private LocationHelper locationHelper;

    // ───────────────────────── Lifecycle ─────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Нормальный landscape
        setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        );

        setContentView(R.layout.activity_camera);

        db = new DatabaseHelper(this);

        locationHelper = new LocationHelper(this);

        tvZoom = findViewById(R.id.tv_zoom);

        tvLocation = findViewById(R.id.tv_cam_location);

        surfaceView = findViewById(R.id.surface_camera);

        surfaceHolder = surfaceView.getHolder();

        surfaceHolder.addCallback(new SurfaceCallback());

        findViewById(R.id.btn_shot)
                .setOnClickListener(v -> takeShot());

        findViewById(R.id.btn_zoom_in)
                .setOnClickListener(v -> zoomIn());

        findViewById(R.id.btn_zoom_out)
                .setOnClickListener(v -> zoomOut());

        findViewById(R.id.btn_cam_back)
                .setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {

        super.onResume();

        try {

            if (camera == null) {

                camera = Camera.open();
            }

        } catch (Exception e) {

            toast("Камера недоступна");

            finish();

            return;
        }

        currentZoom = 0;

        updateZoomLabel();

        tvLocation.setText(
                locationHelper.getLocationString()
        );
    }

    @Override
    protected void onPause() {

        super.onPause();

        releaseCamera();
    }

    // ───────────────────────── Camera ─────────────────────────

    private void releaseCamera() {

        if (camera != null) {

            try {

                camera.setPreviewCallback(null);

                if (previewing) {
                    camera.stopPreview();
                }

                camera.release();

            } catch (Exception ignored) {
            }

            camera = null;

            previewing = false;
        }
    }

    // ───────────────────────── Preview ─────────────────────────

    private class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            if (camera == null) return;

            try {

                camera.setPreviewDisplay(holder);

                // Preview ровный
                camera.setDisplayOrientation(0);

                Camera.Parameters params =
                        camera.getParameters();

                // JPEG поворачиваем
                params.setRotation(90);

                // Zoom
                if (params.isZoomSupported()) {
                    params.setZoom(currentZoom);
                }

                camera.setParameters(params);

                camera.startPreview();

                previewing = true;

            } catch (IOException e) {

                Log.e(TAG,
                        "Ошибка preview",
                        e);

                toast("Ошибка камеры");
            }
        }

        @Override
        public void surfaceChanged(
                SurfaceHolder holder,
                int format,
                int width,
                int height
        ) {
        }

        @Override
        public void surfaceDestroyed(
                SurfaceHolder holder
        ) {

            previewing = false;
        }
    }

    // ───────────────────────── Shot ─────────────────────────

    private void takeShot() {

        if (camera == null || !previewing) {
            return;
        }

        try {

            camera.autoFocus((success, cam) -> {

                try {

                    cam.takePicture(
                            null,
                            null,
                            pictureCallback
                    );

                } catch (Exception e) {

                    toast("Ошибка снимка");
                }
            });

        } catch (Exception e) {

            toast("Ошибка фокуса");
        }
    }

    private final Camera.PictureCallback pictureCallback =
            (data, cam) -> {

                new Thread(() -> {

                    try {

                        File saveDir = new File(
                                getExternalFilesDir(
                                        Environment.DIRECTORY_PICTURES
                                ),
                                "TrainingMedia"
                        );

                        if (!saveDir.exists()) {
                            saveDir.mkdirs();
                        }

                        String fileName =
                                saveDir +
                                        "/" +
                                        System.currentTimeMillis() +
                                        ".jpg";

                        FileOutputStream fos =
                                new FileOutputStream(fileName);

                        fos.write(data);

                        fos.close();

                        Log.d(TAG,
                                "Сохранено: " + fileName);

                        db.addRecord(
                                "Камера",
                                "Снимок: " +
                                        new File(fileName).getName(),
                                locationHelper.getLatitude(),
                                locationHelper.getLongitude()
                        );

                        toast("Снимок сохранён");

                        runOnUiThread(() -> {

                            try {

                                if (cam != null) {

                                    cam.startPreview();

                                    previewing = true;
                                }

                            } catch (Exception ignored) {
                            }
                        });

                    } catch (Exception e) {

                        Log.e(TAG,
                                "Ошибка сохранения",
                                e);

                        toast(
                                "Ошибка: " + e.getMessage()
                        );
                    }

                }).start();
            };

    // ───────────────────────── Zoom ─────────────────────────

    private void zoomIn() {

        if (camera == null) return;

        try {

            Camera.Parameters params =
                    camera.getParameters();

            if (!params.isZoomSupported()) {

                toast("Зум не поддерживается");

                return;
            }

            int maxZoom = params.getMaxZoom();

            if (currentZoom < maxZoom) {

                currentZoom++;

                if (params.isSmoothZoomSupported()) {

                    camera.startSmoothZoom(currentZoom);

                } else {

                    params.setZoom(currentZoom);

                    camera.setParameters(params);
                }

                updateZoomLabel();
            }

        } catch (Exception e) {

            toast("Ошибка zoom");
        }
    }

    private void zoomOut() {

        if (camera == null) return;

        try {

            Camera.Parameters params =
                    camera.getParameters();

            if (!params.isZoomSupported()) {
                return;
            }

            if (currentZoom > 0) {

                currentZoom--;

                if (params.isSmoothZoomSupported()) {

                    camera.startSmoothZoom(currentZoom);

                } else {

                    params.setZoom(currentZoom);

                    camera.setParameters(params);
                }

                updateZoomLabel();
            }

        } catch (Exception e) {

            toast("Ошибка zoom");
        }
    }

    private void updateZoomLabel() {

        tvZoom.setText(
                "Зум: " + currentZoom + "×"
        );
    }

    // ───────────────────────── Util ─────────────────────────

    private void toast(String msg) {

        runOnUiThread(() ->
                Toast.makeText(
                        CameraActivity.this,
                        msg,
                        Toast.LENGTH_SHORT
                ).show()
        );
    }
}