package com.example.anticovid.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.anticovid.R
import com.example.anticovid.ui.login.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

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
}