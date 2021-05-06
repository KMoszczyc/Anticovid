package com.example.anticovid.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anticovid.data.model.*
import com.example.anticovid.data.repository.ApiRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeViewModel(context: Context?) : ViewModel() {

    private val apiRepository = ApiRepository()

    lateinit var currentCountry: String
        private set
    lateinit var currentCountryCode: String
        private set

    private var _countryDataModel = MutableLiveData<CountryLiveDataModel>()
    val countryDataModel: LiveData<CountryLiveDataModel> = _countryDataModel

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        context!!.getSharedPreferences(SHARED_PREFERENCES_SETTINGS,Context.MODE_PRIVATE)?.let {
            currentCountry = it.getString(SHARED_PREFERENCES_SETTINGS_DEFAULT_COUNTRY, DEFAULT_COUNTRY) ?: DEFAULT_COUNTRY
            currentCountryCode = it.getString(SHARED_PREFERENCES_SETTINGS_DEFAULT_COUNTRY_CODE, DEFAULT_COUNTRY_CODE) ?: DEFAULT_COUNTRY_CODE
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