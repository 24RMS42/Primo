package com.primo.utils

import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.primo.R

fun View.showSnack(resId: Int, duration: Int = Snackbar.LENGTH_LONG){

    val snack = Snackbar.make(this, resId, duration)
    val snackView = snack.view
    snackView.setBackgroundColor(Color.WHITE)
    val text = snackView.findViewById(android.support.design.R.id.snackbar_text) as TextView
    text.setTextColor(ContextCompat.getColor(this.context.applicationContext, R.color.color_red))
    snack.show()
}