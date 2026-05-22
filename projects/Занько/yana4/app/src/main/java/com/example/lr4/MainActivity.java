package com.example.lr4;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lr4.adapters.ProductAdapter;
import com.example.lr4.model.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private TextView tvCheckedCount;
    private Button btnShowChecked;

    private final List<Product> products = new ArrayList<>();
    private int checkedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewProducts);

        // Header / Footer
        LayoutInflater inflater = LayoutInflater.from(this);
        View header = inflater.inflate(R.layout.header_products, listView, false);
        View footer = inflater.inflate(R.layout.footer_products, listView, false);

        listView.addHeaderView(header, null, false);
        listView.addFooterView(footer, null, true);

        tvCheckedCount = footer.findViewById(R.id.tvCheckedCount);
        btnShowChecked = footer.findViewById(R.id.btnShowChecked);

        seedProducts();

        ProductAdapter adapter = new ProductAdapter(
                this,
                products,
                (product, isChecked) -> {
                    checkedCount += isChecked ? 1 : -1;
                    if (checkedCount < 0) checkedCount = 0;
                    updateFooterCount();
                }
        );

        listView.setAdapter(adapter);
        updateFooterCount();

        btnShowChecked.setOnClickListener(v -> {
            ArrayList<Product> selected = new ArrayList<>();
            for (Product p : products) {
                if (p.isChecked()) selected.add(p);
            }

            if (selected.isEmpty()) {
                Toast.makeText(this, "Ничего не выбрано (Занько Я.С., АС-66)", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = new Intent(this, CartActivity.class);
            i.putExtra(CartActivity.EXTRA_SELECTED, (Serializable) selected);
            startActivity(i);
        });
    }

    private void updateFooterCount() {
        tvCheckedCount.setText("Активировано товаров: " + checkedCount + " (АС-66)");
    }

    private void seedProducts() {
        products.clear();
        products.add(new Product(101, "Чай зелёный", 5.20));
        products.add(new Product(102, "Кофе молотый", 12.90));
        products.add(new Product(103, "Шоколад", 3.40));
        products.add(new Product(104, "Печенье", 4.10));
        products.add(new Product(105, "Молоко", 2.30));
        products.add(new Product(106, "Хлеб", 1.80));
        products.add(new Product(107, "Сыр", 9.99));
    }
}
