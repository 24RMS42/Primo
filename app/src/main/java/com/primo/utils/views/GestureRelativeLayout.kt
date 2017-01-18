package com.primo.utils.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.RelativeLayout
import android.widget.Toast


class GestureRelativeLayout : RelativeLayout {

    private var x1: Float = 0f

    var onSwipeListener: OnSwipeListener? = null
        set(value) {field = value}


    constructor(context: Context) : this(context, null) {
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    /**
     * This function intercepting all user touch events
     * and call motion listener @see OnSwipeListener
     */

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = ev.x
                onSwipeListener?.onDown()
            }
            MotionEvent.ACTION_UP -> {
                val x2 = ev.x
                val deltaX = x2 - x1

                if (deltaX < 0 && Math.abs(deltaX) > MIN_DISTANCE) {
                    onSwipeListener?.onSwipeToLeft()
                    return true
                } else if (Math.abs(deltaX) > MIN_DISTANCE) {
                    onSwipeListener?.onSwipeToRight()
                    return true
                }
            }
        }
        return false
    }

    interface OnSwipeListener {
        fun onSwipeToLeft() {}
        fun onSwipeToRight() {}
        fun onDown() {}
    }

    companion object {

        internal val MIN_DISTANCE = 100
    }
}
