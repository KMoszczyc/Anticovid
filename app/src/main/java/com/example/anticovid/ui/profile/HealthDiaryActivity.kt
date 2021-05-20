package com.example.anticovid.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anticovid.R
import kotlinx.android.synthetic.main.activity_health_diary.*
import kotlinx.android.synthetic.main.app_title_bar_with_back_bt.*
import java.util.*
import com.example.anticovid.data.model.*

class HealthDiaryActivity : AppCompatActivity() {

    private lateinit var healthDiaryViewModel: HealthDiaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_diary)
        this.supportActionBar?.hide()

        healthDiaryViewModel = ViewModelProvider(this,
            HealthDiaryViewModelFactory(getSharedPreferences(SHARED_PREFERENCES_HEALTH_DIARY, Context.MODE_PRIVATE)))
            .get(HealthDiaryViewModel::class.java).apply {
                isTodaysEntryCompleted.observe(this@HealthDiaryActivity) {
                    updateTodaysEntryCard(it ?: false)
                }

                healthDiaryEntries.observe(this@HealthDiaryActivity) {
                    val healthDiaryEntries = it ?: emptyList()

                    if (healthDiaryEntries.isEmpty())
                            previous_entries_header.text = getString(R.string.no_previous_entries)
                    else
                        (health_diary_entries_recycler_view.adapter as HealthDiaryEntriesAdapter).updateEntriesList(healthDiaryEntries)
                }
            }

        initUI()
    }

    private fun initUI() {
        back_bt.setOnClickListener {
            finish()
        }

        entry_date.text = formatDate(Calendar.getInstance().time)

        add_new_entry.setOnClickListener {
            val intent = Intent(this, HealthDiaryEntryActivity::class.java)
            startActivityForResult(intent, NEW_HEALTH_DIARY_ENTRY)
        }

        health_diary_entries_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@HealthDiaryActivity)
            adapter = HealthDiaryEntriesAdapter()
        }
    }

    private fun updateTodaysEntryCard(isTodaysEntryCompleted: Boolean) {
        if (isTodaysEntryCompleted) {
            entry_date.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_circle_24, 0)
            entry_info.text = getString(R.string.health_diary_entry_completed)
        }
        else {
            entry_date.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_add_circle_24, 0)
            entry_info.text = getString(R.string.complete_health_diary_entry)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            NEW_HEALTH_DIARY_ENTRY -> {
                if (resultCode == Activity.RESULT_OK) {
                    (data!!.getSerializableExtra(NEW_HEALTH_DIARY_ENTRY_KEY) as? HealthDiaryEntry)?.let {
                        healthDiaryViewModel.newEntryAdded(it)
                    }
                }
            }
        }
    }
}