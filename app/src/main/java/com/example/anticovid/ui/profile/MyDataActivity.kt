package com.example.anticovid.ui.profile

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.anticovid.R
import com.example.anticovid.data.model.SHARED_PREFERENCES_MY_DATA
import com.example.anticovid.data.model.SHARED_PREFERENCES_MY_DATA_USERNAME
import com.example.anticovid.ui.login.afterTextChanged
import kotlinx.android.synthetic.main.activity_my_data.*
import kotlinx.android.synthetic.main.app_title_bar_with_back_bt.*

class MyDataActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_data)
        this.supportActionBar?.hide()

        back_bt.setOnClickListener {
            finish()
        }

        sharedPref = getSharedPreferences(SHARED_PREFERENCES_MY_DATA, Context.MODE_PRIVATE)

        sharedPref.getString(SHARED_PREFERENCES_MY_DATA_USERNAME, "")?.let {
            username = it
            username_et.setText(it)
        }

        username_et.afterTextChanged {
            username = it.trim()
        }
    }

    override fun onStop() {
        super.onStop()

        with (sharedPref.edit()) {
            putString(SHARED_PREFERENCES_MY_DATA_USERNAME, username)
            apply()
        }
    }
}