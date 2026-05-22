package com.example.lr4.callback;

import com.example.lr4.model.Product;

public interface OnProductCheckedChange {
    void onChanged(Product product, boolean isChecked);
}
