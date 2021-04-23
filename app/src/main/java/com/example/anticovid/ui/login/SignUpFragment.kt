package com.example.anticovid.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.anticovid.R
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? LoginActivity)?.let {
            ViewModelProvider(it).get(LoginViewModel::class.java).apply {

                loginFormState.observe(viewLifecycleOwner, Observer { loginState ->
                    if (loginState == null)
                        return@Observer

                    // disable signUp button unless username, password and repeatedPassword are valid
                    sign_up.isEnabled = loginState.isDataValid

                    // display errors
                    if (loginState.emailError != null)
                        email.error = context?.getString(loginState.emailError)
                    if (loginState.passwordError != null)
                        password.error = context?.getString(loginState.passwordError)
                    if (loginState.repeatedPasswordError != null)
                        repeated_password.error = context?.getString(loginState.repeatedPasswordError)
                })

                loginResult.observe(viewLifecycleOwner, Observer { loginResult ->
                    if (loginResult == null)
                        return@Observer

                    // display error message if login failed
                    if (loginResult.error != null)
                        error_message.text = loginResult.error
                })

                isKeyboardVisible.observe(viewLifecycleOwner, Observer { isKeyboardVisible ->
                    if (isKeyboardVisible == null)
                        return@Observer

                    // hide/show views when soft keyboard opens/closes
                    if (isKeyboardVisible) {
                        header.visibility = View.GONE
                        footer.visibility = View.GONE
                    }
                    else {
                        header.visibility = View.VISIBLE
                        footer.visibility = View.VISIBLE
                    }
                })

                // helper method
                fun loginDataChanged() {
                    loginDataChanged(email.text.toString(), password.text.toString(), repeated_password.text.toString())
                }

                email.afterTextChanged {
                    loginDataChanged()
                }

                password.afterTextChanged {
                    loginDataChanged()
                }

                repeated_password.afterTextChanged {
                    loginDataChanged()
                }

                sign_up.setOnClickListener {
                    signUp(email.text.toString(), password.text.toString())
                }

                sign_in.setOnClickListener {
                    loginFormChanged(LoginForm.SignIn)
                }
            }
        }
    }
}