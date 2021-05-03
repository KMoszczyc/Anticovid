package com.example.anticovid.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anticovid.data.repository.ApiRepository
import com.example.anticovid.data.model.CountryLiveDataModel
import com.example.anticovid.data.repository.SettingsRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeViewModel(context: Context?) : ViewModel() {

    private val apiRepository = ApiRepository()

    var currentCountry: String
        private set
    var currentCountryCode: String
        private set

    private var _countryDataModel = MutableLiveData<CountryLiveDataModel>()
    val countryDataModel: LiveData<CountryLiveDataModel> = _countryDataModel

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        SettingsRepository.loadSettingsData(context).let {
            currentCountry = it.defaultCountry
            currentCountryCode = it.defaultCountryCode
        }
    }

    fun onCountrySelected(country: String, countryCode: String) {
        _isLoading.value = true
        currentCountry = country
        currentCountryCode = countryCode

        GlobalScope.launch {
            _countryDataModel.postValue(apiRepository.fetchCovidData(currentCountryCode))
            _isLoading.postValue(false)
        }
    }
}