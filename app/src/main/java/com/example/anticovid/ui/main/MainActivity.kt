package com.example.anticovid.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

            // attach fragment
            when (mainFragment) {
                MainFragmentEnum.HomeFragment ->
                    attachFragment(supportFragmentManager.findFragmentByTag(mainFragment.tag) ?: HomeFragment(), mainFragment.tag)
                MainFragmentEnum.MapFragment ->
                    attachFragment(supportFragmentManager.findFragmentByTag(mainFragment.tag) ?: MapFragment(), mainFragment.tag)
            }
        })

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    mainViewModel.onFragmentSelected(MainFragmentEnum.HomeFragment)
                    true
                }
                R.id.map -> {
                    mainViewModel.onFragmentSelected(MainFragmentEnum.MapFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun attachFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment, tag).commit()
    }
}