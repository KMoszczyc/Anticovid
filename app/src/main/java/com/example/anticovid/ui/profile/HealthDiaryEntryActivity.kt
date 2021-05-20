package com.example.anticovid.ui.profile

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.anticovid.R
import com.example.anticovid.data.model.HealthDiaryEntry
import com.example.anticovid.data.model.NEW_HEALTH_DIARY_ENTRY_KEY
import com.example.anticovid.data.model.formatDate
import com.example.anticovid.data.model.formatDateTime
import kotlinx.android.synthetic.main.activity_health_diary_entry.*
import kotlinx.android.synthetic.main.app_title_bar_with_back_bt.*
import java.util.*

class HealthDiaryEntryActivity : AppCompatActivity() {

    private val newEntry = HealthDiaryEntry()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_diary_entry)
        this.supportActionBar?.hide()

        back_bt.setOnClickListener {
            finish()
        }

        updateDate()

        entry_date.setOnClickListener {
            DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth -> onEntryDateSelected(year, monthOfYear, dayOfMonth) },
                newEntry.date[Calendar.YEAR], newEntry.date[Calendar.MONTH], newEntry.date[Calendar.DAY_OF_MONTH]
            ).show()
        }

        save_bt.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().apply { putExtra(NEW_HEALTH_DIARY_ENTRY_KEY, newEntry) })
            finish()
        }
    }

    private fun onEntryDateSelected(year: Int, month: Int, day: Int) {
        newEntry.apply {
            date.set(year, month, day)
            header = formatDate(date.time)
        }

        updateDate()
    }

    private fun updateDate() {
        entry_date.text = formatDate(newEntry.date.time)
    }
}