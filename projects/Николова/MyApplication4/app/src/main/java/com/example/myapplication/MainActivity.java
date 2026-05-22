package com.example.myapplication;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Product> products = new ArrayList<>();

    TextView tvCount;

    ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ListView lvMain = findViewById(R.id.lvMain);

        for (int i = 1; i <= 25; i++) {
            products.add(new Product(i, "My good №" + i));
        }


        View footer = getLayoutInflater().inflate(R.layout.footer_layout, null);
        tvCount = footer.findViewById(R.id.tvCount);
        Button btnShow = footer.findViewById(R.id.btnShow);

        lvMain.addFooterView(footer);


        adapter = new ProductAdapter(this, products, null);
        lvMain.setAdapter(adapter);
        lvMain.setOnItemClickListener((parent, view, position, id) -> {

            int realPosition = position;

            if (realPosition < products.size()) {

                Product p = products.get(realPosition);
                p.checked = !p.checked;

                adapter.notifyDataSetChanged();

                updateCount();
            }
        });

        btnShow.setOnClickListener(v -> {
            ArrayList<String> selectedNames = new ArrayList<>();
            for (Product p : products) {
                if (p.checked) {

                    selectedNames.add(p.id + "    " + p.name);
                }
            }


            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putStringArrayListExtra("items", selectedNames);
            startActivity(intent);
        });
    }


    private void updateCount() {
        int count = 0;
        for (Product p : products) {
            if (p.checked) {
                count++;
            }
        }
        tvCount.setText("Count of goods = " + count);
    }
}