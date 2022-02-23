package com.waploaj.proximitysensor.accessory

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.waploaj.proximitysensor.R
import java.io.IOException

class MicrophoneAccess : AppCompatActivity() {
    private lateinit var recordButton:Button
    private lateinit var playButton: Button
    private lateinit var stopButton: Button
    private lateinit var txtView: TextView


    private var recoder:MediaRecorder? = null
    private var player:MediaPlayer? = null
    private var fileName:String = ""
    private var permissionToReccordAccepted = false

    //verify permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToReccordAccepted = if (requestCode == 111){
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        }else{
            false
        }
        if (!permissionToReccordAccepted)finish()
    }

    //Set method to call on Record button listener
    private fun onRecord(start:Boolean) = if (start){
        startRecording()
    }else{
        stopRecording()
    }

    // set method to call on play button listener
    private fun onPlay(start: Boolean) = if (start){
        startPlaying()
    }else{
        stopPlaying()
    }

    //set mediaPlayer datasource, prepare and start to play
    private fun startPlaying(){
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            }catch (e:IOException){
                Log.e("AudioTest", "prepare() failed")
            }
        }
    }

    //release player to stop playing
    private fun stopPlaying(){
        player?.release()
        player = null
    }

    //set MediaRecorder output file, AudioSource, OutPutFormat,AudioEncoder
    private fun startRecording(){
        recoder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
            }catch (e:IOException){
                Log.e("AudioTest", "prepare failed")
            }
            start()
        }
    }
    //Stop recording method to release recorder
    private fun stopRecording(){
        recoder?.apply {
            stop()
            release()
        }
        recoder = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        //Check for permission to record and request permision to record
        if (ContextCompat.checkSelfPermission(
                applicationContext,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE),111
            )
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_microphone_access)
        recordButton = findViewById(R.id.record)
        playButton = findViewById(R.id.pause)
        stopButton = findViewById(R.id.play)
        txtView = findViewById(R.id.txtMicrophone)

        txtView.text = ("Give permission")
        recordButton.isEnabled = true
        stopButton.isEnabled = false
        playButton.isEnabled = false

        //set the filename to be stored
        fileName = "${externalCacheDir?.absoluteFile}/+audiorecordTest.3gp"


        //Set recording button listener
        var startRecording = true
        recordButton.setOnClickListener {
            onRecord(startRecording)
            txtView.text = when(startRecording){
                true-> "Start Recording"
                false-> "Stop recording"
            }
            this.stopButton.isEnabled = true
            this.playButton.isEnabled = true
            this.recordButton.isEnabled = false
            startRecording = !startRecording


        }

        //Set stopRecording button listener
        stopButton.setOnClickListener {
            onStop()
                this.recordButton.isEnabled = true
                this.playButton.isEnabled = true
                this.stopButton.isEnabled = false
        }

        //set playRecording button listener
        var startPlay = true
        playButton.setOnClickListener {
            onPlay(startPlay)
            txtView.text = when(startPlay){
                true -> "Start Playing"
                false -> "Stop playing"
            }
            this.recordButton.isEnabled = true
            this.stopButton.isEnabled = true
            this.playButton.isEnabled = false
            startPlay = !startPlay
        }

    }

    //overrid the stop method to release media and recorder
    override fun onStop() {
        super.onStop()
        recoder?.release()
        recoder = null
        player?.release()
        player = null
    }


}