package com.example.taxi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UserActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvRoute: TextView
    private lateinit var btnSetPath: Button
    private lateinit var btnCallTaxi: Button
    private lateinit var btnOpenTaskUser: Button
    private lateinit var btnBackToMainUser: Button

    companion object {
        const val TAG = "UserActivity"
        const val REQUEST_CODE_PATH = 100

        const val EXTRA_ROUTE_FROM = "extra_route_from"
        const val EXTRA_ROUTE_TO = "extra_route_to"
        const val EXTRA_CITY = "extra_city"
        const val EXTRA_START_STREET = "extra_start_street"
        const val EXTRA_END_STREET = "extra_end_street"
        const val EXTRA_COMMENT = "extra_comment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_user)

        initViews()
        showUserData()
        setListeners()
    }

    private fun initViews() {
        tvUserName = findViewById(R.id.tvUserName)
        tvPhone = findViewById(R.id.tvPhone)
        tvRoute = findViewById(R.id.tvRoute)
        btnSetPath = findViewById(R.id.btnSetPath)
        btnCallTaxi = findViewById(R.id.btnCallTaxi)
        btnOpenTaskUser = findViewById(R.id.btnOpenTaskUser)
        btnBackToMainUser = findViewById(R.id.btnBackToMainUser)
    }

    private fun showUserData() {
        val firstName = intent.getStringExtra(MainActivity.EXTRA_FIRST_NAME).orEmpty()
        val lastName = intent.getStringExtra(MainActivity.EXTRA_LAST_NAME).orEmpty()
        val phone = intent.getStringExtra(MainActivity.EXTRA_PHONE).orEmpty()

        tvUserName.text = "Имя и фамилия: $firstName $lastName"
        tvPhone.text = "Телефон: $phone"
    }

    private fun setListeners() {
        btnSetPath.setOnClickListener {
            val intent = Intent(this, PathActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_PATH)
        }

        btnCallTaxi.setOnClickListener {
            Toast.makeText(this, getString(R.string.taxi_sent), Toast.LENGTH_LONG).show()
        }

        btnOpenTaskUser.setOnClickListener {
            val intent = Intent(this, TaskActivity::class.java)
            startActivity(intent)
        }

        btnBackToMainUser.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }
    }

    @Deprecated("Устаревший метод используется по условию задания")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PATH && resultCode == RESULT_OK && data != null) {
            val routeFrom = data.getStringExtra(EXTRA_ROUTE_FROM).orEmpty()
            val routeTo = data.getStringExtra(EXTRA_ROUTE_TO).orEmpty()
            val city = data.getStringExtra(EXTRA_CITY).orEmpty()
            val startStreet = data.getStringExtra(EXTRA_START_STREET).orEmpty()
            val endStreet = data.getStringExtra(EXTRA_END_STREET).orEmpty()
            val comment = data.getStringExtra(EXTRA_COMMENT).orEmpty()

            val routeText = """
                Маршрут движения:
                Пункт отправления: $routeFrom
                Пункт назначения: $routeTo
                Город: $city
                Улица отправления: $startStreet
                Улица назначения: $endStreet
                Комментарий водителю: $comment

                Маршрут задан. Теперь можно вызвать такси.
            """.trimIndent()

            tvRoute.text = routeText
            btnCallTaxi.isEnabled = true
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}