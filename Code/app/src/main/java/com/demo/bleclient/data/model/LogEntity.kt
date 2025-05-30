package com.demo.bleclient.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    @ColumnInfo(name = "device_address") val deviceAddress: String?,
    @ColumnInfo(name = "log_type") val logType: String?,
    @ColumnInfo(name = "log_date") val logDate: String?,
    @ColumnInfo(name = "log_time") val logTime: String?
)
