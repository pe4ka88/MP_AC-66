package com.example.lab4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductAdapter.OnCheckedChangeListener {

    private List<Product> products;
    private TextView tvCheckedCount;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);

        // Header
        View header = getLayoutInflater().inflate(R.layout.header, listView, false);
        listView.addHeaderView(header);
        
        Button btnAbout = header.findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(v -> showAboutDialog());

        // Footer
        View footer = getLayoutInflater().inflate(R.layout.footer, listView, false);
        listView.addFooterView(footer);

        tvCheckedCount = footer.findViewById(R.id.tvCheckedCount);
        Button btnShowChecked = footer.findViewById(R.id.btnShowChecked);

        products = new ArrayList<>();
        products.add(new Product(1, "Laptop", 1000.0));
        products.add(new Product(2, "Mouse", 25.0));
        products.add(new Product(3, "Keyboard", 50.0));
        products.add(new Product(4, "Monitor", 200.0));
        products.add(new Product(5, "Headphones", 80.0));

        adapter = new ProductAdapter(this, products, this);
        listView.setAdapter(adapter);

        btnShowChecked.setOnClickListener(v -> {
            ArrayList<Product> checkedProducts = new ArrayList<>();
            for (Product p : products) {
                if (p.isChecked()) {
                    checkedProducts.add(p);
                }
            }
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            intent.putExtra("checkedItems", checkedProducts);
            startActivity(intent);
        });
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("О программе")
                .setMessage("Лабораторная работа № 4. Списки.\n" +
                        "Задание: Разработать приложение MiniShop с кастомным адаптером, Header/Footer и переходом в корзину.\n\n" +
                        "Выполнил: Ляшук В.И.\nГруппа: АС-66")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onCheckedChanged() {
        int count = 0;
        for (Product p : products) {
            if (p.isChecked()) {
                count++;
            }
        }
        tvCheckedCount.setText("Выбрано товаров: " + count);
    }
}
