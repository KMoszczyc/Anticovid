package com.example.anticovid.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.anticovid.data.model.SettingsData
import com.example.anticovid.data.repository.SettingsRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsViewModel(context: Context?): ViewModel() {

    private var _settingsData = SettingsRepository.loadSettingsData(context)
    val settingsData = _settingsData.copy()

    fun onDefaultCountrySelected(country: String, countryCode: String) {
        settingsData.apply {
            defaultCountry = country
            defaultCountryCode = countryCode
        }
    }

    fun onFragmentStop(context: Context?) {
        if (settingsData != _settingsData) {
            _settingsData = settingsData
            SettingsRepository.saveSettingsData(_settingsData, context)
        }
    }
}