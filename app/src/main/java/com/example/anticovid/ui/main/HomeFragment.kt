package com.example.anticovid.ui.main

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.anticovid.R
import com.example.anticovid.utils.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private val homeViewModel = HomeViewModel()
    private lateinit var flagsDrawables : MutableList<Drawable>
    private lateinit var flagsCountryCodes : MutableList<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.let {
            ViewModelProvider(it).get(MainViewModel::class.java).apply {
                sign_out.setOnClickListener {
                    signOut()
                }
            }
        }

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
            Log.d("Covid data","CountryDataModel: $countryDataModel")

            val gridAdapter = GridAdapter(context!!, countryDataModel)
            covid_gridView.adapter = gridAdapter
        })

        setupCountriesSpinner()
    }

    private fun setupCountriesSpinner() {
        val pair = loadImages(context!!)
        flagsDrawables = pair.first
        flagsCountryCodes = pair.second

        val (countries, countryCodes) = readCountries(context!!)
//        val adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, countries)
        val adapter = SpinnerAdapter(context!!, countries.toTypedArray(), countryCodes, flagsCountryCodes, flagsDrawables)
        countries_spinner.adapter = adapter

        countries_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Log.d("Covid data","Selected country: ${countries[position]}")
                homeViewModel.onCountrySelected(countries[position], countryCodes[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>) { }
        }

        countries_spinner.setSelection(countries.indexOf(homeViewModel.currentCountry));
    }
}
