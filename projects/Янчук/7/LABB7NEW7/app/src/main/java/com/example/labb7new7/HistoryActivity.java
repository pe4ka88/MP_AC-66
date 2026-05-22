package com.example.labb7new7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ListView listView = findViewById(R.id.historyList);



        HistoryManager manager = new HistoryManager(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                manager.getHistory()
        );

        listView.setAdapter(adapter);

        Button btnExport = findViewById(R.id.btnExport);
        btnExport.setOnClickListener(v -> exportHistory());
    }

    private void exportHistory() {
        try {
            // Получаем историю
            HistoryManager manager = new HistoryManager(this);
            ArrayList<String> history = manager.getHistory();

            if (history.isEmpty()) {
                Toast.makeText(this, "История пуста", Toast.LENGTH_SHORT).show();
                return;
            }

            // Создаём текст
            StringBuilder builder = new StringBuilder();
            for (String item : history) {
                builder.append(item).append("\n\n");
            }

            // Создаём файл
            File file = new File(getExternalFilesDir(null), "history.txt");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(builder.toString().getBytes());
            fos.close();

            // Открываем меню "Поделиться"
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    file
            ));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Отправить файл"));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка экспорта", Toast.LENGTH_SHORT).show();
        }
    }


}
