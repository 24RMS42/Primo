package com.primo.goods.mvp

import com.primo.utils.base.BasePresenter
import com.primo.utils.base.BaseView

interface GoodsPagerView : BaseView {

    fun onBought(isSuccess: Boolean)
}

abstract class GoodsPagerPresenter(view: GoodsPagerView): BasePresenter<GoodsPagerView>(view) {

    override fun onPause() {

    }

    override fun onResume() {

    }

    abstract fun placeAnOrder(location: Pair<Float, Float> = Pair(0f, 0f))
}
