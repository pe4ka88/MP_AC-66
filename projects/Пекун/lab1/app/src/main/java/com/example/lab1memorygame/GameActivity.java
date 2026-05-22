package com.example.lab1memorygame;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    // Массив drawable ресурсов для карточек
    private final int[] cardImages = {
            R.drawable.ic_apple,
            R.drawable.ic_banana,
            R.drawable.ic_cherry,
            R.drawable.ic_grapes,
            R.drawable.ic_orange,
            R.drawable.ic_pear,
            R.drawable.ic_strawberry,
            R.drawable.ic_watermelon,
            R.drawable.ic_kiwi,
            R.drawable.ic_pineapple,
            R.drawable.ic_mango,
            R.drawable.ic_peach,
            R.drawable.ic_lemon,
            R.drawable.ic_coconut,
            R.drawable.ic_avocado,
            R.drawable.ic_plum,
            R.drawable.ic_papaya,
            R.drawable.ic_pomegranate
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }
}

