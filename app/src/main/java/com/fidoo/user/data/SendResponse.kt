package com.fidoo.user.data

import android.os.Parcel
import android.os.Parcelable

@Suppress("DEPRECATION")
data class SendResponse(
    val accesstoken: String,
    val id: String,
    val mobile: String,
    val otp: String,
    val is_newUser: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(accesstoken)
        parcel.writeString(id)
        parcel.writeString(mobile)
        parcel.writeString(otp)
        parcel.writeString(is_newUser)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SendResponse> {
        override fun createFromParcel(parcel: Parcel): SendResponse {
            return SendResponse(parcel)
        }

        override fun newArray(size: Int): Array<SendResponse?> {
            return arrayOfNulls(size)
        }
    }
}