package com.primo.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.NumberPicker

fun setOnClickListener(listener: View.OnClickListener, vararg views: View?) {

    for (view in views) {
        view?.setOnClickListener(listener)
    }
}

fun showKeyboard(view: View?) {

    val inputMethodManager = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager;
    inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
}

fun NumberPicker.setDividerColor(color: Int) {

    val pickerFields = NumberPicker::class.java.declaredFields
    for (pf in pickerFields) {
        if (pf.name == "mSelectionDivider") {
            pf.isAccessible = true
            try {
                val colorDrawable = ColorDrawable(color)
                pf.set(this, colorDrawable)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: Resources.NotFoundException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

            break
        }
    }
}