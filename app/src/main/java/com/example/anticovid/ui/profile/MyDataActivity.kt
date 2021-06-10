package com.example.anticovid.ui.profile

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.anticovid.R
import com.example.anticovid.data.model.*
import com.example.anticovid.ui.login.afterTextChanged
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_my_data.*
import kotlinx.android.synthetic.main.app_title_bar_with_back_bt.*

class MyDataActivity : AppCompatActivity() {

    private lateinit var myData: MyData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_data)
        this.supportActionBar?.hide()

        myData = loadMyData()

        initUI()
        updateUI()
    }

    private fun initUI() {
        back_bt.setOnClickListener {
            finish()
        }

        username_et.afterTextChanged {
            myData.username = it
        }

        is_suffering_from_ailment.setOnCheckedChangeListener { _, checkedId ->
            myData.isSufferingFromAilment = when (checkedId) {
                is_suffering_from_ailment_yes.id -> true
                is_suffering_from_ailment_no.id -> false
                else -> return@setOnCheckedChangeListener
            }
        }

        blood_type_spinner.apply {
            adapter = ArrayAdapter.createFromResource(
                this@MyDataActivity,
                R.array.blood_types_array,
                R.layout.item_blood_type
            ).apply {
                setDropDownViewResource(R.layout.item_blood_type_dropdown)
            }

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    myData.bloodType = parent.getItemAtPosition(position).toString()
                }
                override fun onNothingSelected(parent: AdapterView<*>) { }
            }
        }

        is_smoking.setOnCheckedChangeListener { _, checkedId ->
            myData.isSmoking = when (checkedId) {
                is_smoking_yes.id -> true
                is_smoking_no.id -> false
                else -> return@setOnCheckedChangeListener
            }
        }

        save_bt.setOnClickListener {
            saveMyData(Gson().toJson(myData))
            finish()
        }
    }

    private fun updateUI() {
        username_et.setText(myData.username)

        is_suffering_from_ailment.check(
            if (myData.isSufferingFromAilment)
                R.id.is_suffering_from_ailment_yes
            else
                R.id.is_suffering_from_ailment_no
        )

        blood_type_spinner.setSelection(
            resources.getStringArray(R.array.blood_types_array).indexOfFirst { it == myData.bloodType }
        )

        is_smoking.check(
            if (myData.isSmoking)
                R.id.is_smoking_yes
            else
                R.id.is_smoking_no)
    }

    private fun loadMyData(): MyData {
        val myDataString = getSharedPreferences(SHARED_PREFERENCES_MY_DATA, Context.MODE_PRIVATE)
            .getString(SHARED_PREFERENCES_MY_DATA, "")
        val myDataTypeToken = object : TypeToken<MyData>() {}.type

        return Gson().fromJson(myDataString, myDataTypeToken) ?: MyData()
    }

    private fun saveMyData(myDataString: String) {
        with (getSharedPreferences(SHARED_PREFERENCES_MY_DATA, Context.MODE_PRIVATE).edit()) {
            putString(SHARED_PREFERENCES_MY_DATA, myDataString)
            apply()
        }
    }
}