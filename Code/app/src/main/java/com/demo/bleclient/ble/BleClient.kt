package com.demo.bleclient.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import com.demo.bleclient.data.model.EventModel
import com.demo.bleclient.data.model.LogEntity
import java.util.UUID
import com.demo.bleclient.utils.StatusBLEConnection
import com.demo.bleclient.utils.decodeToStringOrFallback
import com.demo.bleclient.utils.fromJson


class BleClient(val context: Context) {

    var bleScanner : BluetoothLeScanner
    var bleAdapter : BluetoothAdapter
    private var bluetoothGatt: BluetoothGatt? = null
    private var handler: Handler ? = null
    private  var runnable: Runnable  ? = null
    private var isScanning = false

    private val TEMPERATURE_SERVICE_UUID = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb")
    private val TEMPERATURE_CHAR_UUID = UUID.fromString("00002A1C-0000-1000-8000-00805f9b34fb")


    private val scanSettings by lazy {
        ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // Change as needed
            .build()
    }

    var onCheckBluetooth: ((
        deviceDataLocation: BluetoothDevice?,
        statusBLEConnection: StatusBLEConnection?,
        logEntity: LogEntity?,
    ) -> Unit)? = null


    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            onCheckBluetooth?.invoke(
                result.device,
                StatusBLEConnection.IS_DEVICE_GET_SUCCESS,
                null
            )
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            onCheckBluetooth?.invoke(
                null,
                StatusBLEConnection.IS_DEVICE_GET_FAIL,
                null
            )
        }
    }

    init {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bleAdapter = bluetoothManager.adapter
        bleScanner = bleAdapter.bluetoothLeScanner
        handler = Handler(Looper.getMainLooper())
    }

    fun getBleAdaptor(): BluetoothAdapter {
        return bleAdapter
    }


    @SuppressLint("MissingPermission")
    fun startScan() {
        if (!isScanning) {
            onCheckBluetooth?.invoke(
                null,
                StatusBLEConnection.IS_DEVICE_SCANNING_START,
                null
            )
            isScanning = true
            val filters = listOf(ScanFilter.Builder().setServiceUuid(ParcelUuid(TEMPERATURE_SERVICE_UUID)).build())

            bleScanner.startScan(filters, scanSettings, leScanCallback)
            runnable = Runnable {
                stopScan()
            }
            runnable?.let {  handler?.postDelayed(it, 5000) }
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        runnable?.let {  handler?.removeCallbacks(it) }
        bleScanner.stopScan(leScanCallback)
        isScanning = false
        onCheckBluetooth?.invoke(
            null,
            StatusBLEConnection.IS_DEVICE_SCANNING_STOP,
            null
        )
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                bluetoothGatt?.discoverServices()

            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                bluetoothGatt?.close()
                bluetoothGatt = null
            }

        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (service in gatt.services) {
                    val service = gatt.getService(TEMPERATURE_SERVICE_UUID)
                    val characteristic = service?.getCharacteristic(TEMPERATURE_CHAR_UUID)
                    if (characteristic != null) {
                        enableNotifications(gatt, characteristic)
                    }
                    onCheckBluetooth?.invoke(
                        null,
                        StatusBLEConnection.IS_DEVICE_CONNECTED_COMPLETED,
                        null
                    )
                }
            } else {
                onCheckBluetooth?.invoke(
                    null,
                    StatusBLEConnection.IS_DEVICE_CONNECTED_FAIL,
                    null
                )
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            val value = characteristic.value
            val dataEvent = value.decodeToStringOrFallback().fromJson<EventModel>()
            val logEntity = LogEntity(deviceAddress=gatt.device.address, logDate = dataEvent.date, logTime = dataEvent.time, logType = dataEvent.type)
            onCheckBluetooth?.invoke(
                null,
                null,
                logEntity
            )
        }

        // Optional: check notification write response
        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            Log.d("BLE Client", "Descriptor write: $status")
        }


    }

    @SuppressLint("MissingPermission")
    private fun enableNotifications(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        gatt.setCharacteristicNotification(characteristic, true)
        val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
        descriptor?.let {
            it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(it)
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}