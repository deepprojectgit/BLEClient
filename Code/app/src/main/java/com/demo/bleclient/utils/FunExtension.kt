package com.demo.bleclient.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

inline fun LifecycleOwner.lifeCycleLaunch(crossinline block: suspend () -> Unit) {
    lifecycleScope.launch {
        block()
    }
}

fun ByteArray.toHexStringData(): String = joinToString(" ") { "%02X".format(it) }

fun ByteArray.decodeToStringOrFallback(): String =
    try {
        String(this, Charsets.UTF_8)
    } catch (e: Exception) {
        this.toHexStringData()
    }

inline fun <reified T> String.fromJson(): T {
    return Gson().fromJson(this, object : TypeToken<T>() {}.type)
}