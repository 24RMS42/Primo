package com.primo.goods.mvp

import android.net.Uri
import com.primo.network.new_models.Product
import com.primo.utils.base.BasePresenter
import com.primo.utils.base.BaseView

interface GoodsScannerView : BaseView {

    fun addProduct(product: Product?)
}

abstract class GoodsScannerPresenter(view : GoodsScannerView) : BasePresenter<GoodsScannerView>(view) {

    override fun onResume() {
    }

    override fun onPause() {
    }

    abstract fun setPermission(isGranted : Boolean)

    abstract fun onDecodeImage(uri : Uri)

    abstract fun onResumeScanning()

    abstract fun onStopScanning()

}