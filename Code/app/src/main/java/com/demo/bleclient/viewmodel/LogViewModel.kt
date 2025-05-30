package com.demo.bleclient.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.demo.bleclient.data.model.LogEntity
import com.demo.bleclient.data.repository.LogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(private val logRepository: LogRepository, ) : ViewModel() {
    fun mDoorLiveData(address:String): LiveData<MutableList<LogEntity>> = logRepository.getDeviceList(address).flowOn(Dispatchers.IO).asLiveData()
}