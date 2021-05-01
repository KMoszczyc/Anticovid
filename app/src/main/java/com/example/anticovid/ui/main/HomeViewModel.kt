package com.example.anticovid.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anticovid.data.repository.ApiRepository
import com.example.anticovid.data.model.CountryLiveDataModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val apiRepository = ApiRepository()

    var currentCountry = "Poland"
        private set
    var currentCountryCode = "PL"
        private set

    private var _countryDataModel = MutableLiveData<CountryLiveDataModel>()
    val countryDataModel: LiveData<CountryLiveDataModel> = _countryDataModel

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

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