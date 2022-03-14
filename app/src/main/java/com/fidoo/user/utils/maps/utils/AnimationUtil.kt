package com.prudhvir3ddy.rideshare.utils

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

object AnimationUtils {

  fun polyLineAnimator(): ValueAnimator {
    val valueAnimator = ValueAnimator.ofInt(0, 100)
    valueAnimator.interpolator = LinearInterpolator()
    valueAnimator.duration = 2000
    return valueAnimator
  }

  fun cabAnimator(): ValueAnimator {
    val valueAnimator = ValueAnimator.ofFloat(5f, 5f)
    valueAnimator.duration = 1000
    valueAnimator.interpolator = LinearInterpolator()
    return valueAnimator
  }

}