package com.example.anticovid.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.anticovid.R
import com.example.anticovid.utils.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel = HomeViewModel(context)

        homeViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading == null)
                return@Observer

            // update UI
            if (isLoading) {
                covid_gridView.visibility = View.GONE
                covidGridViewLoadingBar.visibility = View.VISIBLE
            }
            else {
                covidGridViewLoadingBar.visibility = View.GONE
                covid_gridView.visibility = View.VISIBLE
            }
        })

        homeViewModel.countryDataModel.observe(viewLifecycleOwner, { countryDataModel ->
            val gridAdapter = GridAdapter(context!!, countryDataModel)
            covid_gridView.adapter = gridAdapter
        })

        setupCountriesSpinner(homeViewModel.currentCountry)
    }

    private fun setupCountriesSpinner(defaultCountry: String) {
        val (flagsDrawables, flagsCountryCodes) = loadImages(context!!)
        val (countries, countryCodes) = readCountries(context!!)
        val spinnerAdapter = SpinnerAdapter(context!!, countries.toTypedArray(), countryCodes, flagsCountryCodes, flagsDrawables)

        countries_spinner.apply {
            adapter = spinnerAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    homeViewModel.onCountrySelected(countries[position], countryCodes[position])
                }
                override fun onNothingSelected(parent: AdapterView<*>) { }
            }

            setSelection(countries.indexOf(defaultCountry))
        }
    }
}