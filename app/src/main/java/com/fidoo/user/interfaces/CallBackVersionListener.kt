package com.fidoo.user.interfaces

interface CallBackVersionListener {
    fun onGetResponse(isUpdateAvailable: Boolean, newVersion: String?, currentVersion: String?)
}