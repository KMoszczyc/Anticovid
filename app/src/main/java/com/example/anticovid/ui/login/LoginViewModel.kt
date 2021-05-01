package com.example.anticovid.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anticovid.R
import com.example.anticovid.data.repository.LoginRepository
import com.example.anticovid.data.model.LoggedInUser
import com.example.anticovid.data.model.Result

class LoginViewModel : ViewModel(), LoginRepository.LoginCallbackListener {

    private val loginRepository = LoginRepository(this)

    private val _loginFormEnum = MutableLiveData<LoginFormEnum>()
    val loginFormEnum: LiveData<LoginFormEnum> = _loginFormEnum

    private val _loginFormState = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginFormState

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isKeyboardVisible = MutableLiveData<Boolean>()
    val isKeyboardVisible: LiveData<Boolean> = _isKeyboardVisible

    init {
        // Set SignInFragment as default
        _loginFormEnum.value = LoginFormEnum.SignIn
        _isKeyboardVisible.value = false
    }

    fun signIn(email: String, password: String) {
        _isLoading.value = true
        loginRepository.signIn(email, password)
    }

    fun signUp(email: String, password: String) {
        _isLoading.value = true
        loginRepository.signUp(email, password)
    }

    override fun onLoginResult(result: Result) {
        _isLoading.value = false

        when {
            (result is Result.Success<*>) ->
                _loginResult.value = LoginResult(success = LoggedInUserView(displayName = (result.data as? LoggedInUser)?.displayName ?: "user"))
            (result is Result.Error) ->
                _loginResult.value = LoginResult(error = result.errorMessage)
        }
    }

    fun loginDataChanged(email: String, password: String, repeatedPassword: String? = null) {
        if (!isEmailValid(email))
            _loginFormState.value = LoginFormState(emailError = R.string.invalid_email)
        else if (!isPasswordValid(password))
            _loginFormState.value = LoginFormState(passwordError = R.string.invalid_password)
        else if (!isRepeatedPasswordValid(password, repeatedPassword))
            _loginFormState.value = LoginFormState(repeatedPasswordError = R.string.invalid_repeated_password)
        else
            _loginFormState.value = LoginFormState(isDataValid = true)
    }

    fun loginFormChanged(loginFormEnum: LoginFormEnum) {
        resetLoginStatus()
        _loginFormEnum.value = loginFormEnum
    }

    private fun resetLoginStatus() {
        _loginFormState.value = null
        _loginResult.value = null
    }

    fun onKeyboardVisibilityChanged(isKeyboardVisible: Boolean) {
        _isKeyboardVisible.value = isKeyboardVisible
    }

    // A placeholder username validation check
    private fun isEmailValid(email: String): Boolean {
        return if (email.contains('@'))
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        else
            email.isNotBlank()
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    // A placeholder repeated password validation check
    private fun isRepeatedPasswordValid(password: String, repeatedPassword: String?): Boolean {
        return repeatedPassword == null || repeatedPassword == password
    }
}