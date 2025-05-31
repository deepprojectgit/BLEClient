package com.demo.bleclient.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase.QueryCallback
import com.demo.bleclient.ble.BleClient
import com.demo.bleclient.data.dao.LogDao
import com.demo.bleclient.data.db.AppDatabase
import com.demo.bleclient.data.repository.LogRepository
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext appContext: Context): Context = appContext

    @Provides
    @Singleton
    fun provideAppDatabase(appContext: Context): AppDatabase = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java,
        "LogDB"
    ).build()

    @Provides
    @Singleton
    fun provideDao(appDatabase: AppDatabase) : LogDao = appDatabase.logDao()


    @Provides
    @Singleton
    fun provideBLEClient(context: Context,logDao: LogDao) : BleClient = BleClient(context,logDao)
}