package com.example.a4lab;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private ListView listViewCart;
    private TextView tvTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        try {
            listViewCart = findViewById(R.id.listview_cart);
            tvTotalPrice = findViewById(R.id.tv_total_price);

            // Получение выбранных товаров
            ArrayList<Product> selectedProducts = (ArrayList<Product>) getIntent().getSerializableExtra("selected_products");

            if (selectedProducts != null && !selectedProducts.isEmpty()) {
                // Используем простой ArrayAdapter для теста
                ArrayList<String> productNames = new ArrayList<>();
                double total = 0;

                for (Product product : selectedProducts) {
                    productNames.add(product.getId() + " - " + product.getName() + " - $" + product.getPrice());
                    total += product.getPrice();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productNames);
                listViewCart.setAdapter(adapter);
                tvTotalPrice.setText("Общая стоимость: $" + String.format("%.2f", total));

                Toast.makeText(this, "В корзине " + selectedProducts.size() + " товаров", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка загрузки корзины: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}