package com.example.thirdlab9;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView       listView;
    private TextView       tvCheckedCount;   // badge в sticky footer (activity_main)
    private TextView       tvFooterCount;    // "Count of goods = X" в Footer ListView
    private Button         btnShowChecked;   // кнопка в sticky footer
    private Button         btnAbout;
    private View           loadingContainer;
    private List<Product>  productList = new ArrayList<>();
    private ProductAdapter adapter;

    private final FakeStoreService fakeStore = new FakeStoreService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView         = findViewById(R.id.listViewProducts);
        tvCheckedCount   = findViewById(R.id.tvCheckedCount);
        btnShowChecked   = findViewById(R.id.btnShowChecked);
        btnAbout         = findViewById(R.id.btnAbout);
        loadingContainer = findViewById(R.id.loadingContainer);

        // --- Header ListView ---
        View headerView = LayoutInflater.from(this)
                .inflate(R.layout.list_header, listView, false);
        listView.addHeaderView(headerView, null, false);

        // --- Footer ListView ---
        View footerView = LayoutInflater.from(this)
                .inflate(R.layout.list_footer, listView, false);
        tvFooterCount = footerView.findViewById(R.id.tvFooterCount);
        Button btnFooterShowChecked = footerView.findViewById(R.id.btnFooterShowChecked);
        listView.addFooterView(footerView, null, false);

        // Адаптер ПОСЛЕ addHeaderView/addFooterView
        adapter = new ProductAdapter(this, productList, (position, isChecked) -> {
            int count = 0;
            for (Product p : productList) if (p.isChecked()) count++;
            // обновляем оба индикатора
            tvCheckedCount.setText(String.valueOf(count));
            tvFooterCount.setText("Count of goods = " + count);
            animateCounter(tvCheckedCount);
        });
        listView.setAdapter(adapter);

        // Кнопка в Footer ListView
        btnFooterShowChecked.setOnClickListener(v -> openCart());

        // Кнопка в sticky footer
        btnShowChecked.setOnClickListener(v -> animateButtonPress(v, this::openCart));

        btnAbout.setOnClickListener(v -> animateButtonPress(v, () -> {
            startActivity(new Intent(this, ThirdActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }));

        tvCheckedCount.setText("0");
        tvFooterCount.setText("Count of goods = 0");
        loadProductsFromApi();
    }

    private void openCart() {
        ArrayList<Product> checked = new ArrayList<>();
        for (Product p : productList) if (p.isChecked()) checked.add(p);
        if (checked.isEmpty()) {
            Toast.makeText(this, "Выберите хотя бы один товар", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putParcelableArrayListExtra("checkedProducts", checked);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void loadProductsFromApi() {
        showLoading(true);
        fakeStore.loadProducts(new FakeStoreService.OnProductsLoadedListener() {
            @Override
            public void onSuccess(List<Product> products) {
                showLoading(false);
                productList.clear();
                productList.addAll(products);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(String message) {
                showLoading(false);
                Toast.makeText(MainActivity.this,
                        "Нет сети — загружены тестовые данные", Toast.LENGTH_SHORT).show();
                loadFallbackProducts();
            }
        });
    }

    private void loadFallbackProducts() {
        productList.clear();
        String[] names  = {
            "Fjallraven Backpack","Mens Casual T-Shirt","Mens Cotton Jacket",
            "Womens T-Shirts CTN","John Hardy Bracelet","Solid Gold Petite Ring",
            "White Gold Diamond Ring","Pierced Owl Earrings","WD HDD 1TB",
            "SanDisk SSD PLUS 1TB","Silicon Power 256GB","WD Green 240GB",
            "Samsung 49-inch Monitor","BIYLACLESEN Jacket","Lock Sadie Shoulder",
            "Rain Jacket Women","MBJ Womens Top","Opna Moisture Shirt",
            "DANVOUY Womens Shirt","Acer SB220Q Monitor"
        };
        double[] prices = {
            109.95,22.30,55.99,109.95,695.00,168.00,9.99,10.99,
            54.00,109.00,109.00,64.00,999.99,56.99,133.20,
            135.90,9.85,12.99,7.95,599.00
        };
        for (int i = 0; i < names.length; i++) {
            productList.add(new Product(i + 1, names[i], prices[i]));
        }
        adapter.notifyDataSetChanged();
    }

    private void showLoading(boolean loading) {
        if (loadingContainer != null)
            loadingContainer.setVisibility(loading ? View.VISIBLE : View.GONE);
        listView.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    private void animateCounter(TextView view) {
        ObjectAnimator sX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.5f, 1f);
        ObjectAnimator sY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.5f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(sX, sY);
        set.setDuration(300);
        set.setInterpolator(new OvershootInterpolator(3f));
        set.start();
    }

    private void animateButtonPress(View view, Runnable onEnd) {
        AnimatorSet down = makeScale(view, 1f, 0.94f, 80);
        AnimatorSet up   = makeScale(view, 0.94f, 1f, 120);
        up.setInterpolator(new OvershootInterpolator(2f));
        AnimatorSet seq = new AnimatorSet();
        seq.play(up).after(down);
        seq.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator a) { onEnd.run(); }
        });
        seq.start();
    }

    private AnimatorSet makeScale(View v, float from, float to, long dur) {
        AnimatorSet s = new AnimatorSet();
        s.playTogether(
            ObjectAnimator.ofFloat(v, "scaleX", from, to),
            ObjectAnimator.ofFloat(v, "scaleY", from, to));
        s.setDuration(dur);
        return s;
    }
}