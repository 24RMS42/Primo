package com.primo.utils

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation

const val SHORT_DURATION = 300L
const val NORMAL_DURATION = 500L
const val LONG_DURATION = 1000L

fun View.tapScaleAnimation() {
    val tap = ObjectAnimator.ofPropertyValuesHolder(this,
            PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f, 1f),
            PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f, 1f))
    tap.duration = SHORT_DURATION;
    tap.start();
}

fun View.bouncingAnimation() {
    val enter = ObjectAnimator.ofPropertyValuesHolder(this,
            PropertyValuesHolder.ofFloat("translationY", 0f, -20f, 0f, -20f, 0f, -20f, 0f))
    enter.duration = NORMAL_DURATION;
    enter.start()
}

fun View.rotate(startDegree: Float, endDegree: Float) {

    val rotate = ObjectAnimator.ofPropertyValuesHolder(this,
            PropertyValuesHolder.ofFloat("rotation", startDegree, endDegree))
    rotate.duration = SHORT_DURATION
    rotate.start()
}

fun View.previewAnimation() {

    val enter = ObjectAnimator.ofPropertyValuesHolder(this,
            PropertyValuesHolder.ofFloat("translationY", -this.height.toFloat(), 0f),
            PropertyValuesHolder.ofFloat("alpha", 0f, 1f))
    enter.duration = NORMAL_DURATION
    enter.start()

    val exit = ObjectAnimator.ofPropertyValuesHolder(this,
            PropertyValuesHolder.ofFloat("translationY", 0f, this.height.toFloat()),
            PropertyValuesHolder.ofFloat("alpha", 1f, 0f))
    exit.duration = NORMAL_DURATION
    exit.startDelay = LONG_DURATION + LONG_DURATION
    exit.start()
}

fun View.slideAnimation() {

    val enter = ObjectAnimator.ofPropertyValuesHolder(this,
            PropertyValuesHolder.ofFloat("translationX", this.width.toFloat(), 0f),
            PropertyValuesHolder.ofFloat("alpha", 0f, 1f))
    enter.duration = SHORT_DURATION
    enter.start()

    val exit = ObjectAnimator.ofPropertyValuesHolder(this,
            PropertyValuesHolder.ofFloat("translationX", 0f, this.width.toFloat()),
            PropertyValuesHolder.ofFloat("alpha", 1f, 0f))
    exit.duration = SHORT_DURATION
    exit.startDelay = LONG_DURATION + LONG_DURATION
    exit.start()
}

fun View.expandAnimation(height: Int) {

    val view = this
    view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val targetHeight = height

    view.layoutParams.height

    view.layoutParams.height = 1
    view.visibility = View.VISIBLE


    val a = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            view.layoutParams.height = if (interpolatedTime == 1f)
                ViewGroup.LayoutParams.WRAP_CONTENT
            else
                (targetHeight * interpolatedTime).toInt()
            view.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    a.duration = SHORT_DURATION
    view.startAnimation(a)
}

fun View.collapseAnimation() {

    val view = this

    val initialHeight = view.measuredHeight
    val a = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime == 1f) {
                view.visibility = View.GONE
            } else {
                view.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                view.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    a.duration = SHORT_DURATION
    view.startAnimation(a)
}