package com.example.anticovid.ui.profile

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.anticovid.ui.login.LoginViewModel

class HealthDiaryViewModelFactory(private val sharedPreferences: SharedPreferences) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthDiaryViewModel::class.java)) {
            return HealthDiaryViewModel(sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}