package com.waploaj.proximitysensor


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView


class MainActivity : AppCompatActivity(),SensorEventListener {
   private lateinit var sensorManger:SensorManager
   private lateinit var proximitSensor:Sensor
   private var isAsensorON:Boolean =false
   private lateinit var textVie:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textVie = findViewById(R.id.txtView)
        sensorManger = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (sensorManger.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null){
            isAsensorON = true
            proximitSensor = sensorManger.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        }else{
            textVie.setText("There is no proximity sensor!")
            isAsensorON = false
        }

    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if (isAsensorON){
            textVie.setText("There  distace btn is ${p0!!.values[0]} cm")
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onResume() {
        super.onResume()
        sensorManger.registerListener(this, proximitSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        if (isAsensorON == true){
            sensorManger.unregisterListener(this)
        }
    }




}