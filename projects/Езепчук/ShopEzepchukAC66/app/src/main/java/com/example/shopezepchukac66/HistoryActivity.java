package com.example.shopezepchukac66;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ListView listViewHistory;
    private HistoryAdapter adapter;
    private OrderRepository orderRepository;

    ImageButton btnAccount;

    // Footer
    private LinearLayout storeSection, accountSection, historySection;
    private TextView txtCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        orderRepository = new OrderRepository(this);

        btnAccount = findViewById(R.id.btnAccount);

        btnAccount.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, ProfActivity.class);
            startActivity(intent);
        });

        // List
        listViewHistory = findViewById(R.id.listViewHistory);

        List<Order> orders = orderRepository.getOrders();
        adapter = new HistoryAdapter(this, orders);
        listViewHistory.setAdapter(adapter);

        // Footer
        View footer = findViewById(R.id.footerLayout);
        txtCount = footer.findViewById(R.id.txtCount);
        storeSection = footer.findViewById(R.id.sectionStore);
        accountSection = footer.findViewById(R.id.sectionAccount);
        historySection = footer.findViewById(R.id.sectionHistory);

        updateCounter();
        setupFooterClicks();
    }

    private void setupFooterClicks() {

        storeSection.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        accountSection.setOnClickListener(v -> {
            startActivity(new Intent(this, AccountActivity.class));
            finish();
        });

        historySection.setOnClickListener(v -> {
            // уже на этой странице
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter.updateData(orderRepository.getOrders());
        updateCounter();
    }

    private void updateCounter() {
        txtCount.setText("В корзине: " + CartManager.getCart().size());
    }
}