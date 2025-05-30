package com.demo.bleclient.data.model

import android.bluetooth.BluetoothDevice
import com.demo.bleclient.utils.StatusBLEConnection

data class BleEvent(
    val isDisconnect: Boolean = false,
    val isStopScan: Boolean = false,
    val isStartScan: Boolean = false,
    val isTryToConnect: Boolean = false,
    val eventData: String?=null,
    val isBleOn: Boolean = false,
    val isBleOff: Boolean = false,
    val bleDevice: BluetoothDevice?=null,
    val logEntity : LogEntity ?=null,
    val statusBLEConnection: StatusBLEConnection?= null
)
