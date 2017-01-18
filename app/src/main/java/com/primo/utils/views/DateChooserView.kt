package com.primo.utils.views

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.primo.R

class DateChooserView : LinearLayout, View.OnClickListener {

    object time {
        val MORNING = 1
        val AFTERNOON = 2
        val EVENING = 3
    }

    private var morning : AppCompatButton? = null
    private var afternoon : AppCompatButton? = null
    private var evening : AppCompatButton? = null

    var timePosition = 2

    var timePositionChangeListener : OnTimePositionChangeListener? = null
        set(value) {field = value}

    constructor(context: Context) : this(context, null) {
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {

        inflate(context, R.layout.date_chooser_view, this)

        orientation = HORIZONTAL
        background = ContextCompat.getDrawable(context, R.drawable.rounded_border)

        morning = findViewById(R.id.morning) as AppCompatButton
        afternoon = findViewById(R.id.afternoon) as AppCompatButton
        evening = findViewById(R.id.evening) as AppCompatButton

        com.primo.utils.setOnClickListener(this, morning, afternoon, evening)
    }

    private fun clearViews() {

        morning?.setBackgroundColor(Color.TRANSPARENT)
        afternoon?.setBackgroundColor(Color.TRANSPARENT)
        evening?.setBackgroundColor(Color.TRANSPARENT)

        morning?.setTextColor(R.color.color_grey)
        afternoon?.setTextColor(R.color.color_grey)
        evening?.setTextColor(R.color.color_grey)

        morning?.isClickable = true
        afternoon?.isClickable = true
        evening?.isClickable = true
    }

    private fun paintViews(position : Int, button : AppCompatButton) {

        button.isClickable = false
        button.setTextColor(Color.WHITE)

        timePosition = position
        timePositionChangeListener?.onTimePositionChanged(position)

        when(position) {
            time.MORNING -> button.setBackgroundResource(R.drawable.rounded_border_btn_left)
            time.AFTERNOON -> button.setBackgroundResource(R.drawable.normal_btn)
            time.EVENING -> button.setBackgroundResource(R.drawable.rounded_border_btn_right)
        }
    }

    override fun onClick(v: View?) {

        clearViews()

        when(v?.id) {
            R.id.morning -> paintViews(time.MORNING, v as AppCompatButton)
            R.id.afternoon -> paintViews(time.AFTERNOON, v as AppCompatButton)
            R.id.evening -> paintViews(time.EVENING, v as AppCompatButton)
        }

    }

    fun setTime(position : Int) {

        when(position) {

            time.MORNING -> paintViews(time.MORNING, morning as AppCompatButton)
            time.AFTERNOON -> paintViews(time.AFTERNOON, afternoon as AppCompatButton)
            time.EVENING -> paintViews(time.EVENING, evening as AppCompatButton)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, resources.getDimensionPixelSize(R.dimen.date_chooser_view_height))
    }

    interface OnTimePositionChangeListener {
        fun onTimePositionChanged(timePosition: Int)
    }
}
