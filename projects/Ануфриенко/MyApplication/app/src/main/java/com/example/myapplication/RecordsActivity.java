package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class RecordsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        recyclerView = findViewById(R.id.recyclerRecords);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadRecords();
    }

    private void loadRecords() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("records")
                .orderBy("timeLeft", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<Record> list = snapshot.toObjects(Record.class);
                    recyclerView.setAdapter(new RecordsAdapter(list));
                });
    }
}