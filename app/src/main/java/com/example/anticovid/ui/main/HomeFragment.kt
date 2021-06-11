package com.example.anticovid.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.anticovid.R
import com.example.anticovid.data.model.MyData
import com.example.anticovid.data.model.SHARED_PREFERENCES_INFECTION_RISK
import com.example.anticovid.data.model.SHARED_PREFERENCES_MY_DATA
import com.example.anticovid.data.model.SHARED_PREFERENCES_SETTINGS
import com.example.anticovid.utils.loadImages
import com.example.anticovid.utils.readCountries
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.current_state_card.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var sharedPref: SharedPreferences
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel = ViewModelProvider(this, HomeViewModelFactory(requireContext()))
            .get(HomeViewModel::class.java).apply {
                isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
                    if (isLoading == null)
                        return@Observer

                    // update UI
                    if (isLoading) {
                        covid_gridView.visibility = View.GONE
                        covidGridViewLoadingBar.visibility = View.VISIBLE
                    } else {
                        covidGridViewLoadingBar.visibility = View.GONE
                        covid_gridView.visibility = View.VISIBLE
                    }
                })

                countryDataModel.observe(viewLifecycleOwner, Observer { countryDataModel ->
                    val gridAdapter = GridAdapter(requireContext(), countryDataModel)
                    covid_gridView.adapter = gridAdapter
                })
        }

        sharedPref = requireContext().getSharedPreferences(
            SHARED_PREFERENCES_MY_DATA,
            Context.MODE_PRIVATE
        ).also {
            updateGreetings(it)
            updateInfectionRisk(it)
            listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == SHARED_PREFERENCES_MY_DATA)
                    updateGreetings(sharedPreferences)
                if(key == SHARED_PREFERENCES_INFECTION_RISK)
                    updateInfectionRisk(sharedPreferences)
            }.also { listener ->
                it.registerOnSharedPreferenceChangeListener(listener)
            }
        }

        setupCountriesSpinner(homeViewModel.currentCountry)
    }

    fun updateInfectionRisk(sharedPref: SharedPreferences){
        val infectionRisk = sharedPref.getInt(SHARED_PREFERENCES_INFECTION_RISK, 0)
        Log.wtf("changeInfectionRisk",infectionRisk.toString())
        if (infectionRisk<30){
            current_state_tv.text = "Low risk of infection"
            state_color_strip.setBackgroundColor(resources.getColor(R.color.colorStateGreen));
        }
        else if(infectionRisk<90){
            current_state_tv.text = "Medium risk of infection"
            state_color_strip.setBackgroundColor(resources.getColor(R.color.colorStateOrange));

        }
        else{
            current_state_tv.text = "High risk of infection"
            state_color_strip.setBackgroundColor(resources.getColor(R.color.colorTextError));
        }
    }

    private fun updateGreetings(sharedPref: SharedPreferences) {
        val myDataString = sharedPref.getString(SHARED_PREFERENCES_MY_DATA, "")
        val myDataTypeToken = object : TypeToken<MyData>() {}.type
        val myData = Gson().fromJson(myDataString, myDataTypeToken) ?: MyData()

        greeting_tv.text = "${getString(R.string.hello)} ${myData.username}"
    }

    private fun setupCountriesSpinner(defaultCountry: String) {
        val (flagsDrawables, flagsCountryCodes) = loadImages(requireContext())
        val (countries, countryCodes) = readCountries(requireContext())
        val spinnerAdapter = SpinnerAdapter(
            requireContext(),
            countries.toTypedArray(),
            countryCodes,
            flagsCountryCodes,
            flagsDrawables
        )

        countries_spinner.apply {
            adapter = spinnerAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    homeViewModel.onCountrySelected(countries[position], countryCodes[position])
                }
                override fun onNothingSelected(parent: AdapterView<*>) { }
            }

            setSelection(countries.indexOf(defaultCountry))
        }
    }
}
