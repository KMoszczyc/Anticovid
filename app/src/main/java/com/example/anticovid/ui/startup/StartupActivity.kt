package com.example.anticovid.ui.startup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.anticovid.ui.main.MainActivity
import com.example.anticovid.R
import com.example.anticovid.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class StartupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)
        supportActionBar?.hide()

        // Handler used to take action after displaying UI
        Handler(Looper.getMainLooper()).postDelayed({
            // check if user is signed in and open proper activity
            if (FirebaseAuth.getInstance().currentUser != null)
                startActivity(Intent(applicationContext, MainActivity::class.java))
            else
                startActivity(Intent(applicationContext, LoginActivity::class.java))

            finish()
        },1000)
    }
}