package com.example.anticovid.ui.profile.health_diary.entry

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HealthDiaryEntryViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthDiaryEntryViewModel::class.java)) {
            return HealthDiaryEntryViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}