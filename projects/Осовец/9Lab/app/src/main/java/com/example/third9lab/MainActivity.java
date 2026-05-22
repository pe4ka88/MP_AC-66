package com.example.third9lab;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.third9lab.adapters.GoodsAdapter;
import com.example.third9lab.api.ApiClient;
import com.example.third9lab.api.ProductResponse;
import com.example.third9lab.models.Good;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private List<Good> goods;
    private GoodsAdapter adapter;
    private TextView tvCheckedCount;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        goods = new ArrayList<>();

        progressBar = findViewById(R.id.progress_bar);

        ListView lvGoods = findViewById(R.id.lv_goods);

        // Inflate header and footer
        LayoutInflater inflater = LayoutInflater.from(this);
        View headerView = inflater.inflate(R.layout.header_mygoods, lvGoods, false);
        View footerView = inflater.inflate(R.layout.footer_mygoods, lvGoods, false);

        tvCheckedCount = footerView.findViewById(R.id.tv_checked_count);
        Button btnShowChecked = footerView.findViewById(R.id.btn_show_checked);

        // Must add header/footer before setAdapter
        lvGoods.addHeaderView(headerView, null, false);
        lvGoods.addFooterView(footerView, null, false);

        adapter = new GoodsAdapter(this, goods, checkedCount ->
                tvCheckedCount.setText("Count of goods = " + checkedCount)
        );
        lvGoods.setAdapter(adapter);

        btnShowChecked.setOnClickListener(v -> {
            ArrayList<Good> checkedGoods = new ArrayList<>();
            for (Good g : goods) {
                if (g.isChecked()) {
                    checkedGoods.add(g);
                }
            }
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra("checked_goods", checkedGoods);
            startActivity(intent);
        });

        Button btnTaskInfo = footerView.findViewById(R.id.btn_task_info);
        btnTaskInfo.setOnClickListener(v -> showTaskInfoDialog());

        loadProductsFromApi();
    }

    private void loadProductsFromApi() {
        progressBar.setVisibility(View.VISIBLE);

        ApiClient.getService().getProducts().enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call,
                                   Response<List<ProductResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    goods.clear();
                    for (ProductResponse p : response.body()) {
                        goods.add(new Good(p.getId(), p.getTitle(), p.getPrice()));
                    }
                    adapter.notifyDataSetChanged();
                    tvCheckedCount.setText("Count of goods = 0");
                } else {
                    Toast.makeText(MainActivity.this,
                            "Ошибка загрузки: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,
                        "Нет соединения: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showTaskInfoDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.task_title))
                .setMessage(getString(R.string.task_body))
                .setPositiveButton("OK", null)
                .show();
    }
}