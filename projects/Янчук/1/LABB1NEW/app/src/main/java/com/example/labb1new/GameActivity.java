package com.example.labb1new;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labb1new.adapters.CardAdapter;
import com.example.labb1new.db.RecordDatabase;
import com.example.labb1new.models.Card;
import com.example.labb1new.models.GameConfig;
import com.example.labb1new.utils.ImageProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity implements CardAdapter.OnCardClickListener {

    private RecyclerView recyclerView;
    private TextView tvTimer, tvMoves, tvInfo;
    private Button btnRestart;

    private GameConfig config;
    private List<Card> cards = new ArrayList<>();
    private CardAdapter adapter;

    private int moves = 0;
    private int seconds = 0;

    private int groupSize;
    private int groupsCount;
    private int matchedGroups = 0;

    private Handler timerHandler = new Handler();
    private Handler handler = new Handler();

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds++;
            tvTimer.setText("Время: " + seconds + " сек");
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        recyclerView = findViewById(R.id.recyclerView);
        tvTimer = findViewById(R.id.tvTimer);
        tvMoves = findViewById(R.id.tvMoves);
        tvInfo = findViewById(R.id.tvInfo);
        btnRestart = findViewById(R.id.btnRestart);

        config = (GameConfig) getIntent().getSerializableExtra("config");

        recyclerView.setLayoutManager(new GridLayoutManager(this, config.getGridSize()));

        groupSize = config.getMode() == GameConfig.Mode.PAIRS ? 2 : 3;

        generateField();

        adapter = new CardAdapter(cards, this);
        recyclerView.setAdapter(adapter);

        timerHandler.postDelayed(timerRunnable, 1000);

        btnRestart.setOnClickListener(v -> restartGame());
    }

    private void restartGame() {
        seconds = 0;
        moves = 0;
        matchedGroups = 0;

        tvTimer.setText("Время: 0 сек");
        tvMoves.setText("Ходы: 0");
        tvInfo.setText("Новая игра");

        generateField();
        adapter.notifyDataSetChanged();
    }

    private void generateField() {
        cards.clear();
        matchedGroups = 0;

        int gridSize = config.getGridSize();
        int totalCells = gridSize * gridSize;

        groupsCount = totalCells / groupSize;

        int[] images = ImageProvider.getImages();
        List<Integer> imageList = new ArrayList<>();

        for (int i = 0; i < groupsCount; i++) {
            int img = images[i % images.length];
            for (int j = 0; j < groupSize; j++) {
                imageList.add(img);
            }
        }

        Collections.shuffle(imageList);

        for (int img : imageList) {
            cards.add(new Card(img, img));
        }
    }

    @Override
    public void onCardClick(int position) {
        Card clicked = cards.get(position);

        if (clicked.isMatched() || clicked.isOpen()) return;

        clicked.setOpen(true);
        adapter.notifyItemChanged(position);

        moves++;
        tvMoves.setText("Ходы: " + moves);

        handler.postDelayed(this::checkMatch, 150);
    }

    private void checkMatch() {
        List<Card> opened = new ArrayList<>();
        for (Card c : cards) {
            if (c.isOpen() && !c.isMatched()) {
                opened.add(c);
            }
        }
        if (opened.size() == 1) return;

        boolean allSame = true;
        int id = opened.get(0).getId();
        for (Card c : opened) {
            if (c.getId() != id) {
                allSame = false;
                break;
            }
        }

        if (groupSize == 2) {

            if (opened.size() == 2 && allSame) {
                tvInfo.setText("Совпадение!");
                handler.postDelayed(() -> {
                    for (Card c : opened) {
                        c.setMatched(true);
                        c.setOpen(false);
                    }
                    matchedGroups++;
                    adapter.notifyDataSetChanged();

                    if (matchedGroups == groupsCount) {
                        tvInfo.setText("Победа!");
                        saveRecord();
                    }

                }, 300);
                return;
            }

            if (opened.size() == 2 && !allSame) {
                tvInfo.setText("Не совпало");
                handler.postDelayed(() -> {
                    for (Card c : opened) c.setOpen(false);
                    adapter.notifyDataSetChanged();
                }, 400);
            }

            return;
        }

        if (opened.size() == 2) {

            if (!allSame) {
                // 2 разные → закрываем
                tvInfo.setText("Не совпало");
                handler.postDelayed(() -> {
                    for (Card c : opened) c.setOpen(false);
                    adapter.notifyDataSetChanged();
                }, 400);
            }

            return;
        }

        if (opened.size() == 3) {

            if (allSame) {

                tvInfo.setText("Совпадение!");
                handler.postDelayed(() -> {
                    for (Card c : opened) {
                        c.setMatched(true);
                        c.setOpen(false);
                    }
                    matchedGroups++;
                    adapter.notifyDataSetChanged();

                    if (matchedGroups == groupsCount) {
                        tvInfo.setText("Победа!");
                        saveRecord();
                    }

                }, 300);

            } else {
                // 3 разные → закрываем
                tvInfo.setText("Не совпало");
                handler.postDelayed(() -> {
                    for (Card c : opened) c.setOpen(false);
                    adapter.notifyDataSetChanged();
                }, 400);
            }
        }
    }

    private void saveRecord() {
        RecordDatabase db = new RecordDatabase(this);
        SQLiteDatabase w = db.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("gridSize", config.getGridSize());
        cv.put("mode", config.getMode().toString());
        cv.put("time", seconds);
        cv.put("moves", moves);

        w.insert("records", null, cv);
        w.close();
    }
}










