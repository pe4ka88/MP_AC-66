package com.example.labb4new4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Product> products = new ArrayList<>();
    ProductAdapter adapter;
    TextView tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);

        // Правильное добавление Header и Footer
        View header = getLayoutInflater().inflate(R.layout.header, null);
        View footer = getLayoutInflater().inflate(R.layout.footer, null);

        listView.addHeaderView(header);
        listView.addFooterView(footer);

        tvCount = footer.findViewById(R.id.tvCount);
        Button btnShow = footer.findViewById(R.id.btnShow);

        // Данные
        products.add(new Product(1, "Laptop", 1200));
        products.add(new Product(2, "Mouse", 25));
        products.add(new Product(3, "Keyboard", 45));
        products.add(new Product(4, "Monitor", 300));
        products.add(new Product(5, "USB Cable", 10));

        adapter = new ProductAdapter(this, products, this::updateCount);
        listView.setAdapter(adapter);

        btnShow.setOnClickListener(v -> {
            ArrayList<Product> selected = new ArrayList<>();
            for (Product p : products) {
                if (p.checked) selected.add(p);
            }

            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            intent.putExtra("cart", selected);
            startActivity(intent);
        });

        updateCount();
    }

    private void updateCount() {
        int count = 0;
        for (Product p : products) if (p.checked) count++;
        tvCount.setText("Выбрано: " + count);
    }
}
