package com.example.lab3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    private EditText etServerUrl;
    private Spinner spinnerPageSize;
    private CheckBox cbShowImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Настройки — Осовец");
        }

        etServerUrl = findViewById(R.id.et_server_url);
        spinnerPageSize = findViewById(R.id.spinner_page_size);
        cbShowImages = findViewById(R.id.cb_show_images);
        Button btnSave = findViewById(R.id.btn_save_settings);

        String[] pageSizes = {"10", "20", "50", "100"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, pageSizes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPageSize.setAdapter(adapter);

        loadSettings();

        btnSave.setOnClickListener(v -> saveSettings());
    }

    private void loadSettings() {
        try {
            SharedPreferences prefs = getSharedPreferences("lab3_settings", MODE_PRIVATE);
            etServerUrl.setText(prefs.getString("server_url",
                    "https://jsonplaceholder.typicode.com"));
            int pageSize = prefs.getInt("page_size", 20);

            String[] pageSizes = {"10", "20", "50", "100"};
            for (int i = 0; i < pageSizes.length; i++) {
                if (Integer.parseInt(pageSizes[i]) == pageSize) {
                    spinnerPageSize.setSelection(i);
                    break;
                }
            }

            cbShowImages.setChecked(prefs.getBoolean("show_images", true));
        } catch (Exception e) {
            Toast.makeText(this,
                    "Ошибка загрузки настроек: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveSettings() {
        try {
            String url = etServerUrl.getText().toString().trim();
            if (url.isEmpty()) {
                etServerUrl.setError("Введите URL сервера");
                return;
            }

            int pageSize = Integer.parseInt(spinnerPageSize.getSelectedItem().toString());

            SharedPreferences.Editor editor =
                    getSharedPreferences("lab3_settings", MODE_PRIVATE).edit();
            editor.putString("server_url", url);
            editor.putInt("page_size", pageSize);
            editor.putBoolean("show_images", cbShowImages.isChecked());
            editor.apply();

            Toast.makeText(this,
                    "Настройки сохранены (Осовец)", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this,
                    "Ошибка сохранения настроек: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
