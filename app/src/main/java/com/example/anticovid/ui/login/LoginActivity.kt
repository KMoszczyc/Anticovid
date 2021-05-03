package com.example.anticovid.ui.login

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.anticovid.ui.main.MainActivity
import com.example.anticovid.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory()).get(LoginViewModel::class.java)

        loginViewModel.loginFormEnum.observe(this, Observer { loginFormEnum ->
            if (loginFormEnum == null)
                return@Observer

            // attach fragment
            when (loginFormEnum) {
                LoginFormEnum.SignIn ->
                    attachFragment(SignInFragment())
                LoginFormEnum.SignUp ->
                    attachFragment(SignUpFragment())
            }

            Log.d("MyDebug", "fragments: ${supportFragmentManager.fragments.size}")
        })

        loginViewModel.loginResult.observe(this, Observer { loginResult ->
            if (loginResult == null)
                return@Observer

            // take action on successful login
            if (loginResult.success != null) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        })

        loginViewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading == null)
                return@Observer

            // update UI
            if (isLoading) {
                clearFocus()
                hideSoftKeyboard()
                disableTouchEvents()
                loading.visibility = View.VISIBLE
            }
            else {
                loading.visibility = View.GONE
                enableTouchEvents()
            }
        })

        // handle keyboard events
        container.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            container.getWindowVisibleDisplayFrame(r)
            val screenHeight = container.rootView.height

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            val keypadHeight = screenHeight - r.bottom

            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                // keyboard is opened
                if (loginViewModel.isKeyboardVisible.value == false)
                    loginViewModel.onKeyboardVisibilityChanged(true)
            }
            else {
                // keyboard is closed
                if (loginViewModel.isKeyboardVisible.value == true)
                    loginViewModel.onKeyboardVisibilityChanged(false)
            }
        }
    }

    private fun attachFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    private fun clearFocus() {
        container.requestFocus()
    }

    private fun hideSoftKeyboard() {
        currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun enableTouchEvents() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun disableTouchEvents() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}