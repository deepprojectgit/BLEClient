package com.demo.bleclient.data.model

import com.google.gson.annotations.SerializedName

data class EventModel(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("time")
	val time: String? = null,

	@field:SerializedName("type")
	val type: String? = null
)
