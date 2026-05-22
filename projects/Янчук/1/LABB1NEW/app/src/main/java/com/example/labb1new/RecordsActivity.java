package com.example.labb1new;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labb1new.db.RecordDatabase;

import java.util.ArrayList;

public class RecordsActivity extends AppCompatActivity {

    private ListView listRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        listRecords = findViewById(R.id.listRecords);

        RecordDatabase db = new RecordDatabase(this);
        SQLiteDatabase r = db.getReadableDatabase();

        Cursor c = r.rawQuery("SELECT * FROM records ORDER BY time ASC", null);

        ArrayList<String> items = new ArrayList<>();

        while (c.moveToNext()) {
            int size = c.getInt(c.getColumnIndexOrThrow("gridSize"));
            String mode = c.getString(c.getColumnIndexOrThrow("mode"));
            int time = c.getInt(c.getColumnIndexOrThrow("time"));
            int moves = c.getInt(c.getColumnIndexOrThrow("moves"));

            items.add(size + "x" + size + " | " + mode + " | " + time + " сек | " + moves + " ходов");
        }

        c.close();
        r.close();

        listRecords.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items));
    }
}
