package com.example.minishop

import android.os.Build
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CartActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var tvCartTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        listView = findViewById(R.id.listViewCart)
        tvCartTitle = findViewById(R.id.tvCartTitle)

        val items = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("checked_items", ArrayList::class.java) as? ArrayList<Product>
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("checked_items") as? ArrayList<Product>
        } ?: arrayListOf()

        tvCartTitle.text = "Корзина товаров (${items.size})"
        listView.adapter = CartAdapter(this, items)
        title =""
    }
}