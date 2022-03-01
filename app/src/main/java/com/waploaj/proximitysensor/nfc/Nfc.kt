package com.waploaj.proximitysensor.nfc

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.waploaj.proximitysensor.R

class Nfc : AppCompatActivity(), NfcAdapter.CreateNdefMessageCallback {

    private var nfcAdapter:NfcAdapter? = null
    private lateinit var txtNfc:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)
        txtNfc = findViewById(R.id.txtNfc)

        //Check for available NfcAdapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null){
            Toast.makeText(this, "Nfc adapter is not available",Toast.LENGTH_LONG).show()
            finish()
            return
        }

        //Register callback
        nfcAdapter?.setNdefPushMessageCallback(this, this)
    }

    override fun createNdefMessage(event: NfcEvent?): NdefMessage {
        val text = "Beam me up Android !\n\n" +
                    "Beam time" + System.currentTimeMillis()
        return NdefMessage(
            arrayOf(
                NdefRecord.createMime("com.waploaj.proximitysensor.nfc",text.toByteArray())
            )
        )
    }

    override fun onResume() {
        super.onResume()
        //check activity started due to Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action){
            processIntent(intent)
        }
    }

    //parse the ndef message from the intent and parse to textView
    private fun processIntent(intent: Intent?) {
        txtNfc = findViewById(R.id.txtNfc)
        //Only one message was sent during beam
        intent?.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMsg ->
            (rawMsg[0] as NdefMessage).apply {
                //record[0] containt MiME type 1 contain AAR if present
                txtNfc.text = String(records[0].payload)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //onResume get call after this to handle the intent
        setIntent(intent)
    }
}