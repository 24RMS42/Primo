package com.primo.utils.views

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import android.widget.EditText

import com.primo.R

class PrefixedEditText : AppCompatEditText {

    private val mPrefixTextColor: Int
    private var prefix = ""
    private val prefixSpace: Int

    constructor(context: Context) : this(context, null) {
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PrefixedEditText)

        prefix = typedArray.getString(R.styleable.PrefixedEditText_prefix_text)
        prefixSpace = typedArray.getDimensionPixelSize(R.styleable.PrefixedEditText_prefix_space, 0)
        mPrefixTextColor = typedArray.getColor(R.styleable.PrefixedEditText_prefix_color, Color.BLACK)

        setPrefix()

        typedArray.recycle()
    }

    /**
     * Draw prefix-text with chosen color @property mPrefixTextColor
     */

    private fun setPrefix() {
        setCompoundDrawables(TextDrawable(), null, null, null)
    }

    private inner class TextDrawable : Drawable() {

        init {

            setBounds(0, 0, paint.measureText(prefix).toInt() + prefixSpace, textSize.toInt())
        }

        override fun draw(canvas: Canvas) {
            val paint = paint
            paint.color = mPrefixTextColor
            val lineBaseline = getLineBounds(0, null)
            canvas.drawText(prefix, 0f, (canvas.clipBounds.top + lineBaseline).toFloat(), paint)
        }

        override fun setAlpha(alpha: Int) {
            /* Not supported */
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            /* Not supported */
        }

        override fun getOpacity(): Int {
            return 1
        }
    }
}