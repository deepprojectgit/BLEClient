package com.demo.bleclient.data.dao

import android.location.Address
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.demo.bleclient.data.model.LogEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface LogDao {

    @Query("select * from logentity where device_address = :deviceAddress")
    fun getDeviceAddress(deviceAddress:String): Flow<MutableList<LogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(logs: LogEntity)
}