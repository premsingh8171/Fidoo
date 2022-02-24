package com.prudhvir3ddy.rideshare.utils

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.SystemClock
import android.view.animation.LinearInterpolator
import com.fidoo.user.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlin.math.abs
import kotlin.math.atan


object MapUtils {

  var isMarkerRotating:Boolean = false

  fun getCarBitmap(context: Context): Bitmap {
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.rider)
    return Bitmap.createScaledBitmap(bitmap, 50, 100, false)
  }
  fun getDestinationBitmap(): Bitmap {
    val height = 20
    val width = 20
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    paint.color = Color.BLACK
    paint.style = Paint.Style.FILL
    paint.isAntiAlias = true
    canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
    return bitmap
  }

  fun getRotation(start: LatLng, end: LatLng): Float {
    val latDifference: Double = abs(start.latitude - end.latitude)
    val lngDifference: Double = abs(start.longitude - end.longitude)
    var rotation = -1F
    when {
      start.latitude < end.latitude && start.longitude < end.longitude -> {
        rotation = Math.toDegrees(atan(lngDifference / latDifference)).toFloat()
      }
      start.latitude >= end.latitude && start.longitude < end.longitude -> {
        rotation = (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 90).toFloat()
      }
      start.latitude >= end.latitude && start.longitude >= end.longitude -> {
        rotation = (Math.toDegrees(atan(lngDifference / latDifference)) + 180).toFloat()
      }
      start.latitude < end.latitude && start.longitude >= end.longitude -> {
        rotation =
          (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 270).toFloat()
      }
    }
    return rotation
  }

  fun bearingBetweenLocations(latLng1: LatLng, latLng2: LatLng): Float {
    val PI = 3.14159
    val lat1 = latLng1.latitude * PI / 180
    val long1 = latLng1.longitude * PI / 180
    val lat2 = latLng2.latitude * PI / 180
    val long2 = latLng2.longitude * PI / 180
    val dLon = long2 - long1
    val y = Math.sin(dLon) * Math.cos(lat2)
    val x = Math.cos(lat1) * Math.sin(lat2) - (Math.sin(lat1)
            * Math.cos(lat2) * Math.cos(dLon))
    var brng = Math.atan2(y, x)
    brng = Math.toDegrees(brng)
   // brng = (brng + 360) % 360
    return brng.toFloat()
  }

  fun rotateMarker(marker: Marker, toRotation: Float) {
    if (!isMarkerRotating) {
      val handler = Handler()
      val start: Long = SystemClock.uptimeMillis()
      val startRotation: Float = marker.getRotation()
      val duration: Long = 1000
      val interpolator = LinearInterpolator()
      handler.post(object : Runnable {
        override fun run() {
          isMarkerRotating = true
          val elapsed: Long = SystemClock.uptimeMillis() - start
          val t: Float = interpolator.getInterpolation(elapsed.toFloat() / duration)
          val rot = t * toRotation + (1 - t) * startRotation
          marker.setRotation(if (-rot > 180) rot / 2 else rot)
          if (t < 1.0) {
            // Post again 16ms later.
            handler.postDelayed(this, 16)
          } else {
            isMarkerRotating = false
          }
        }
      })
    }
  }


  public fun angleFromCoordinate(
    lat1: Double, long1: Double, lat2: Double,
    long2: Double
  ): Double {
    val dLon = long2 - long1
    val y = Math.sin(dLon) * Math.cos(lat2)
    val x = Math.cos(lat1) * Math.sin(lat2) - (Math.sin(lat1)
            * Math.cos(lat2) * Math.cos(dLon))
    var brng = Math.atan2(y, x)
    brng = Math.toDegrees(brng)
    brng = (brng + 360) % 360
    brng = 360 - brng // count degrees counter-clockwise - remove to make clockwise
    return brng
  }
}