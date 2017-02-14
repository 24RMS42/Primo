/**
 * Changes:
 *
 * - Add swipe down effect
 *
 * 2015 © Primo . All rights reserved.
 */

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
    private var y1: Float = 0f

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
                y1 = ev.y
                Log.d("Test", "== Down point: " + x1 + "" + y1)
                onSwipeListener?.onDown()
            }
            MotionEvent.ACTION_UP -> {
                val x2 = ev.x
                val y2 = ev.y
                val deltaX = x2 - x1
                val deltaY = y2 - y1
                Log.d("Test", "== Up point: " + x2 + "" + y2)

                if (deltaX < 0 && Math.abs(deltaX) > MIN_DISTANCE) {
                    onSwipeListener?.onSwipeToLeft()
                    return true
                } else if (Math.abs(deltaX) > MIN_DISTANCE) {
                    onSwipeListener?.onSwipeToRight()
                    return true
                } else if (deltaY < 0 && Math.abs(deltaY) > MIN_DISTANCE){
                    onSwipeListener?.onSwipeDown()
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
        fun onSwipeDown(){}
    }

    companion object {

        internal val MIN_DISTANCE = 100
    }
}
