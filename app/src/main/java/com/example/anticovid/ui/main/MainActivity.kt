package com.example.anticovid.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.anticovid.R
import com.example.anticovid.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.net.NetworkInterface
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private val REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.supportActionBar?.hide()

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java).apply {
            isUserSignedIn.observe(this@MainActivity, Observer { isUserSignedIn ->
                if (isUserSignedIn == null)
                    return@Observer

                if (!isUserSignedIn) {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            })
        }

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
        with(supportFragmentManager) {
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
        val isFineLocationDenied = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )  == PackageManager.PERMISSION_DENIED
        val isCourseLocationDenied = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )  == PackageManager.PERMISSION_DENIED
        val isInternetDenied = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET
        )

        if (isFineLocationDenied || isCourseLocationDenied ||  isInternetDenied) {
            requestPermissions(permissions, REQUEST_CODE)
        }

        val requestCode = 1;
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600)
        }
        startActivityForResult(discoverableIntent, requestCode)
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
        val mBtAdapter = BluetoothAdapter.getDefaultAdapter()
        dicoverDevicesPeriodically(mBtAdapter)
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device!!.name
                    val deviceHardwareAddress = device.address // MAC address

                    val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)
                        .toInt() // signal strength
                    Log.wtf("bluetooth",deviceName + " " + deviceHardwareAddress + " " + device.type + " " + device.uuids + ", rssi: " + rssi + ", meters: " + rssiToMeters(rssi))
                }
            }
        }
    }

    fun dicoverDevicesPeriodically(btAdapter: BluetoothAdapter){
        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                if (btAdapter.isDiscovering()) {
                    btAdapter.cancelDiscovery();
                }
                btAdapter.startDiscovery();
                mainHandler.postDelayed(this, 60000)
            }
        })
    }

    fun rssiToMeters(rssi: Int): Double {
        val tx_power = -60.0
        return Math.pow(10.0, (tx_power - rssi) / (10 * 2))
    }
}