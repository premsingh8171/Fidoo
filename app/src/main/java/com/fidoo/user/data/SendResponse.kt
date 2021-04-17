package com.fidoo.user.data

import java.io.Serializable

data class SendResponse(val accesstoken : String,val id : String,val mobile : String, val otp: String):Serializable