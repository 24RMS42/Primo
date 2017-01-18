package com.primo.goods.mvp

import com.primo.network.new_models.WishItem
import com.primo.utils.base.BasePresenter
import com.primo.utils.base.BaseView

interface GoodsWishlistView : BaseView {

    fun showWishes(wishes: MutableList<WishItem>)

    fun deleteItem(wishItem: WishItem)
}

abstract class GoodsWishlistPresenter(view : GoodsWishlistView) : BasePresenter<GoodsWishlistView>(view) {

    override fun onResume() {
    }

    override fun onPause() {
    }

    abstract fun getWishes()

    abstract fun removeWishItem(wishItem: WishItem)

    abstract fun addItemToCart(wishItem: WishItem)
}