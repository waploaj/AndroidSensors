package com.waploaj.proximitysensor.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.result.contract.ActivityResultContracts
import com.waploaj.proximitysensor.R
import java.util.jar.Attributes

class BluetoothActivity : AppCompatActivity() {
    private lateinit var toogleButton: ToggleButton
    private lateinit var listView: ListView


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
        pairedDevice?.forEach { device ->
            println("device name --" + device.name)
            println("de" + device.address)
            println(device.bondState)
            println(device)
        }


//        val adapters = ArrayAdapter<BluetoothDevice>(this,android.R.layout.simple_list_item_1,list);
//        listView.setAdapter(adapters)

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