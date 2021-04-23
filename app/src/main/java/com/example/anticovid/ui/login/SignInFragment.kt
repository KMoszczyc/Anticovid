package com.example.anticovid.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.anticovid.R
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignInFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? LoginActivity)?.let {
            ViewModelProvider(it).get(LoginViewModel::class.java).apply {

                loginFormState.observe(viewLifecycleOwner, Observer { loginState ->
                    if (loginState == null)
                        return@Observer

                    // disable signIn button unless username and password are valid
                    sign_in.isEnabled = loginState.isDataValid

                    // display errors
                    if (loginState.emailError != null)
                        email.error = context?.getString(loginState.emailError)
                    if (loginState.passwordError != null)
                        password.error = context?.getString(loginState.passwordError)
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
                    loginDataChanged(email.text.toString(), password.text.toString())
                }

                email.afterTextChanged {
                    loginDataChanged()
                }

                password.afterTextChanged {
                    loginDataChanged()
                }

                sign_in.setOnClickListener {
                    signIn(email.text.toString(), password.text.toString())
                }

                sign_up.setOnClickListener {
                    loginFormChanged(LoginForm.SignUp)
                }
            }
        }
    }
}