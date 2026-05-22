package com.example.minishop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var tvCheckedCount: TextView
    private lateinit var btnShowCheckedItems: Button
    private lateinit var adapter: ProductAdapter

    private val products = mutableListOf(
        Product(1, "Смартфон", 899.99),
        Product(2, "Ноутбук", 2199.00),
        Product(3, "Наушники", 249.50),
        Product(4, "Клавиатура", 189.99),
        Product(5, "Мышь", 99.99),
        Product(6, "Монитор", 799.00),
        Product(7, "Планшет", 1299.90),
        Product(8, "Часы", 549.49),
        Product(9, "Колонка", 299.99),
        Product(10, "Веб-камера", 159.95)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listViewProducts)

        val headerView = layoutInflater.inflate(R.layout.list_header, listView, false)
        val footerView = layoutInflater.inflate(R.layout.list_footer, listView, false)

        listView.addHeaderView(headerView, null, false)
        listView.addFooterView(footerView, null, false)

        tvCheckedCount = footerView.findViewById(R.id.tvCheckedCount)
        btnShowCheckedItems = footerView.findViewById(R.id.btnShowCheckedItems)

        adapter = ProductAdapter(this, products) {
            updateCheckedCount()
        }

        listView.adapter = adapter
        updateCheckedCount()

        btnShowCheckedItems.setOnClickListener {
            val checkedItems = ArrayList(products.filter { it.checked })
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("checked_items", checkedItems)
            startActivity(intent)
        }
    }

    private fun updateCheckedCount() {
        val count = products.count { it.checked }
        tvCheckedCount.text = "Выбрано товаров: $count"
        title = "MiniShop (Гончерёнок К.А. АС-66)"
    }
}