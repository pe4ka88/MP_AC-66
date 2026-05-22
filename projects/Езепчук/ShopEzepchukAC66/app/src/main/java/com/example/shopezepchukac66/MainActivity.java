package com.example.shopezepchukac66;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ArrayList<Product> products;
    ProductAdapter adapter;
    TextView txtCount;
    ImageButton btnAccount;
    LinearLayout storeSection, accountSection, historySection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);

        View footer = findViewById(R.id.footerLayout);

        txtCount = footer.findViewById(R.id.txtCount);
        storeSection = footer.findViewById(R.id.sectionStore);
        accountSection = footer.findViewById(R.id.sectionAccount);
        historySection = footer.findViewById(R.id.sectionHistory);
        btnAccount = findViewById(R.id.btnAccount);
        btnAccount.setOnClickListener(v -> {
            saveCart(); // сохраняем текущую корзину
            startActivity(new Intent(MainActivity.this, ProfActivity.class));
        });
        products = new ArrayList<>();
        adapter = new ProductAdapter(this, products);
        listView.setAdapter(adapter);

        updateCounter();

        storeSection.setOnClickListener(v -> listView.smoothScrollToPosition(0));

        accountSection.setOnClickListener(v -> {

            saveCart(); // сохраняем

            startActivity(new Intent(MainActivity.this, AccountActivity.class));
        });

        historySection.setOnClickListener(v -> {

            saveCart(); // сохраняем

            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        });

        loadProducts();
    }

    private void loadProducts() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getProducts().enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    products.clear();
                    products.addAll(response.body().products);

                    ArrayList<Product> cart = CartManager.getCart();

                    for (Product p : products) {
                        p.checked = cart.contains(p);
                    }

                    adapter.notifyDataSetChanged();
                    updateCounter();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                txtCount.setText("Error loading data");
            }
        });
    }

    private void saveCart() {

        ArrayList<Product> selected = new ArrayList<>();

        for (Product p : products) {
            if (p.checked) {
                selected.add(p);
            }
        }

        CartManager.setCart(selected);
    }

    public void updateCounter() {
        int count = 0;

        for (Product p : products) {
            if (p.checked) count++;
        }

        txtCount.setText("В корзине: " + count);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<Product> cart = CartManager.getCart();

        for (Product p : products) {
            p.checked = cart.contains(p);
        }

        adapter.notifyDataSetChanged();
        updateCounter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCart(); // сохраняем при любом переходе
    }
}