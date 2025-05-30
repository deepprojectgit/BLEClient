package com.demo.bleclient.service


import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.demo.bleclient.R
import com.demo.bleclient.ble.BleClient
import com.demo.bleclient.data.model.BleEvent
import com.demo.bleclient.data.model.LogEntity
import com.demo.bleclient.data.repository.LogRepository
import com.demo.bleclient.utils.GlobalEventBus
import com.demo.bleclient.utils.StatusBLEConnection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BleForegroundService : Service() {

    lateinit var bleClient: BleClient

    val scopeSendEvent = CoroutineScope(Dispatchers.IO)

    private lateinit var job: Job

    val scopeReceiveEvent = CoroutineScope(Dispatchers.IO)

    private lateinit var job1: Job

    @Inject
    lateinit var logRepository: LogRepository

    companion object {
        var isServiceRunning: Boolean = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        bleClient = BleClient(this@BleForegroundService)
        isServiceRunning = true
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bleClient.onCheckBluetooth = { device, status,logEntity ->
            job = scopeSendEvent.launch {
                GlobalEventBus.eventDevice.emit(BleEvent(statusBLEConnection=status, bleDevice = device))
                logEntity?.let {item ->
                    try {
                        logRepository.insertBLEAddress(logEntity)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }
            }
        }

        job1 = scopeReceiveEvent.launch {
            GlobalEventBus.eventDevice.eventReceive.collect{ event->
                if(event.isStartScan){

                    bleClient.startScan()
                    GlobalEventBus.eventDevice.emit(BleEvent(isStartScan = false))
                }
                if(event.isTryToConnect){
                    event.bleDevice?.let { device->  bleClient.connectToDevice(device) }
                    GlobalEventBus.eventDevice.emit(BleEvent(isStartScan = false))
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if(this::job.isInitialized){
            job.cancel()
        }
        if(this::job1.isInitialized){
            job1.cancel()
        }
        bleClient.stopScan()
        bleClient.disconnect()
        isServiceRunning = false
    }

}