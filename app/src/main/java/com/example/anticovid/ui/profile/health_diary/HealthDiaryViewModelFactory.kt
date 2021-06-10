package com.example.anticovid.ui.profile.health_diary

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HealthDiaryViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthDiaryViewModel::class.java)) {
            return HealthDiaryViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}