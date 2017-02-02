package com.primo.utils.base

import android.widget.Toast
import com.primo.main.MainClass
import com.primo.utils.interfaces.DebugInformer
import com.primo.utils.other.RxEvent

interface BaseView: DebugInformer{

    fun showProgress()

    fun hideProgress()

    fun showMessage(message : String?, event: RxEvent? = null)

    override fun showErrorMessage(message: String) {
        //Toast.makeText(MainClass.context, "API ERROR: $message", Toast.LENGTH_LONG).show()
    }

    override fun showToastMessage(message: String) {
        Toast.makeText(MainClass.context, message, Toast.LENGTH_LONG).show()
    }

    fun displayErrorMessage(message : String? = "", code: Int?, event: RxEvent? = null)
}

abstract class BasePresenter <T : BaseView>(view : T) {

    protected var view: T? = view

    abstract fun onResume()

    abstract fun onPause()

    open fun onDestroy() {
        view = null
    }
}