package com.demo.bleclient.receiver

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BluetoothReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
            when (state) {
                BluetoothAdapter.STATE_OFF -> {
                    // Bluetooth is OFF
                    Toast.makeText(context, "Bluetooth is OFF", Toast.LENGTH_SHORT).show()
                }
                BluetoothAdapter.STATE_ON -> {
                    // Bluetooth is ON
                    Toast.makeText(context, "Bluetooth is ON", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}