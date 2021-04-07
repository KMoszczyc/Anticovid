package com.example.anticovid.data

import com.example.anticovid.data.model.LoggedInUser
import com.example.anticovid.data.model.Result
import com.google.firebase.auth.FirebaseAuth

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository {

    var loginCallbackListener: LoginCallbackListener? = null

    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    private val mAuth = FirebaseAuth.getInstance()

    init {
        mAuth.currentUser?.let {
            user = LoggedInUser(it.uid, it.displayName ?: "")
        }
    }

    fun signOut() {
        user = null
        mAuth.signOut()
    }

    fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    onSuccessfulLogin()
                else
                    onLoginError(task.exception?.message ?: "Error")
            }
    }

    fun signUp(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    onSuccessfulLogin()
                else
                    onLoginError(task.exception?.message ?: "Error")
            }
    }

    private fun onSuccessfulLogin() {
        mAuth.currentUser?.let {
            LoggedInUser(it.uid, it.displayName ?: "").apply {
                user = this
                loginCallbackListener?.onLoginResult(Result.Success(this))
            }
        }
    }

    private fun onLoginError(message: String) {
        loginCallbackListener?.onLoginResult(Result.Error(message))
    }

    interface LoginCallbackListener {
        fun onLoginResult(result: Result<LoggedInUser>)
    }
}