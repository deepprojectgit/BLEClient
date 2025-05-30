package com.demo.bleclient.utils

enum class StatusBLEConnection(val value:Int){
    IS_DEVICE_SCANNING_START(1),
    IS_DEVICE_SCANNING_STOP(2),
    IS_DEVICE_GET_SUCCESS(3),
    IS_DEVICE_GET_FAIL(4),
    IS_DEVICE_CONNECTED_COMPLETED(5),
    IS_DEVICE_CONNECTED_FAIL(6),
}
