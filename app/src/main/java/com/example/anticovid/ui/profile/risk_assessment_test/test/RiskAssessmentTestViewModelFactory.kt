package com.example.anticovid.ui.profile.risk_assessment_test.test

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RiskAssessmentTestViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RiskAssessmentTestViewModel::class.java)) {
            return RiskAssessmentTestViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}