package com.example.anticovid.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.anticovid.R
import com.example.anticovid.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        attachFragment(MainFragment(), "MainFragment")
    }

    private fun attachFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment, tag).commit()
    }
}