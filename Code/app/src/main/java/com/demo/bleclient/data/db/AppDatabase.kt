package com.demo.bleclient.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.demo.bleclient.data.dao.LogDao
import com.demo.bleclient.data.model.LogEntity

@Database(entities = [LogEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
}