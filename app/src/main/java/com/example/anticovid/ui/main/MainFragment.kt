package com.example.anticovid.ui.main

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.example.anticovid.R
import com.example.anticovid.ui.login.LoginActivity
import com.example.anticovid.utils.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainFragment : Fragment() {
    val apiManager = ApiManager()
    var currentCountry = "Poland"
    var currentCountryCode = "PL"
    lateinit var flagsDrawables : MutableList<Drawable>
    lateinit var flagsCountryCodes : MutableList<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        var pair = loadImages(context!!)
        flagsDrawables = pair.first
        flagsCountryCodes = pair.second

        val (countries, countryCodes) = readCountries(context!!)
//        val adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, countries)
        val adapter = SpinnerAdapter(context!!, countries.toTypedArray(), countryCodes, flagsCountryCodes, flagsDrawables)
        countries_spinner.adapter = adapter

        val index = countries.indexOf(currentCountry)
        countries_spinner.setSelection(index);

        countries_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                currentCountry = countries[position]
                currentCountryCode = countryCodes[position]

                setupCovidDataGridView()
            }
            override fun onNothingSelected(parent: AdapterView<*>) { }
        }
    }
}
