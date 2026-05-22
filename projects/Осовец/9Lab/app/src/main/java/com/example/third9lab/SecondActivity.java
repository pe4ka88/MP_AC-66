package com.example.third9lab;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.third9lab.adapters.CheckedGoodsAdapter;
import com.example.third9lab.models.Good;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView tvCartTitle = findViewById(R.id.tv_cart_title);
        ListView lvCheckedGoods = findViewById(R.id.lv_checked_goods);
        Button btnTaskInfo = findViewById(R.id.btn_task_info);
        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        ArrayList<Good> checkedGoods = (ArrayList<Good>) getIntent().getSerializableExtra("checked_goods");

        if (checkedGoods == null) {
            checkedGoods = new ArrayList<>();
        }

        tvCartTitle.setText("В Вашей корзине " + checkedGoods.size() + " товар(ов):");

        CheckedGoodsAdapter adapter = new CheckedGoodsAdapter(this, checkedGoods);
        lvCheckedGoods.setAdapter(adapter);

        btnTaskInfo.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.task_title))
                        .setMessage(getString(R.string.task_body))
                        .setPositiveButton("OK", null)
                        .show()
        );
    }
}
