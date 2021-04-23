package com.example.anticovid.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.anticovid.R
import com.example.anticovid.ui.login.LoginActivity
import com.example.anticovid.utils.readCountries
import com.example.anticovid.utils.readTextFile
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainFragment : Fragment() {
    val apiManager = ApiManager()
    var currentCountry = "Poland"
    var currentCountryCode = "PL"

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCountriesSpinner()

        val gridAdapter = GridAdapter(context!!, null)
        covid_gridView.adapter = gridAdapter
        setupCovidDataGridView()


        sign_out.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
        }
    }

    fun setupCovidDataGridView() =  GlobalScope.launch {
        val currentCountryDataModel = apiManager.fetchCovidData(currentCountryCode)
        println(currentCountryDataModel)

        activity!!.runOnUiThread {
            val gridAdapter = GridAdapter(context!!, currentCountryDataModel)
            covid_gridView.adapter = gridAdapter
        }
    }

    fun setupCountriesSpinner(){
        val (countries, country_codes) = readCountries(context!!)
        val adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, countries)

        println("countries $countries")
        countries_spinner.adapter = adapter

        val index = countries.indexOf(currentCountry)
        countries_spinner.setSelection(index);

        countries_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                currentCountry = countries[position]
                currentCountryCode = country_codes[position]

                setupCovidDataGridView()
            }
            override fun onNothingSelected(parent: AdapterView<*>) { }
        }
    }

}