package com.example.anticovid.ui.profile.health_diary.entry

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anticovid.R
import com.example.anticovid.data.model.*
import java.text.SimpleDateFormat
import java.util.*

class HealthDiaryEntryViewModel(private val context: Context) : ViewModel() {

    private val _entry = MutableLiveData<HealthDiaryEntry>()
    val entry: LiveData<HealthDiaryEntry> = _entry

    private val _dateModEnabled = MutableLiveData<Boolean>()
    val dateModEnabled: LiveData<Boolean> = _dateModEnabled

    private val _tempError = MutableLiveData<String>()
    val tempError: LiveData<String> = _tempError

    private val _saveEnabled = MutableLiveData<Boolean>()
    val saveEnabled: LiveData<Boolean> = _saveEnabled

    init {
        val entryCheck = (context as Activity).intent.getSerializableExtra(HEALTH_DIARY_ENTRY_KEY) as? HealthDiaryEntry
        _dateModEnabled.value = entryCheck == null
        _saveEnabled.value = entryCheck != null
        _entry.value = entryCheck ?: HealthDiaryEntry()
    }

    fun parmEntryDate(year: Int, month: Int, day: Int) {
        _entry.value?.let {
            it.date.set(year, month, day)

            with (SimpleDateFormat("ddMMyyyy")) {
                val today = format(Calendar.getInstance().time)
                it.header = if (format(it.date.time) == today)
                    formatDateTime(it.date.time)
                else
                    formatDate(it.date.time)
            }
        }
    }

    fun parmEntryTemp(tempString: String) {
        _saveEnabled.value = false
        if (tempString.isNotBlank()) {
            val temp = tempString.toDouble()
            when {
                temp < 35 -> _tempError.value = context.getString(R.string.value_to_low)
                temp > 42 -> _tempError.value = context.getString(R.string.value_to_high)
                else -> {
                    _entry.value?.temperature = temp
                    _tempError.value = null
                    _saveEnabled.value = true
                }
            }
        }
    }

    fun parmRunnyNoseState(state: SymptomState) {
        _entry.value?.runnyNose = state
    }

    fun parmCoughState(state: SymptomState) {
        _entry.value?.cough = state
    }

    fun parmShiveringState(state: SymptomState) {
        _entry.value?.shivering = state
    }

    fun parmAchingMusclesState(state: SymptomState) {
        _entry.value?.achingMuscles = state
    }

    fun parmPlacesAndContacts(text: String) {
        _entry.value?.placesAndContacts = text
    }
}