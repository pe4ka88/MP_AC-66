package com.example.taxi

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etPhone: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var btnRegistration: Button
    private lateinit var btnOpenTaskMain: Button
    private lateinit var preferences: SharedPreferences

    companion object {
        const val TAG = "MainActivity"

        const val PREFS_NAME = "taxi_preferences"
        const val KEY_PHONE = "key_phone"
        const val KEY_FIRST_NAME = "key_first_name"
        const val KEY_LAST_NAME = "key_last_name"

        const val EXTRA_PHONE = "extra_phone"
        const val EXTRA_FIRST_NAME = "extra_first_name"
        const val EXTRA_LAST_NAME = "extra_last_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_main)

        initViews()
        initPreferences()
        restoreUserData()
        setListeners()
    }

    private fun initViews() {
        etPhone = findViewById(R.id.etPhone)
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        btnRegistration = findViewById(R.id.btnRegistration)
        btnOpenTaskMain = findViewById(R.id.btnOpenTaskMain)
    }

    private fun initPreferences() {
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    private fun setListeners() {
        btnRegistration.setOnClickListener {
            openUserScreen()
        }

        btnOpenTaskMain.setOnClickListener {
            val intent = Intent(this, TaskActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openUserScreen() {
        val phone = etPhone.text.toString().trim()
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()

        if (phone.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_fields), Toast.LENGTH_SHORT).show()
            return
        }

        saveUserData(phone, firstName, lastName)
        btnRegistration.text = getString(R.string.login)

        val intent = Intent(this, UserActivity::class.java).apply {
            putExtra(EXTRA_PHONE, phone)
            putExtra(EXTRA_FIRST_NAME, firstName)
            putExtra(EXTRA_LAST_NAME, lastName)
        }

        startActivity(intent)
    }

    private fun saveUserData(phone: String, firstName: String, lastName: String) {
        preferences.edit()
            .putString(KEY_PHONE, phone)
            .putString(KEY_FIRST_NAME, firstName)
            .putString(KEY_LAST_NAME, lastName)
            .apply()
    }

    private fun restoreUserData() {
        val phone = preferences.getString(KEY_PHONE, "") ?: ""
        val firstName = preferences.getString(KEY_FIRST_NAME, "") ?: ""
        val lastName = preferences.getString(KEY_LAST_NAME, "") ?: ""

        if (phone.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()) {
            etPhone.setText(phone)
            etFirstName.setText(firstName)
            etLastName.setText(lastName)
            btnRegistration.text = getString(R.string.login)
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