package com.example.anticovid.ui.profile.health_diary

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anticovid.data.model.HealthDiaryEntry
import com.example.anticovid.data.model.SHARED_PREFERENCES_HEALTH_DIARY
import com.example.anticovid.data.model.SHARED_PREFERENCES_HEALTH_DIARY_ENTRIES
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class HealthDiaryViewModel(private val context: Context) : ViewModel() {

    private val _isTodaysEntryCompleted = MutableLiveData<Boolean>()
    val isTodaysEntryCompleted: LiveData<Boolean> = _isTodaysEntryCompleted

    private val _healthDiaryEntries = MutableLiveData<List<HealthDiaryEntry>>()
    val healthDiaryEntries: LiveData<List<HealthDiaryEntry>> = _healthDiaryEntries

    init {
        loadEntries().also {
            _healthDiaryEntries.value = it
            if (it.isNotEmpty())
                checkIfEntryFromToday(it.first())
        }
    }

    private fun checkIfEntryFromToday(entry: HealthDiaryEntry) {
        with (SimpleDateFormat("ddMMyyyy")) {
            val today = format(Calendar.getInstance().time)
            _isTodaysEntryCompleted.value = format(entry.date.time) == today
        }
    }

    fun saveEntry(entry: HealthDiaryEntry) {
        _healthDiaryEntries.value = (_healthDiaryEntries.value?.toMutableList() ?: mutableListOf()).apply {
            removeIf { it.date.timeInMillis == entry.date.timeInMillis }
            add(entry)
            sortByDescending { it.date }
        }

        checkIfEntryFromToday(entry)
        saveEntries(Gson().toJson(_healthDiaryEntries.value))
    }

    private fun loadEntries(): List<HealthDiaryEntry> {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_HEALTH_DIARY, Context.MODE_PRIVATE)
        val entriesString = sharedPreferences.getString(SHARED_PREFERENCES_HEALTH_DIARY_ENTRIES, "")
        val listType = object : TypeToken<List<HealthDiaryEntry>>() {}.type

        return Gson().fromJson(entriesString, listType) ?: emptyList()
    }

    private fun saveEntries(entriesString: String) {
        with (context.getSharedPreferences(SHARED_PREFERENCES_HEALTH_DIARY, Context.MODE_PRIVATE).edit()) {
            putString(SHARED_PREFERENCES_HEALTH_DIARY_ENTRIES, entriesString)
            apply()
        }
    }
}