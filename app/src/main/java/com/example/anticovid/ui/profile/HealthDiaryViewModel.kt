package com.example.anticovid.ui.profile

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anticovid.data.model.HealthDiaryEntry
import com.example.anticovid.data.model.SHARED_PREFERENCES_HEALTH_DIARY_ENTRIES
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class HealthDiaryViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    private val _isTodaysEntryCompleted = MutableLiveData<Boolean>()
    val isTodaysEntryCompleted: LiveData<Boolean> = _isTodaysEntryCompleted

    private val _healthDiaryEntries = MutableLiveData<List<HealthDiaryEntry>>()
    val healthDiaryEntries: LiveData<List<HealthDiaryEntry>> = _healthDiaryEntries

    init {
        val entries = loadEntries()
        val sdf = SimpleDateFormat("ddMMyyyy")
        val today = sdf.format(Calendar.getInstance().time)

        entries.forEach {
            if (sdf.format(it.date.time) == today) {
                _isTodaysEntryCompleted.value = true
                return@forEach
            }
        }

        _healthDiaryEntries.value = entries
    }

    fun newEntryAdded(newEntry: HealthDiaryEntry) {
        _healthDiaryEntries.value = (_healthDiaryEntries.value?.toMutableList() ?: mutableListOf()).apply {
            add(newEntry)
            sortByDescending { it.date }
        }

        with (SimpleDateFormat("ddMMyyyy")) {
            if (format(newEntry.date.time) == format(Calendar.getInstance().time))
                _isTodaysEntryCompleted.value = true
        }

        saveEntries(Gson().toJson(_healthDiaryEntries.value))
    }

    private fun loadEntries(): List<HealthDiaryEntry> {
        val entriesString = sharedPreferences.getString(SHARED_PREFERENCES_HEALTH_DIARY_ENTRIES, "")
        val listType = object : TypeToken<List<HealthDiaryEntry>>() {}.type

        return Gson().fromJson(entriesString, listType) ?: emptyList()
    }

    private fun saveEntries(entriesString: String) {
        with (sharedPreferences.edit()) {
            putString(SHARED_PREFERENCES_HEALTH_DIARY_ENTRIES, entriesString)
            apply()
        }
    }
}