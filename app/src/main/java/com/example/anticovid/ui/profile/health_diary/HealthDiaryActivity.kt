package com.example.anticovid.ui.profile.health_diary

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anticovid.R
import kotlinx.android.synthetic.main.activity_health_diary.*
import kotlinx.android.synthetic.main.app_title_bar_with_back_bt.*
import java.util.*
import com.example.anticovid.data.model.*
import com.example.anticovid.ui.profile.health_diary.entry.HealthDiaryEntryActivity

class HealthDiaryActivity : AppCompatActivity(),
    HealthDiaryEntryRecyclerAdapter.OnHealthDiaryEntryClickListener {

    private lateinit var healthDiaryViewModel: HealthDiaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_diary)
        this.supportActionBar?.hide()

        healthDiaryViewModel = ViewModelProvider(this, HealthDiaryViewModelFactory(this))
            .get(HealthDiaryViewModel::class.java).apply {
                isTodaysEntryCompleted.observe(this@HealthDiaryActivity, { isTodaysEntryCompleted ->
                    isTodaysEntryCompleted?.let { updateTodaysEntryCard(it) }
                })

                healthDiaryEntries.observe(this@HealthDiaryActivity, {
                    val healthDiaryEntries = it ?: emptyList()

                    if (healthDiaryEntries.isEmpty())
                        previous_entries_header.text = getString(R.string.no_previous_entries)
                    else
                        (health_diary_entries_recycler_view.adapter as HealthDiaryEntryRecyclerAdapter).updateEntriesList(healthDiaryEntries)
                })
            }

        initUI()
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

    private fun initUI() {
        back_bt.setOnClickListener {
            finish()
        }

        entry_date.text = formatDate(Calendar.getInstance().time)

        add_new_entry.setOnClickListener {
            startHealthDiaryEntryActivity()
        }

        health_diary_entries_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@HealthDiaryActivity)
            adapter = HealthDiaryEntryRecyclerAdapter(this@HealthDiaryActivity)
        }
    }

    override fun onHealthDiaryEntryClick(entry: HealthDiaryEntry) {
        startHealthDiaryEntryActivity(entry)
    }

    private fun startHealthDiaryEntryActivity(entry: HealthDiaryEntry? = null) {
        val intent = Intent(this, HealthDiaryEntryActivity::class.java).apply {
            putExtra(HEALTH_DIARY_ENTRY_KEY, entry)
        }
        startActivityForResult(intent, HEALTH_DIARY_ENTRY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            HEALTH_DIARY_ENTRY -> {
                if (resultCode == Activity.RESULT_OK) {
                    (data!!.getSerializableExtra(HEALTH_DIARY_ENTRY_KEY) as? HealthDiaryEntry)?.let {
                        healthDiaryViewModel.saveEntry(it)
                    }
                }
            }
        }
    }
}