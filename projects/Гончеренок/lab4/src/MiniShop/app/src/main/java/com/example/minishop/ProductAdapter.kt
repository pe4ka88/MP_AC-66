package com.example.minishop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView

class ProductAdapter(
    private val context: Context,
    private val items: MutableList<Product>,
    private val onCheckedChanged: () -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Product = items[position]

    override fun getItemId(position: Int): Long = items[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)

        val tvId = view.findViewById<TextView>(R.id.tvProductId)
        val tvName = view.findViewById<TextView>(R.id.tvProductName)
        val tvPrice = view.findViewById<TextView>(R.id.tvProductPrice)
        val checkBox = view.findViewById<CheckBox>(R.id.cbProduct)

        val item = items[position]

        tvId.text = "ID: ${item.id}"
        tvName.text = item.name
        tvPrice.text = "Цена: %.2f руб.".format(item.price)

        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = item.checked
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.checked = isChecked
            onCheckedChanged()
        }

        return view
    }
}