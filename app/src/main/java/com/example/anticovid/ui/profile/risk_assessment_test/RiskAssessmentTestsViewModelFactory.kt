package com.example.anticovid.ui.profile.risk_assessment_test

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RiskAssessmentTestsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RiskAssessmentTestsViewModel::class.java)) {
            return RiskAssessmentTestsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}