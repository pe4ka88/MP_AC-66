package com.example.labseven17.ui;

import android.view.View;
import android.widget.AdapterView;

public class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {

    public interface OnPositionSelected {
        void onSelected(int position);
    }

    private final OnPositionSelected callback;

    public SimpleItemSelectedListener(OnPositionSelected callback) {
        this.callback = callback;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        callback.onSelected(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // No-op.
    }
}
