package com.example.NavigationForBlind.DeviceData

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng


class LocationRepository(private var context: Context, private var map: GoogleMap) {
    private lateinit var fusedLocationClient: FusedLocationProviderClient;
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location

    private val INTERVAL = 1000L
    private val FASTEST_INTERVAL = 500L
    private val SMALLEST_DISPLACEMENT = 0f
    private val ZOOM = 17f

    private var isLocationFound = false;

    init
    {
        lastLocation = Location("")
        locationUpdatePeriodically()
        startLocationUpdates()
    }

    fun updateLocation()
    {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            Log.wtf("location", "requiered location permissions")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if(location!=null)
            {
                lastLocation = location
            }
        }
    }

    private fun locationUpdatePeriodically()
    {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest()
        locationRequest.interval = INTERVAL
        locationRequest.fastestInterval = FASTEST_INTERVAL
        locationRequest.smallestDisplacement = SMALLEST_DISPLACEMENT
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    lastLocation = locationResult.lastLocation
                    Log.wtf("locationchange", lastLocation.toString())

                    if(!isLocationFound) {
                        val latLng = LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())
                        map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                        map.animateCamera(CameraUpdateFactory.zoomTo(ZOOM));
                        isLocationFound = true
                    }
                }
            }
        }

        updateLocation()
    }

    fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ) {
            Log.wtf("location", "requiered location permissions")
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true;
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    private fun checkInternetConenction(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null
    }
}