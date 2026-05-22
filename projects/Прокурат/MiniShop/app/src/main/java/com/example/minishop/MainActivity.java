package com.example.minishop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minishop.adapters.ProductAdapter;
import com.example.minishop.interfaces.OnItemCheckedListener;
import com.example.minishop.models.Product;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnItemCheckedListener {

    private ListView listViewProducts;
    private TextView textViewCheckedCount;
    private Button buttonShowChecked;
    private ProductAdapter adapter;
    private List<Product> productList;
    private List<Product> selectedProducts;
    private int checkedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewProducts = findViewById(R.id.listViewProducts);
        selectedProducts = new ArrayList<>();

        createSampleProducts();

        adapter = new ProductAdapter(this, productList, this);

        View headerView = getLayoutInflater().inflate(R.layout.header_main, null);
        listViewProducts.addHeaderView(headerView);

        View footerView = getLayoutInflater().inflate(R.layout.footer_main, null);
        textViewCheckedCount = footerView.findViewById(R.id.textViewCheckedCount);
        buttonShowChecked = footerView.findViewById(R.id.buttonShowChecked);
        listViewProducts.addFooterView(footerView);

        listViewProducts.setAdapter(adapter);

        buttonShowChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedProducts.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Корзина пуста! Выберите товары.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
                    intent.putExtra("selected_products", new ArrayList<>(selectedProducts));
                    startActivity(intent);
                }
            }
        });
    }

    private void createSampleProducts() {
        productList = new ArrayList<>();
        productList.add(new Product(1, "Яблоко", 2.50));
        productList.add(new Product(2, "Банан", 3.20));
        productList.add(new Product(3, "Клубника", 5.90));
        productList.add(new Product(4, "Виноград", 4.75));
        productList.add(new Product(5, "Апельсин", 3.80));
        productList.add(new Product(6, "Лимон", 2.90));
        productList.add(new Product(7, "Груша", 3.40));
        productList.add(new Product(8, "Арбуз", 7.50));
    }

    @Override
    public void onItemChecked(Product product, boolean isChecked) {
        if (isChecked) {
            if (!selectedProducts.contains(product)) {
                selectedProducts.add(product);
                checkedCount++;
            }
        } else {
            selectedProducts.remove(product);
            checkedCount--;
        }

        textViewCheckedCount.setText(String.valueOf(checkedCount));
    }
}