package com.example.anticovid.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.anticovid.R
import com.example.anticovid.data.model.DEFAULT_COUNTRY
import com.example.anticovid.data.model.SHARED_PREFERENCES_SETTINGS
import com.example.anticovid.data.model.SHARED_PREFERENCES_SETTINGS_DEFAULT_COUNTRY
import com.example.anticovid.data.model.SHARED_PREFERENCES_SETTINGS_DEFAULT_COUNTRY_CODE
import com.example.anticovid.utils.loadImages
import com.example.anticovid.utils.readCountries
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.let {
            ViewModelProvider(it).get(MainViewModel::class.java).let { mvm ->
                sign_out.setOnClickListener {
                    mvm.signOut()
                }
            }
        }

        sharedPref = context!!.getSharedPreferences(SHARED_PREFERENCES_SETTINGS,Context.MODE_PRIVATE).apply {
            getString(SHARED_PREFERENCES_SETTINGS_DEFAULT_COUNTRY, DEFAULT_COUNTRY)?.let {
                setupCountriesSpinner(it)
            }
        }
    }

    private fun setupCountriesSpinner(defaultCountry: String) {
        val (flagsDrawables, flagsCountryCodes) = loadImages(context!!)
        val (countries, countryCodes) = readCountries(context!!)
        val spinnerAdapter = SpinnerAdapter(context!!, countries.toTypedArray(), countryCodes, flagsCountryCodes, flagsDrawables)

        countries_spinner.apply {
            adapter = spinnerAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    with (sharedPref.edit()) {
                        putString(SHARED_PREFERENCES_SETTINGS_DEFAULT_COUNTRY, countries[position])
                        putString(SHARED_PREFERENCES_SETTINGS_DEFAULT_COUNTRY_CODE, countryCodes[position])
                        apply()
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>) { }
            }

            setSelection(countries.indexOf(defaultCountry))
        }
    }
}