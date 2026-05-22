package com.example.minishop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoodsAdapter.OnCheckedChangeListener {

    private ListView listViewGoods;
    private TextView tvSelectedCount;
    private Button btnShowCheckedItems;

    private ArrayList<Good> goodsList;
    private GoodsAdapter goodsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewGoods = findViewById(R.id.listViewGoods);

        // Header
        LayoutInflater inflater = getLayoutInflater();
        View headerView = inflater.inflate(R.layout.list_header, listViewGoods, false);

        // Footer
        View footerView = inflater.inflate(R.layout.list_footer, listViewGoods, false);

        tvSelectedCount = footerView.findViewById(R.id.tvSelectedCount);
        btnShowCheckedItems = footerView.findViewById(R.id.btnShowCheckedItems);

        listViewGoods.addHeaderView(headerView, null, false);
        listViewGoods.addFooterView(footerView, null, false);

        goodsList = new ArrayList<>();
        fillGoods();

        goodsAdapter = new GoodsAdapter(this, goodsList, this);
        listViewGoods.setAdapter(goodsAdapter);

        updateSelectedCount();

        btnShowCheckedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<Good> checkedGoods = new ArrayList<>();

                for (Good good : goodsList) {
                    if (good.isChecked()) {
                        checkedGoods.add(good);
                    }
                }

                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                intent.putParcelableArrayListExtra("checked_goods", checkedGoods);
                startActivity(intent);
            }
        });
    }

    private void fillGoods() {
        goodsList.add(new Good(1, "Смартфон Samsung", 1200.0, false));
        goodsList.add(new Good(2, "Наушники Sony", 350.0, false));
        goodsList.add(new Good(3, "Power Bank Xiaomi", 95.0, false));
        goodsList.add(new Good(4, "Смарт-часы Huawei", 480.0, false));
        goodsList.add(new Good(5, "Клавиатура Logitech", 210.0, false));
        goodsList.add(new Good(6, "Мышь A4Tech", 75.0, false));
        goodsList.add(new Good(7, "Планшет Lenovo", 890.0, false));
        goodsList.add(new Good(8, "Флешка Kingston 64GB", 40.0, false));
    }

    private void updateSelectedCount() {
        int count = 0;

        for (Good good : goodsList) {
            if (good.isChecked()) {
                count++;
            }
        }

        tvSelectedCount.setText("Количество выбранных товаров: " + count);
    }

    @Override
    public void onCheckedChanged() {
        updateSelectedCount();
    }
}