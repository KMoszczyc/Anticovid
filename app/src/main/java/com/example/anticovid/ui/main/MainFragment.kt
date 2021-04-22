package com.example.anticovid.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.anticovid.R
import com.example.anticovid.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {
    val apiManager = ApiManager()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var gson = Gson()

        sign_out.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
        }

        fetch_covid_button.setOnClickListener {
            val currentCountryDataModel = apiManager.fetchCovidData("poland")
            println(currentCountryDataModel)
        }
    }

}