package com.example.anticovid.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anticovid.data.repository.LoginRepository

class MainViewModel : ViewModel() {

    private val loginRepository = LoginRepository()

    private val _mainFragmentEnum = MutableLiveData<MainFragmentEnum>()
    val mainFragmentEnum: LiveData<MainFragmentEnum> = _mainFragmentEnum

    private val _isUserSignedIn = MutableLiveData<Boolean>()
    val isUserSignedIn: LiveData<Boolean> = _isUserSignedIn

    init {
        _isUserSignedIn.value = loginRepository.isUserSignedIn()
    }

    fun onFragmentSwitch(selectedFragment: MainFragmentEnum) {
        _mainFragmentEnum.value = selectedFragment
    }

    fun signOut() {
        loginRepository.signOut()
        _isUserSignedIn.value = false
    }
}