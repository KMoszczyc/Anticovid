package com.example.anticovid.ui.profile.health_diary.entry

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.anticovid.R
import com.example.anticovid.data.model.*
import com.example.anticovid.ui.login.afterTextChanged
import kotlinx.android.synthetic.main.activity_health_diary_entry.*
import kotlinx.android.synthetic.main.app_title_bar_with_back_bt.*
import java.util.*

class HealthDiaryEntryActivity : AppCompatActivity() {

    private lateinit var healthDiaryEntryViewModel: HealthDiaryEntryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_diary_entry)
        this.supportActionBar?.hide()

        healthDiaryEntryViewModel = ViewModelProvider(this, HealthDiaryEntryViewModelFactory(this))
            .get(HealthDiaryEntryViewModel::class.java).apply {
                entry.observe(this@HealthDiaryEntryActivity, { entry ->
                    entry?.let { updateUI(it) }
                })

                dateModEnabled.observe(this@HealthDiaryEntryActivity, { dateModEnabled ->
                    if (dateModEnabled == true) {
                        entry_date.apply {
                            isEnabled = false
                            setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                        }
                    }
                })

                saveEnabled.observe(this@HealthDiaryEntryActivity, { saveEnabled ->
                    save_bt.isEnabled = saveEnabled
                })

                tempError.observe(this@HealthDiaryEntryActivity, { tempError ->
                    entry_temp.error = tempError
                })
            }

        initUI()
    }

    private fun initUI() {
        back_bt.setOnClickListener {
            finish()
        }

        entry_date.setOnClickListener {
            healthDiaryEntryViewModel.entry.value?.let { entry ->
                DatePickerDialog(this@HealthDiaryEntryActivity, { _, year, monthOfYear, dayOfMonth -> onEntryDateSelected(year, monthOfYear, dayOfMonth) },
                    entry.date[Calendar.YEAR], entry.date[Calendar.MONTH], entry.date[Calendar.DAY_OF_MONTH]
                ).apply {
                    datePicker.maxDate = System.currentTimeMillis()
                    show()
                }
            }
        }

        entry_temp.afterTextChanged {
            healthDiaryEntryViewModel.parmEntryTemp(it)
        }

        with(healthDiaryEntryViewModel) {
            runny_nose.setOnCheckedChangeListener { _, checkedId ->
                parmRunnyNoseState( when (checkedId) {
                    R.id.runny_nose_none -> SymptomState.None
                    R.id.runny_nose_slight -> SymptomState.Slight
                    R.id.runny_nose_severe -> SymptomState.Severe
                    else -> SymptomState.None
                })
            }

            cough.setOnCheckedChangeListener { _, checkedId ->
                parmCoughState( when (checkedId) {
                    R.id.cough_none -> SymptomState.None
                    R.id.cough_slight -> SymptomState.Slight
                    R.id.cough_severe -> SymptomState.Severe
                    else -> SymptomState.None
                })
            }

            shivering.setOnCheckedChangeListener { _, checkedId ->
                parmShiveringState( when (checkedId) {
                    R.id.shivering_none -> SymptomState.None
                    R.id.shivering_slight -> SymptomState.Slight
                    R.id.shivering_severe -> SymptomState.Severe
                    else -> SymptomState.None
                })
            }

            aching_muscles.setOnCheckedChangeListener { _, checkedId ->
                parmAchingMusclesState( when (checkedId) {
                    R.id.aching_muscles_none -> SymptomState.None
                    R.id.aching_muscles_slight -> SymptomState.Slight
                    R.id.aching_muscles_severe -> SymptomState.Severe
                    else -> SymptomState.None
                })
            }

            places_and_contacts_text.afterTextChanged {
                parmPlacesAndContacts(it)
            }

            save_bt.setOnClickListener {
                setResult(
                    Activity.RESULT_OK,
                    Intent().apply { putExtra(HEALTH_DIARY_ENTRY_KEY, healthDiaryEntryViewModel.entry.value) }
                )
                finish()
            }
        }
    }

    private fun onEntryDateSelected(year: Int, month: Int, day: Int) {
        healthDiaryEntryViewModel.parmEntryDate(year, month, day)
        updateDate(Date(year, month, day))
    }

    private fun updateDate(date: Date) {
        entry_date.text = formatDate(date)
    }

    private fun updateUI(entry: HealthDiaryEntry) {
        updateDate(entry.date.time)

        entry.temperature?.let {
            entry_temp.setText(it.toString())
        }

        runny_nose.check( when (entry.runnyNose) {
            SymptomState.None -> R.id.runny_nose_none
            SymptomState.Slight -> R.id.runny_nose_slight
            SymptomState.Severe -> R.id.runny_nose_severe
        })

        cough.check( when (entry.cough) {
            SymptomState.None -> R.id.cough_none
            SymptomState.Slight -> R.id.cough_slight
            SymptomState.Severe -> R.id.cough_severe
        })

        shivering.check( when (entry.shivering) {
            SymptomState.None -> R.id.shivering_none
            SymptomState.Slight -> R.id.shivering_slight
            SymptomState.Severe -> R.id.shivering_severe
        })

        aching_muscles.check( when (entry.achingMuscles) {
            SymptomState.None -> R.id.aching_muscles_none
            SymptomState.Slight -> R.id.aching_muscles_slight
            SymptomState.Severe -> R.id.aching_muscles_severe
        })

        places_and_contacts_text.setText(entry.placesAndContacts)
    }
}