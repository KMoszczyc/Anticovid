package com.example.anticovid.data

import com.example.anticovid.data.model.LoggedInUser
import com.example.anticovid.ui.login.LoginCallbackListener
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    var loginCallbackListener: LoginCallbackListener? = null

    private val mAuth = FirebaseAuth.getInstance()

    init {
        mAuth.currentUser?.let {
            user = LoggedInUser(it.uid, it.displayName ?: "")
        }
    }

    fun logout() {
        user = null
        mAuth.signOut()
    }

    fun login(username: String, password: String) {
        mAuth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mAuth.currentUser?.let {
                        val loggedInUser = LoggedInUser(it.uid, it.displayName ?: "")
                        user = loggedInUser
                        loginCallbackListener?.onLoginResult(Result.Success(loggedInUser))
                    }
                }
                else
                {
                    task.exception?.let {
                        loginCallbackListener?.onLoginResult(Result.Error(IOException(it.message, it)))
                    }
                }
            }
    }
}