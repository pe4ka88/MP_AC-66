package com.example.memorygame

import android.os.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var grid: GridLayout
    private lateinit var tvTimer: TextView
    private lateinit var tvPairs: TextView

    private var firstCard: ImageView? = null
    private var firstTag: Int = -1
    private var isBusy = false

    private var pairsLeft = 8
    private var seconds = 0

    private val handler = Handler(Looper.getMainLooper())

    private val images = mutableListOf(
        R.drawable.car1, R.drawable.car1,
        R.drawable.car2, R.drawable.car2,
        R.drawable.car3, R.drawable.car3,
        R.drawable.car4, R.drawable.car4,
        R.drawable.car5, R.drawable.car5,
        R.drawable.car6, R.drawable.car6,
        R.drawable.car7, R.drawable.car7,
        R.drawable.car8, R.drawable.car8
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        grid = findViewById(R.id.grid)
        tvTimer = findViewById(R.id.tvTimer)
        tvPairs = findViewById(R.id.tvPairs)

        findViewById<Button>(R.id.btnRestart).setOnClickListener {
            restartGame()
        }

        findViewById<Button>(R.id.btnAuthor).setOnClickListener {
            try {
                val intent = android.content.Intent(this, AuthorActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Ошибка открытия экрана автора", Toast.LENGTH_SHORT).show()
            }
        }

        startGame()
        startTimer()
    }

    private fun startGame() {
        images.shuffle()
        grid.removeAllViews()
        pairsLeft = 8
        tvPairs.text = "Осталось пар: $pairsLeft"

        for (i in images.indices) {
            val card = ImageView(this)
            card.setImageResource(R.drawable.back)
            card.tag = images[i]
            card.layoutParams = GridLayout.LayoutParams().apply {
                width = 180
                height = 180
                setMargins(8, 8, 8, 8)
            }

            card.setOnClickListener {
                if (isBusy || card == firstCard) return@setOnClickListener
                openCard(card)
            }

            grid.addView(card)
        }
    }

    private fun openCard(card: ImageView) {
        card.setImageResource(card.tag as Int)

        if (firstCard == null) {
            firstCard = card
            firstTag = card.tag as Int
        } else {
            isBusy = true
            handler.postDelayed({
                if (firstTag == card.tag as Int) {
                    card.visibility = ImageView.INVISIBLE
                    firstCard?.visibility = ImageView.INVISIBLE
                    pairsLeft--
                    tvPairs.text = "Осталось пар: $pairsLeft"
                    checkWin()
                } else {
                    card.setImageResource(R.drawable.back)
                    firstCard?.setImageResource(R.drawable.back)
                }
                firstCard = null
                isBusy = false
            }, 800)
        }
    }

    private fun startTimer() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                seconds++
                tvTimer.text = "Время: $seconds сек"
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    private fun checkWin() {
        if (pairsLeft == 0) {
            handler.removeCallbacksAndMessages(null)
            AlertDialog.Builder(this)
                .setTitle("Победа!")
                .setMessage("Вы прошли игру за $seconds секунд")
                .setPositiveButton("Ок", null)
                .show()
        }
    }

    private fun restartGame() {
        handler.removeCallbacksAndMessages(null)
        seconds = 0
        tvTimer.text = "Время: 0 сек"
        startGame()
        startTimer()
    }
}
