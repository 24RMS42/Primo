package com.primo.goods.mvp

import com.primo.network.new_models.CartItem
import com.primo.utils.base.BasePresenter
import com.primo.utils.base.BaseView
import java.util.*

interface GoodsHistoryView : BaseView {

    fun showHistoryResult(products: ArrayList<CartItem>?)
}

abstract class GoodsHistoryPresenter(view: GoodsHistoryView) : BasePresenter<GoodsHistoryView>(view) {

    override fun onResume() {
    }

    override fun onPause() {
    }

    abstract fun getOrderHistory(page: Int)

    abstract fun addItemToCart(item: CartItem)
}