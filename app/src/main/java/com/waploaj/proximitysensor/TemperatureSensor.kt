package com.waploaj.proximitysensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class TemperatureSensor : AppCompatActivity(),SensorEventListener {
    private lateinit var sensorManger:SensorManager
    private lateinit var tempSensor:Sensor
    private lateinit var txtTemp:TextView
    private var isSensorTempOn:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temperature_sensor)
        txtTemp = findViewById(R.id.txtTemp)

        //use sensorManger for getting sensor service and get default sensor
        sensorManger = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManger.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            isSensorTempOn = true
            tempSensor = sensorManger.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        }else{
            isSensorTempOn = false
            txtTemp.setText("The sensor is not available on your phone")

        }
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
    //detect when there is change in sensor
    override fun onSensorChanged(p0: SensorEvent?) {
        if (p0!!.sensor == tempSensor)txtTemp.setText("the value of temperature is ${p0!!.values[0]} celicius")
    }

    //register sensor listener through sensorManger
    override fun onResume() {
        super.onResume()
        if (sensorManger.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            sensorManger.registerListener(this,tempSensor,SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    //Unregister sensor listener onpause
    override fun onPause() {
        super.onPause()
        if (isSensorTempOn == true){
            sensorManger.unregisterListener(this)
        }
    }
}