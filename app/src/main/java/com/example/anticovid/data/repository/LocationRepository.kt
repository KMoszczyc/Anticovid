package com.example.NavigationForBlind.DeviceData

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.math.MathUtils
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import java.text.SimpleDateFormat
import java.util.*


class LocationRepository(private var context: Context, private var map: GoogleMap) {
    private var database: FirebaseDatabase
    private var userId: String
    private var locationsRefPath: String
    private var lastLocationRef: DatabaseReference
    private var lastUpdateTimeRef: DatabaseReference

    private lateinit var fusedLocationClient: FusedLocationProviderClient;
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location

    private val INTERVAL = 30000L
    private val FASTEST_INTERVAL = 30000L
    private val SMALLEST_DISPLACEMENT = 1f
    private val ZOOM = 17f

    private var isLocationFound = false;

    private lateinit var heatmapProvider: HeatmapTileProvider
    private var heatmapOverlay: TileOverlay? = null

    init
    {
        lastLocation = Location("")
        locationUpdatePeriodically()
        startLocationUpdates()

        userId = FirebaseAuth.getInstance().currentUser!!.uid
        database = FirebaseDatabase.getInstance("https://anticovid-93262-default-rtdb.europe-west1.firebasedatabase.app/")
        locationsRefPath = "user-$userId/locations/"
        lastLocationRef =  database.getReference("user-$userId/lastLocation")
        lastUpdateTimeRef =  database.getReference("user-$userId/lastUpdateTime")

        database.reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children
                val locations = mutableListOf<LatLng>()
                for (user in users) {
                    val userKeySplit = user.key!!.split("-")

                    //  dont add current user circle on map
                    if (userKeySplit.size <= 1 || (userKeySplit.size > 1 && userId != userKeySplit[1])) {
                        val location = stringToLatLng(user.child("lastLocation").value.toString())
                        locations.add(location)
                    }
                }

                addHeatMap(locations)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })

//        addRandomLocations(100)

        map.setOnCameraChangeListener(object : OnCameraChangeListener {
            private var currentZoom = -1f
            override fun onCameraChange(pos: CameraPosition) {
                if (pos.zoom != currentZoom && ::heatmapProvider.isInitialized){
                    currentZoom = pos.zoom;
                    val radius = getHeatmapRadius(pos.zoom.toDouble())
                    println("radius:  $radius, zoom:  ${pos.zoom}")
                    heatmapProvider.setRadius(radius)
                }
            }
        })
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
                    Log.wtf("lasyLocation:", lastLocation.toString())
                    val locationRef = database.getReference(locationsRefPath + getCurrentDateTime())
                    val locationStr = "${lastLocation.getLatitude()} ${lastLocation.getLongitude()}"
                    locationRef.setValue(locationStr)
                    lastLocationRef.setValue(locationStr)
                    lastUpdateTimeRef.setValue(getCurrentDateTimePretty())

                    println(locationsRefPath + getCurrentDateTime() + ": " + locationStr)

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

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): String {
        return Calendar.getInstance().time.toString("yyyy-MM-dd_HH:mm:ss")
    }

    fun getCurrentDateTimePretty(): String {
        return Calendar.getInstance().time.toString("yyyy-MM-dd HH:mm:ss")
    }


    fun stringToLatLng(location: String): LatLng {
        val splitLocation = location.split(" ")
        return LatLng(splitLocation[0].toDouble(), splitLocation[1].toDouble())
    }

    private fun addHeatMap(locations: MutableList<LatLng>) {
        if(!::heatmapProvider.isInitialized){
            // Create the gradient.
//            val colors = intArrayOf(
//                Color.rgb(102, 225, 0),  // green
//                Color.rgb(255, 0, 0) // red
//            )
//            //  important numbers, especially second one - how many people turn the heatmap red
//            val startPoints = floatArrayOf(0.1f, 1f)
//            val gradient = Gradient(colors, startPoints)

            heatmapProvider = HeatmapTileProvider.Builder()
                .data(locations)
                .opacity(0.7)
                .radius(50)
//                .gradient(gradient)
                .build()
            heatmapOverlay = map.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))!!
        }
        else{
            heatmapProvider.setData(locations)
            heatmapOverlay!!.clearTileCache()
        }

    }

    //    for debug locations
    fun addRandomLocations(number: Int){
        val defaultLat = 51.109088f
        val defaultLng = 17.031801f

        for(i in 0..number){
            val randomLat = random(defaultLat - 0.001f, defaultLat + 0.001f)
            val randomLng = random(defaultLng - 0.002f, defaultLng + 0.002f)
            val ref = database.getReference("user_" + i + "/lastLocation")
            ref.setValue("$randomLat $randomLng")
        }
    }

    fun random(min: Float, max: Float): Double {
        return min + Math.random() * (max - min)
    }

//    to be fixed
    fun getHeatmapRadius(zoom: Double): Int {
        val someLatValue = 51.726332;
        val desiredRadiusInMeters = 20;

        val metersPerPx = 156543.03392 * Math.cos(someLatValue * Math.PI / 180) / Math.pow(2.0,zoom);
        return MathUtils.clamp((desiredRadiusInMeters / metersPerPx).toInt(), 1, 50)
    };
}