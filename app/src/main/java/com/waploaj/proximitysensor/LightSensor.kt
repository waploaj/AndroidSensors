package com.waploaj.proximitysensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class LightSensor : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager:SensorManager
    private lateinit var lightSensor:Sensor
    private lateinit var txtLight:TextView
    private var isLightSensorOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_light_sensor)
        txtLight = findViewById(R.id.txtLight)

        //call sensorManger for getting sensorService and default sensor available
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            isLightSensorOn = true
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        }else{
            isLightSensorOn = false
        }

    }

    //override a method on sensor when data is change to update to textview
    override fun onSensorChanged(p0: SensorEvent?) {
        if (p0!!.sensor == lightSensor){
            txtLight.setText("the value is ${p0!!.values[0]}")
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    //unregister a sensor to stop listen on changes
    override fun onPause() {
        super.onPause()
        if (isLightSensorOn == true){
            sensorManager.unregisterListener(this, lightSensor)
        }
    }

    //To start register a sensor when listen to changes
    override fun onResume() {
        super.onResume()
        if (isLightSensorOn == true){
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
}