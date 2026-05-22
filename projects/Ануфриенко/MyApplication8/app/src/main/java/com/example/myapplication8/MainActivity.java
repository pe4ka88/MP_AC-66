package com.example.myapplication8;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Главная Activity приложения.
 *
 * Вкладки:
 *   0 — Карта (MapFragment)
 *   1 — История (HistoryFragment)
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private MapFragment mapFragment;

    // ── Разрешения ─────────────────────────────────────────────────────
    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {
                        boolean fine = Boolean.TRUE.equals(
                                result.get(Manifest.permission.ACCESS_FINE_LOCATION));
                        if (fine) {
                            startTrackingService();
                        } else {
                            Toast.makeText(this,
                                    "Без разрешения GPS карта не будет обновляться",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

    // ── Lifecycle ──────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //DatabaseHelper.getInstance(this).clearAll();
        //DatabaseHelper.insertTestData(this);
        setContentView(R.layout.activity_main);

        // ── Отступ от выреза камеры и статус-бара ─────────────────────
        applyWindowInsets();

        // ── Вкладки ────────────────────────────────────────────────────
        setupTabs();

        // ── Запрос разрешений ──────────────────────────────────────────
        requestLocationPermissions();
    }

    // ── Отступы от выреза ──────────────────────────────────────────────

    private void applyWindowInsets() {
        // Разрешаем контенту отрисовываться под системными барами
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        } else {
            View decorView = getWindow().getDecorView();
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(flags);
        }

        // Применяем insets к корневому контейнеру
        View root = findViewById(R.id.rootContainer);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() |
                            WindowInsetsCompat.Type.displayCutout());
            // Padding: верх = статус-бар + вырез, низ = навигационная панель
            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );
            return WindowInsetsCompat.CONSUMED;
        });
    }

    // ── Вкладки ────────────────────────────────────────────────────────

    private void setupTabs() {
        viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        PagerAdapter adapter = new PagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2); // Не пересоздавать фрагменты

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("🗺 Карта");
                    break;
                case 1:
                    tab.setText("📋 История");
                    break;
            }
        }).attach();

        // При переключении на карту — обновить маркеры
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0 && mapFragment != null) {
                    mapFragment.refreshMap();
                }
            }
        });
    }

    // ── Разрешения ─────────────────────────────────────────────────────

    private void requestLocationPermissions() {
        boolean hasFine = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (hasFine) {
            startTrackingService();
        } else {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    // ── Сервис отслеживания ────────────────────────────────────────────

    private void startTrackingService() {
        Intent intent = new Intent(this, LocationTrackingService.class);
        intent.setAction(LocationTrackingService.ACTION_START);
        startForegroundService(intent);
    }

    // ── ViewPager2 Adapter ─────────────────────────────────────────────

    private class PagerAdapter extends FragmentStateAdapter {

        PagerAdapter(@NonNull FragmentActivity fa) { super(fa); }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                mapFragment = MapFragment.newInstance();
                return mapFragment;
            } else {
                return HistoryFragment.newInstance();
            }
        }

        @Override
        public int getItemCount() { return 2; }
    }
}