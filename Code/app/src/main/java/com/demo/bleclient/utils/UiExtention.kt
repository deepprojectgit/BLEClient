package com.demo.bleclient.utils

import android.content.Context
import androidx.annotation.StringRes


fun Context.getStr(@StringRes id: Int) = resources.getString(id)
