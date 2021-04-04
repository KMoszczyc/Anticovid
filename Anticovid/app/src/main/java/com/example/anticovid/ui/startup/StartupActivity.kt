package com.example.anticovid.ui.startup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.anticovid.R
import com.example.anticovid.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class StartupActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)
        supportActionBar?.hide()

        // Handler used to take action after displaying UI
        Handler(Looper.getMainLooper()).postDelayed({
            // check if user is signed in and open appropriate activity
            if (mAuth.currentUser != null) {
                // Open MainActivity
                // TODO
                Toast.makeText(this, "User signed in", Toast.LENGTH_LONG).show()
            }
            else {
                // Open LoginActivity
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }
        },2000)
    }
}