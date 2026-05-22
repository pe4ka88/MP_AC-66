package com.example.hellocharlote;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    private ImageView characterImage;
    private TextView dialogText;
    private EditText nicknameInput;
    private LinearLayout gridButtonsLayout;
    private Switch infiniteSwitch;

    private Button btnA, btnB, btnC, btnD;

    private int step = 0;

    private String nickname = "Player";
    private String grid = "4 x 4";
    private boolean infinite = false;

    private final int[] portraits = {
            R.drawable.q84_1,
            R.drawable.q84_2,
            R.drawable.q84_3,
            R.drawable.q84_4
    };

    private Handler typeHandler = new Handler();
    private int charIndex;
    private String currentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        characterImage = findViewById(R.id.characterImage);
        dialogText = findViewById(R.id.dialogText);
        nicknameInput = findViewById(R.id.nicknameInput);
        gridButtonsLayout = findViewById(R.id.gridButtons);
        infiniteSwitch = findViewById(R.id.infiniteSwitch);

        btnA = findViewById(R.id.btnA);
        btnB = findViewById(R.id.btnB);
        btnC = findViewById(R.id.btnC);
        btnD = findViewById(R.id.btnD);

        // Клик по диалоговому окну
        findViewById(R.id.dialogBox).setOnClickListener(v -> handleStep());

        // Настройка кнопок викторины
        btnA.setOnClickListener(v -> chooseGrid("2 x 2"));
        btnB.setOnClickListener(v -> chooseGrid("3 x 3"));
        btnC.setOnClickListener(v -> chooseGrid("5 x 5"));
        btnD.setOnClickListener(v -> chooseGrid("6 x 6"));

        showCurrentStep();
    }

    private void chooseGrid(String selection) {
        grid = selection;
        gridButtonsLayout.setVisibility(View.GONE);
        step++;
        showCurrentStep();
    }

    private void handleStep() {
        switch (step) {
            case 0:
                step++;
                showCurrentStep();
                break;

            case 1: // Ввод имени
                String text = nicknameInput.getText().toString().trim();
                if (!text.isEmpty()) {
                    nickname = text;
                    nicknameInput.setVisibility(View.GONE);
                    step++;
                    showCurrentStep();
                } else {
                    Toast.makeText(this, "Введите имя, чтобы продолжить", Toast.LENGTH_SHORT).show();
                }
                break;

            case 2: // выбор сетки через кнопки, обработка в chooseGrid()
                break;

            case 3: // Бесконечный режим
                infinite = infiniteSwitch.isChecked();
                infiniteSwitch.setVisibility(View.GONE);
                step++;
                showCurrentStep();
                break;

            case 4: // старт игры
                startGame();
                break;
        }
    }

    private void showCurrentStep() {
        nicknameInput.setVisibility(View.GONE);
        gridButtonsLayout.setVisibility(View.GONE);
        infiniteSwitch.setVisibility(View.GONE);

        switch (step) {
            case 0:
                animateDialog("Снова здравствуй, куклавод.", portraits[0]);
                break;

            case 1:
                animateDialog("И каким же именем назавешься в этот раз?", portraits[1]);
                nicknameInput.setVisibility(View.VISIBLE);
                nicknameInput.requestFocus();
                break;

            case 2:
                animateDialog("На каком поле будем 'отжигать'?", portraits[2]);
                gridButtonsLayout.setVisibility(View.VISIBLE);
                break;

            case 3:
                animateDialog("Праведешь со мной побольше времени?)", portraits[3]);
                infiniteSwitch.setVisibility(View.VISIBLE);
                break;

            case 4:
                animateDialog("Ну что, вперед? Коснитесь экрана чтобы начать.", portraits[0]);
                break;
        }
    }

    // Эффект постепенной печати текста
    private void animateDialog(String text, int portraitRes) {
        setPortrait(portraitRes);

        dialogText.setText("");
        currentText = text;
        charIndex = 0;

        typeHandler.removeCallbacksAndMessages(null);
        typeHandler.postDelayed(typeRunnable, 50);

        // Подпрыгивание персонажа
        ObjectAnimator jump = ObjectAnimator.ofFloat(characterImage, "translationY", -10f, 0f);
        jump.setDuration(400);
        jump.setInterpolator(new AccelerateDecelerateInterpolator());
        jump.start();
    }

    private final Runnable typeRunnable = new Runnable() {
        @Override
        public void run() {
            if (charIndex < currentText.length()) {
                dialogText.append(String.valueOf(currentText.charAt(charIndex)));
                charIndex++;
                typeHandler.postDelayed(this, 30);
            }
        }
    };

    private void setPortrait(int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), resId, options);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bmp);
        drawable.setAntiAlias(false);
        drawable.setFilterBitmap(false); // nearest neighbor
        characterImage.setImageDrawable(drawable);
    }

    private void startGame() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("nickname", nickname);
        intent.putExtra("grid", grid);
        intent.putExtra("infinite", infinite);
        startActivity(intent);
        finish();
    }
}