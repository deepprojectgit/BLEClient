package com.demo.bleclient.utils

import com.demo.bleclient.data.model.BleEvent


object GlobalEventBus {
    val eventDevice = SingleEventFlow<BleEvent>()
}