package com.example.a4lab;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductAdapter.OnProductSelectedListener {

    private ListView listView;
    private TextView tvSelectedCount;
    private ProductAdapter adapter;
    private List<Product> productList;
    private Button btnShowChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация списка товаров
        initProductList();

        // Настройка ListView
        listView = findViewById(R.id.listview_products);

        // Создание Header
        View headerView = LayoutInflater.from(this).inflate(R.layout.list_header, null);
        listView.addHeaderView(headerView);

        // Создание Footer
        View footerView = LayoutInflater.from(this).inflate(R.layout.list_footer, null);
        tvSelectedCount = footerView.findViewById(R.id.tv_selected_count);
        btnShowChecked = footerView.findViewById(R.id.btn_show_checked);
        listView.addFooterView(footerView);

        // Настройка адаптера
        adapter = new ProductAdapter(productList, getLayoutInflater(), this);
        listView.setAdapter(adapter);

        // Обработчик кнопки Show Checked Items
        btnShowChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCart();
            }
        });

        // Обновление счетчика
        updateSelectedCount();
    }

    private void initProductList() {
        productList = new ArrayList<>();
        productList.add(new Product(101, "Смартфон Vivo X300", 999.99));
        productList.add(new Product(102, "Ноутбук Pro m5", 2299.99));
        productList.add(new Product(103, "Беспроводные наушники Buds 2 PRO", 49.99));
        productList.add(new Product(104, "Фитнес-браслет SmartBand 10", 39.99));
        productList.add(new Product(105, "Планшет Galaxy Tab S10", 1499.99));
        productList.add(new Product(106, "Внешний SSD 1TB", 319.99));
    }

    private void updateSelectedCount() {
        int count = 0;
        for (Product product : productList) {
            if (product.isSelected()) {
                count++;
            }
        }
        tvSelectedCount.setText("Выбрано товаров: " + count);
    }

    private void showCart() {
        try {
            ArrayList<Product> selectedProducts = new ArrayList<>();
            for (Product product : productList) {
                if (product.isSelected()) {
                    selectedProducts.add(product);
                }
            }

            if (selectedProducts.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, выберите хотя бы один товар", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                intent.putExtra("selected_products", selectedProducts);
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onProductSelectedChanged() {
        updateSelectedCount();
    }
}