package com.example.anticovid.ui.login

import com.example.anticovid.data.model.LoggedInUser
import com.example.anticovid.data.Result

interface LoginCallbackListener {
    fun onLoginResult(result: Result<LoggedInUser>)
}