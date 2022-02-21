package com.waploaj.proximitysensor

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class GpsSensor : AppCompatActivity() {
    private lateinit var locationManger:LocationManager
    private lateinit var txtLongitude:TextView
    private lateinit var txtLatitude:TextView
    private var isGpsSensorOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gps_sensor)

        //call location service cast as locationManger
        locationManger = getSystemService(LOCATION_SERVICE) as LocationManager
        txtLatitude = findViewById(R.id.latitude)
        txtLongitude = findViewById(R.id.longitude)

        //Check and request gps permission on device
        if (ContextCompat.checkSelfPermission(
                applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION), 111)

        }else{
            isGpsSensorOn = true
        }

        if (isGpsSensorOn == true){
            //Requst location on update take distancebtn, timeinterval and listener
            locationManger.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000.toLong(),
                1.toFloat()
            ){
                Log.d("GpsEnable", "onCreate: .")
                if (locationManger != null){
                    //Get the lastKnownLocation
                    val location = locationManger.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    txtLatitude.setText("${location!!.latitude} latiude")
                    txtLongitude.setText("${location!!.longitude} longitude")
                }
            }

        }
    }


}