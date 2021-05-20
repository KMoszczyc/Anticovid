package com.example.anticovid.ui.profile

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.anticovid.R
import com.example.anticovid.data.model.*
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

        sharedPref = getSharedPreferences(SHARED_PREFERENCES_MY_DATA, Context.MODE_PRIVATE)

        initUI()
        initData()
    }

    private fun initUI() {
        back_bt.setOnClickListener {
            finish()
        }

        username_et.afterTextChanged {
            username = it.trim()
        }

        question1_yes.setOnClickListener {
            saveAnswer(SHARED_PREFERENCES_MY_DATA_QUESTION_1,true)
        }

        question1_no.setOnClickListener {
            saveAnswer(SHARED_PREFERENCES_MY_DATA_QUESTION_1,false)
        }

        blood_type_spinner.apply {
            adapter = ArrayAdapter.createFromResource(this@MyDataActivity, R.array.blood_types_array, R.layout.item_blood_type).apply {
                setDropDownViewResource(R.layout.item_blood_type_dropdown)
            }

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    with (sharedPref.edit()) {
                        putString(SHARED_PREFERENCES_MY_DATA_QUESTION_2, parent.getItemAtPosition(position).toString())
                        apply()
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>) { }
            }
        }

        question3_yes.setOnClickListener {
            saveAnswer(SHARED_PREFERENCES_MY_DATA_QUESTION_3,true)
        }

        question3_no.setOnClickListener {
            saveAnswer(SHARED_PREFERENCES_MY_DATA_QUESTION_3,false)
        }
    }

    private fun saveAnswer(key: String, answer: Boolean) {
        with (sharedPref.edit()) {
            putBoolean(key, answer)
            apply()
        }
    }

    private fun initData() {
        with (sharedPref) {
            getString(SHARED_PREFERENCES_MY_DATA_USERNAME, "")?.let {
                username = it
                username_et.setText(it)
            }

            if (getBoolean(SHARED_PREFERENCES_MY_DATA_QUESTION_1, false))
                question1.check(R.id.question1_yes)
            else
                question1.check(R.id.question1_no)

            getString(SHARED_PREFERENCES_MY_DATA_QUESTION_2, "")?.let { blood_type ->
                resources.getStringArray(R.array.blood_types_array).forEachIndexed { index, s ->
                    if (s == blood_type) {
                        blood_type_spinner.setSelection(index)
                        return@let
                    }
                }
            }

            if (getBoolean(SHARED_PREFERENCES_MY_DATA_QUESTION_3, false))
                question3.check(R.id.question3_yes)
            else
                question3.check(R.id.question3_no)
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