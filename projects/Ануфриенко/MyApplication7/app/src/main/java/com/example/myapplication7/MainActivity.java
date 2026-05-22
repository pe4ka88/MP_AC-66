package com.example.myapplication7;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERM_REQUEST = 100;

    // На Android 10+ WRITE/READ_EXTERNAL_STORAGE не нужны для getExternalFilesDir
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO
    };

    private DatabaseHelper db;
    private LocationHelper locationHelper;
    private TextView       tvLocation;
    private TextView       tvHistoryCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db             = new DatabaseHelper(this);
        locationHelper = new LocationHelper(this);

        tvLocation     = findViewById(R.id.tv_location);
        tvHistoryCount = findViewById(R.id.tv_history_count);

        LinearLayout btnCamera  = findViewById(R.id.btn_camera);
        LinearLayout btnMedia   = findViewById(R.id.btn_media);
        LinearLayout btnGallery = findViewById(R.id.btn_gallery);

        Button btnHelp    = findViewById(R.id.btn_help);
        Button btnHistory = findViewById(R.id.btn_history);

        btnCamera .setOnClickListener(v -> openActivity(CameraActivity.class));
        btnMedia  .setOnClickListener(v -> openActivity(MediaActivity.class));
        btnGallery.setOnClickListener(v -> openActivity(GalleryActivity.class));
        btnHelp   .setOnClickListener(v -> openActivity(HelpActivity.class));
        btnHistory.setOnClickListener(v -> openActivity(HistoryActivity.class));

        checkAndRequestPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvHistoryCount.setText("Записей в истории: " + db.getCount());
        tvLocation.setText(locationHelper.getLocationString());
    }

    private void openActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    // ─── Запрашиваем только те разрешения, которые ещё не выданы ───

    private void checkAndRequestPermissions() {
        java.util.List<String> needed = new java.util.ArrayList<>();
        for (String perm : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                needed.add(perm);
            }
        }
        if (!needed.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    needed.toArray(new String[0]), PERM_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int code,
                                           @NonNull String[] perms,
                                           @NonNull int[] results) {
        super.onRequestPermissionsResult(code, perms, results);
        if (code == PERM_REQUEST) {
            for (int i = 0; i < results.length; i++) {
                if (results[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            "Отказано: " + perms[i].replace("android.permission.", ""),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}