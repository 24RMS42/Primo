package com.primo.goods.mvp

import android.net.Uri
import com.primo.network.new_models.Count
import com.primo.network.new_models.Product
import com.primo.utils.base.BasePresenter
import com.primo.utils.base.BaseView

interface GoodsScannerView : BaseView {

    fun addProduct(product: Product?)

    fun onCheckShippingCardBeforeCheckout(result: Array<String?>)

    fun getCountResult(counts: Count)
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

    abstract fun updateUserLanguage(language: String)

    abstract fun checkShippingCardBeforeCheckout()

    abstract fun getPublicCount()

    abstract fun getLiveCount()
}