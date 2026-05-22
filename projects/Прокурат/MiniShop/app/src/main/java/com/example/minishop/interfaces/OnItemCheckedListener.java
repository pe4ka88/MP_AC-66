package com.example.minishop.interfaces;

import com.example.minishop.models.Product;

public interface OnItemCheckedListener {
    void onItemChecked(Product product, boolean isChecked);
}