package com.waploaj.proximitysensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView

class MotionStepCounter : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManger:SensorManager
    private lateinit var stepSensor:Sensor
    private lateinit var textView: TextView
    private var isSensorAvailable = false
    private var stepCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motion_step_counter)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        textView = findViewById(R.id.txtMotion)

        sensorManger = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManger.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
            isSensorAvailable = true
            stepSensor = sensorManger.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        }else{
            textView.setText("There is no Sensor available")
            isSensorAvailable = false
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if (p0!!.sensor == stepSensor ){
            stepCount = p0!!.values[0].toInt()
            textView.setText("There number of step taken is ${stepCount}")
        }
    }

    override fun onPause() {
        super.onPause()
        if (isSensorAvailable == true || sensorManger.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
            sensorManger.unregisterListener(this)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isSensorAvailable == true|| sensorManger.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
            sensorManger.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
}