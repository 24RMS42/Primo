package com.primo.utils.other

import android.text.Editable
import android.text.NoCopySpan
import android.view.View

interface MultipleTextWatcher : NoCopySpan {

    fun afterTextChanged(v: View, s: Editable){}

    fun onTextChanged(v: View, s: CharSequence, start: Int, before: Int, count: Int){}

    fun beforeTextChanged(v: View, s: CharSequence, start: Int, count: Int, after: Int){}
}