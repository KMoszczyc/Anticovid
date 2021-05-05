package com.example.anticovid.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.anticovid.R
import com.example.anticovid.ui.login.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private val REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.supportActionBar?.hide()

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        mainViewModel.isUserSignedIn.observe(this, Observer { isUserSignedIn ->
            if (isUserSignedIn == null)
                return@Observer

            if (!isUserSignedIn) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        })

        mainViewModel.mainFragmentEnum.observe(this, Observer { mainFragment ->
            if (mainFragment == null)
                return@Observer

            val fragment = supportFragmentManager.findFragmentByTag(mainFragment.tag)
            if (fragment != null)
                showFragment(fragment)
            else
                addFragment(
                    when (mainFragment) {
                        MainFragmentEnum.HomeFragment -> HomeFragment()
                        MainFragmentEnum.MapFragment -> MapFragment()
                        MainFragmentEnum.SettingsFragment -> SettingsFragment()
                    }, mainFragment.tag
                )
        })

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            hideCurrentFragment()

            when(item.itemId) {
                R.id.home -> {
                    mainViewModel.onFragmentSelected(MainFragmentEnum.HomeFragment)
                    true
                }
                R.id.map -> {
                    mainViewModel.onFragmentSelected(MainFragmentEnum.MapFragment)
                    true
                }
                R.id.settings -> {
                    mainViewModel.onFragmentSelected(MainFragmentEnum.SettingsFragment)
                    true
                }
                else -> false
            }
        }

        requestAllPermissions()
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().show(fragment).commit()
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().add(R.id.main_fragment_container, fragment, tag).commit()
    }

    private fun hideCurrentFragment() {
        supportFragmentManager.run {
            findFragmentByTag(mainViewModel.mainFragmentEnum.value?.tag)?.let {
                beginTransaction().hide(it).commit()
            }
        }
    }

    fun requestAllPermissions()
    {
        val isFineLocationDenied = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)  == PackageManager.PERMISSION_DENIED
        val isCourseLocationDenied = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)  == PackageManager.PERMISSION_DENIED
        val isInternetDenied = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET
        )

        if (isFineLocationDenied || isCourseLocationDenied ||  isInternetDenied) {
            requestPermissions(permissions, REQUEST_CODE)
        }
    }
}