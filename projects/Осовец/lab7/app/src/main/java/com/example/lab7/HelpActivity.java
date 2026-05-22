package com.example.lab7;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab7.data.HistoryLogger;
import com.example.lab7.ui.UiHelpers;

public class HelpActivity extends AppCompatActivity {

    private TextView bodyText;
    private float textSize = 15f;
    private int sectionIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bodyText = findViewById(R.id.textTaskBody);

        UiHelpers.bindBack(this);
        UiHelpers.bindAuthorButton(this);

        findViewById(R.id.btnShowTask).setOnClickListener(v -> {
            sectionIndex = 0;
            renderSection();
        });

        findViewById(R.id.btnShowScenario).setOnClickListener(v -> {
            sectionIndex = 1;
            renderSection();
        });

        findViewById(R.id.btnShowOwner).setOnClickListener(v -> {
            sectionIndex = 2;
            renderSection();
        });

        findViewById(R.id.btnHelpZoomIn).setOnClickListener(v -> {
            textSize = Math.min(24f, textSize + 1f);
            bodyText.setTextSize(textSize);
            HistoryLogger.log(this, "HELP", "ZOOM_IN");
        });

        findViewById(R.id.btnHelpZoomOut).setOnClickListener(v -> {
            textSize = Math.max(12f, textSize - 1f);
            bodyText.setTextSize(textSize);
            HistoryLogger.log(this, "HELP", "ZOOM_OUT");
        });

        renderSection();
    }

    private void renderSection() {
        if (sectionIndex == 0) {
            bodyText.setText(getString(R.string.task_full_text));
            HistoryLogger.log(this, "HELP", "SHOW_TASK");
            return;
        }
        if (sectionIndex == 1) {
            bodyText.setText(getString(R.string.defense_scenario));
            HistoryLogger.log(this, "HELP", "SHOW_SCENARIO");
            return;
        }
        bodyText.setText(getString(R.string.task_owner));
        HistoryLogger.log(this, "HELP", "SHOW_OWNER");
    }
}
