package com.example.minishop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CartAdapter(
    private val context: Context,
    private val items: List<Product>
) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Product = items[position]

    override fun getItemId(position: Int): Long = items[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_cart_product, parent, false)

        val tvId = view.findViewById<TextView>(R.id.tvCartProductId)
        val tvName = view.findViewById<TextView>(R.id.tvCartProductName)
        val tvPrice = view.findViewById<TextView>(R.id.tvCartProductPrice)

        val item = items[position]

        tvId.text = "ID: ${item.id}"
        tvName.text = item.name
        tvPrice.text = "Цена: %.2f руб.".format(item.price)

        return view
    }
}