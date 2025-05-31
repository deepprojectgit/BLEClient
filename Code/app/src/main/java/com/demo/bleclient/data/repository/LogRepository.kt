package com.demo.bleclient.data.repository

import com.demo.bleclient.ble.BleClient
import com.demo.bleclient.data.dao.LogDao
import com.demo.bleclient.data.model.LogEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogRepository @Inject constructor(val logDao: LogDao,val bleClient: BleClient){
    suspend fun insertBLEAddress(logEntity: LogEntity) = logDao.insert(logEntity)
    fun getDeviceList(address:String) = logDao.getDeviceAddress(address)
    fun getBLEClient():BleClient = bleClient
}