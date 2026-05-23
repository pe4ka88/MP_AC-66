package com.example.a1lab;

import android.content.Context;
import android.widget.ImageView;

public class CardButton extends ImageView {
    private int position;

    public CardButton(Context context, int position) {
        super(context);
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}