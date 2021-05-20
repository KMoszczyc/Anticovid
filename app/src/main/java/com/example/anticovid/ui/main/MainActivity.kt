package com.example.anticovid.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.anticovid.R
import com.example.anticovid.ui.login.*
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

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    switchFragment(MainFragmentEnum.HomeFragment)
                    true
                }
                R.id.map -> {
                    switchFragment(MainFragmentEnum.MapFragment)
                    true
                }
                R.id.profile -> {
                    switchFragment(MainFragmentEnum.ProfileFragment)
                    true
                }
                R.id.settings -> {
                    switchFragment(MainFragmentEnum.SettingsFragment)
                    true
                }
                else -> false
            }
        }

        // default fragment
        switchFragment(MainFragmentEnum.HomeFragment)

        requestAllPermissions()
    }

    private fun switchFragment(mainFragmentEnum: MainFragmentEnum) {
        supportFragmentManager.run {
            findFragmentByTag(mainViewModel.mainFragmentEnum.value?.tag)?.let {
                beginTransaction().hide(it).commit()
            }

            findFragmentByTag(mainFragmentEnum.tag).let { fragment ->
                if (fragment != null)
                    beginTransaction().show(fragment).commit()
                else
                    beginTransaction().add(
                        R.id.main_fragment_container,
                        when (mainFragmentEnum) {
                            MainFragmentEnum.HomeFragment -> HomeFragment()
                            MainFragmentEnum.MapFragment -> MapFragment()
                            MainFragmentEnum.ProfileFragment -> ProfileFragment()
                            MainFragmentEnum.SettingsFragment -> SettingsFragment()
                        },
                        mainFragmentEnum.tag
                    ).commit()
            }
        }

        mainViewModel.onFragmentSwitch(mainFragmentEnum)
    }

    private fun requestAllPermissions()
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