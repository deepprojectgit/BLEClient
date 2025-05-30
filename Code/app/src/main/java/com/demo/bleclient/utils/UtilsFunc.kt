package com.demo.bleclient.utils


import android.content.Context
import android.content.Intent
import com.demo.bleclient.service.BleForegroundService

class UtilsFunc {
    companion object{
        fun startBleForegroundService(context: Context) {
            if (BleForegroundService.isServiceRunning) {
                return
            }
            try {
                val serviceIntent= Intent(context, BleForegroundService::class.java)
                context.stopService(serviceIntent)
                context.startService(serviceIntent)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        fun stopBleForegroundService(context: Context) {
            try {
                val serviceIntent= Intent(context, BleForegroundService::class.java)
                context.stopService(serviceIntent)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}