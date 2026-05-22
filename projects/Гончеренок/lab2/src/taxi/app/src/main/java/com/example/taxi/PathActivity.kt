package com.example.taxi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PathActivity : AppCompatActivity() {

    private lateinit var etRouteFrom: EditText
    private lateinit var etRouteTo: EditText
    private lateinit var etCity: EditText
    private lateinit var etStartStreet: EditText
    private lateinit var etEndStreet: EditText
    private lateinit var etComment: EditText
    private lateinit var btnOk: Button
    private lateinit var btnOpenTaskPath: Button
    private lateinit var btnBackToMainPath: Button

    companion object {
        const val TAG = "PathActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_path)

        initViews()
        setListeners()
    }

    private fun initViews() {
        etRouteFrom = findViewById(R.id.etRouteFrom)
        etRouteTo = findViewById(R.id.etRouteTo)
        etCity = findViewById(R.id.etCity)
        etStartStreet = findViewById(R.id.etStartStreet)
        etEndStreet = findViewById(R.id.etEndStreet)
        etComment = findViewById(R.id.etComment)
        btnOk = findViewById(R.id.btnOk)
        btnOpenTaskPath = findViewById(R.id.btnOpenTaskPath)
        btnBackToMainPath = findViewById(R.id.btnBackToMainPath)
    }

    private fun setListeners() {
        btnOk.setOnClickListener {
            returnRouteResult()
        }

        btnOpenTaskPath.setOnClickListener {
            val intent = Intent(this, TaskActivity::class.java)
            startActivity(intent)
        }

        btnBackToMainPath.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }
    }

    private fun returnRouteResult() {
        val routeFrom = etRouteFrom.text.toString().trim()
        val routeTo = etRouteTo.text.toString().trim()
        val city = etCity.text.toString().trim()
        val startStreet = etStartStreet.text.toString().trim()
        val endStreet = etEndStreet.text.toString().trim()
        val comment = etComment.text.toString().trim()

        if (routeFrom.isEmpty() || routeTo.isEmpty() || city.isEmpty() ||
            startStreet.isEmpty() || endStreet.isEmpty() || comment.isEmpty()
        ) {
            Toast.makeText(this, getString(R.string.empty_route_fields), Toast.LENGTH_SHORT).show()
            return
        }

        val resultIntent = Intent().apply {
            putExtra(UserActivity.EXTRA_ROUTE_FROM, routeFrom)
            putExtra(UserActivity.EXTRA_ROUTE_TO, routeTo)
            putExtra(UserActivity.EXTRA_CITY, city)
            putExtra(UserActivity.EXTRA_START_STREET, startStreet)
            putExtra(UserActivity.EXTRA_END_STREET, endStreet)
            putExtra(UserActivity.EXTRA_COMMENT, comment)
        }

        setResult(RESULT_OK, resultIntent)
        finish()
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