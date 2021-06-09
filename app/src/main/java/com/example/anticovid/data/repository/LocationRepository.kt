package com.example.NavigationForBlind.DeviceData

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import com.google.maps.android.heatmaps.HeatmapTileProvider
import java.math.BigDecimal
import java.math.RoundingMode
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
    private var last_update_time_threshold_seconds = 300

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
                updateHeatMap(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })

        addRandomLocations(900)

        map.setOnCameraChangeListener(object : OnCameraChangeListener {
            private var currentZoom = -1f
            override fun onCameraChange(pos: CameraPosition) {
                if (pos.zoom != currentZoom && ::heatmapProvider.isInitialized) {
                    currentZoom = pos.zoom;
                    val radius = getHeatmapRadius(pos.zoom.toDouble())
                    println("radius:  $radius, zoom:  ${pos.zoom}")
                    heatmapProvider.setRadius(radius)
                }
            }
        })
    }

    fun updateHeatMap(snapshot: DataSnapshot){
        val users = snapshot.children
        val locations = mutableListOf<LatLng>()
        for (user in users) {
            val userKeySplit = user.key!!.split("-")

            //  dont add current user circle on map, show locations updated in last 5 minutes only
            if (userKeySplit.size <= 1 || (userKeySplit.size > 1 && userId != userKeySplit[1] && getDateDiffInSeconds(user.child("lastUpdateTime").value.toString())<last_update_time_threshold_seconds)) {
                val location = stringToLatLng(user.child("lastLocation").value.toString())
                locations.add(location)
            }
        }

        addHeatMap(locations)
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
                    updateDataInFirebase()

                    if(!isLocationFound) {
                        val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                        map.animateCamera(CameraUpdateFactory.zoomTo(ZOOM));
                        isLocationFound = true
                    }
                }
            }
        }

        updateLocation()
    }

    fun updateDataInFirebase(){
        Log.wtf("lastLocation:", lastLocation.toString())
        val locationStr = "${lastLocation.getLatitude()} ${lastLocation.getLongitude()}"
        lastLocationRef.setValue(locationStr)
        lastUpdateTimeRef.setValue(getCurrentDateTimePretty())

        println(locationsRefPath + getCurrentDateTime() + ": " + locationStr)
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

    fun getDateDiffInSeconds(dateStr: String): Int {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr)
        val today = Date()
        val diff = today.time - date.time
        val seconds = (diff / 1000).toInt()
        return seconds
    }

    fun stringToLatLng(location: String): LatLng {
        val splitLocation = location.split(" ")
        return LatLng(splitLocation[0].toDouble(), splitLocation[1].toDouble())
    }

    private fun addHeatMap(locations: MutableList<LatLng>) {
        if(!::heatmapProvider.isInitialized){
            heatmapProvider = HeatmapTileProvider.Builder()
                .data(locations)
                .opacity(0.7)
                .radius(10)
                .maxIntensity(1.0)
                .build()
            heatmapOverlay = map.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))!!
        }
        else{
            heatmapOverlay!!.clearTileCache()
            heatmapProvider.setData(locations)
        }
    }

    //    for debug locations
    fun addRandomLocations(number: Int){
        val defaultLat = 51.109088f
        val defaultLng = 17.031801f
        val radius = 0.01f
        for(i in 0..number){
//            val randomLat = random(defaultLat - radius, defaultLat + radius)
//            val randomLng = random(defaultLng - radius*2, defaultLng + radius*2)
            val (lat, lng) = randomLatLng(radius, defaultLat, defaultLng)
            val ref = database.getReference("user_" + i + "/lastLocation")
            ref.setValue("$lat $lng")
        }
    }

    fun random(min: Float, max: Float): Double {
        return min + Math.random() * (max - min)
    }

    fun randomLatLng(radius:Float, centerLat: Float, centerLng: Float): Pair<Double, Double> {
        val r = radius * Math.sqrt(Math.random())
        val theta = Math.random() * 2 * Math.PI
        val lat = centerLat + r * Math.cos(theta)/1.7
        val lng = centerLng + r * Math.sin(theta)

//        more than 8 decimal places cause google maps heatmap locations to bug on different zooms
        val latRounded = BigDecimal(lat).setScale(6, RoundingMode.HALF_EVEN).toDouble()
        val lngRounded = BigDecimal(lng).setScale(6, RoundingMode.HALF_EVEN).toDouble()

        return Pair(latRounded, lngRounded)
    }

//    to be fixed
    fun getHeatmapRadius(zoom: Double): Int {
        val someLatValue = 51.726332;
        val desiredRadiusInMeters = 20;

        val metersPerPx = 156543.03392 * Math.cos(someLatValue * Math.PI / 180) / Math.pow(
            2.0,
            zoom
        );
        return MathUtils.clamp((desiredRadiusInMeters / metersPerPx).toInt(), 1, 50)
    };
}