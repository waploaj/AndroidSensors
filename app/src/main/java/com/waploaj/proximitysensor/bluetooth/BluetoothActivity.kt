package com.waploaj.proximitysensor.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.result.contract.ActivityResultContracts
import com.waploaj.proximitysensor.R
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.IndexOutOfBoundsException
import java.util.*

private val myUUID:UUID = UUID.fromString("1232DSDAsdsdasda-dadsadasdaxe-k0mcwjcwwncw")
private const val myName = "hello"
private const val TAG = "MY_APP_DEBUG_TAG"
const val MESSAGE_READ = 0
const val MESSAGE_WRITE = 1
const val MESSAGE_TOAST = 2

class BluetoothActivity : AppCompatActivity() {
    private lateinit var toogleButton: ToggleButton
    private lateinit var listView: ListView
    private var bluetoothDevices = arrayListOf<BluetoothDevice>()
    private var deviceName = arrayListOf<String>()


    private inner class ServerAcceptThread:Thread(){
        private val mmServerSocket:BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE){
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(myName,myUUID)
        }

        override fun run() {
            //Keep listen until error occured or socket is returned
            var shouldKeepListen = true

            while (shouldKeepListen){
                val socket:BluetoothSocket? = try {
                    mmServerSocket?.accept()
                }catch (e:IOException){
                    Log.e("bluetoothSocket","ServerSocket failde",e)
                    shouldKeepListen = false
                    null
                }
                socket.also {
                    mmServerSocket?.close()
                    shouldKeepListen = false
                }
            }
        }

        //Close server socket and cause the thread to finish
        fun cancel(){
            try {
                mmServerSocket?.close()
            }catch (e:IOException){
                Log.e("ConnectionFailed!", "Connection close failed",e)
            }
        }
    }

    private inner class ClientConnectThread(device: BluetoothDevice):Thread(){
        private val mmSocket:BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE){
            device.createRfcommSocketToServiceRecord(myUUID)
        }

        public override fun run() {
            //Cancel the discovery process because it slow down the connection
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.let { socket ->
                socket.connect()
            }
        }

        fun cancel(){
            try {
                mmSocket?.close()
            }catch (e:IOException){
                Log.e("Socket", "Could not close the client socket",e)
            }
        }
    }

    class BluetoothService(
        //Handle that gets info from Bluetooth Service
        private val handler:Handler
    ){
        private inner class ConnectedThread(private val mmSocket:BluetoothSocket):Thread(){
            private val mmInPutStream:InputStream = mmSocket.inputStream
            private val mmOutPutStream:OutputStream = mmSocket.outputStream
            private val mmBuffer:ByteArray = ByteArray(1024)

            override fun run() {
                var numByte:Int //number of bytes returns from read()

                //keep listen to the InputStream until an error occured
                while (true){
                    //Read from inputStream
                    numByte = try {
                        mmInPutStream.read(mmBuffer)
                    }catch (e:IOException){
                        Log.e(TAG,"InputStream was disconnected",e)
                        break
                    }
                    //Send the message to Ui activity
                    val readMsg = handler.obtainMessage(
                        MESSAGE_READ,numByte,-1,mmBuffer
                    )
                    readMsg.sendToTarget()
                }
            }

            //Call this function to mainActivity to send data to remote device
            fun write(byte:ByteArray){
                try {
                    mmOutPutStream.write(byte)
                }catch (e:IOException){
                    Log.e(TAG,"Error occured during send messge",e)

                    //Send the failed message back to activity
                    val writeErrorMessage = handler.obtainMessage(MESSAGE_TOAST)
                    val bundle = Bundle().apply {
                        putString("Toast","could not send the data")
                    }
                    writeErrorMessage.data = bundle
                    handler.sendMessage(writeErrorMessage)
                    return
                }

                //Share the sent message with UI activity
                val writtenMsg = handler.obtainMessage(
                    MESSAGE_WRITE, -1,-1,mmBuffer
                )
                writtenMsg.sendToTarget()
            }

            //Call this method to activity to shut socket
            fun cancle(){
                try {
                    mmSocket.close()
                }catch (e:IOException){
                    Log.e(TAG,"Connection closed failed!")
                }
            }
        }
    }


    //An instance of bluetooth adapter
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        toogleButton = findViewById(R.id.toogleBtn)
        listView = findViewById(R.id.listView)

        //check for bluetooth adapter availability
        if (bluetoothAdapter == null) {
            Toast.makeText(
                this, "Bluetooth is not supported for this device", Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        //Enable bluetooth
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtAdapter = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            getResult.launch(enableBtAdapter)
        }

        //List of bt device
        val pairedDevice: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        var index:Int = 0
        if (pairedDevice!!.size >0){
            try {
                pairedDevice.forEach { device ->
                    bluetoothDevices[0] = device
                    deviceName[0] = device.name
                    index++
                }
            }catch (e:IndexOutOfBoundsException){
                e.localizedMessage
            }

        }


       val arrayAdapter:ArrayAdapter<String> = ArrayAdapter(
           this,android.R.layout.simple_list_item_1,deviceName
       )

        listView.adapter = arrayAdapter

        //Register for broadcast when device is discovered
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        //set the device to be discovered for five minutes
        val discoverableIntent: Intent =
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            }
        getDiscovarableIntentActivityResult.launch(discoverableIntent)


    }

    //Create BroadcastReceiver for ACTION_FOUND
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String? = intent?.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    //Discover has found a bluetooth device
                    //Get BluetoothDevice and its info from intent
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address //MAC address of device
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //unregister the ACTION_FOUND receiver
        unregisterReceiver(receiver)
    }

    //start activity for result bluetooth
    val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            Toast.makeText(
                this, "Bluetooth is on now", Toast.LENGTH_SHORT
            ).show()
        }
    }

    //Start activity for discovarableIntent
    val getDiscovarableIntentActivityResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){
            if (it.resultCode == Activity.RESULT_OK){
                Toast.makeText(
                    this,"Discovarable for 5min", Toast.LENGTH_SHORT
                ).show()
            }
        finish()
    }
}