/**
 * Changes:
 *
 * - Show message when click + (the item is 0 stock)
 * - 503 HTTP status handling
 * - Increase badge number when add item to cart
 *
 * 2015 © Primo . All rights reserved.
 */


package com.primo.goods.mvp

import android.util.Log
import android.widget.Toast
import com.primo.R
import com.primo.main.MainClass
import com.primo.network.api_new.*
import com.primo.network.new_models.Cart
import com.primo.network.new_models.WishItem
import com.primo.utils.consts.CONNECTION_ERROR
import com.primo.utils.consts.INTERNAL_ERROR
import com.primo.utils.consts.UNAVAILABLE_ERROR
import com.primo.utils.getInt
import org.json.JSONObject


class GoodsWishlistPresenterImpl(view: GoodsWishlistView): GoodsWishlistPresenter(view) {

    override fun getWishes() {

        val auth = MainClass.getAuth()
        val token = auth.access_token

        if (!token.isEmpty()) {

            val wishlistCall: GetListOfWishes = GetListOfWishesImpl(object: ApiResult<MutableList<WishItem>> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: MutableList<WishItem>) {
                    view?.showWishes(result)
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("TEST", message)

                    displayMessage(message, code)
                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

            wishlistCall.getListOfWishes(token)
        }
    }

    override fun removeWishItem(wishItem: WishItem) {

        val auth = MainClass.getAuth()
        val token = auth.access_token

        if (!token.isEmpty()) {

            val wishlistCall: DeleteWishlistItem = DeleteWishlistItemImpl(object : ApiResult<String> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: String) {
                    Log.d("TEST", result)
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("TEST", message)

                    displayMessage(message, code)
                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

            wishlistCall.deleteWishlistItem(wishItem.wishlistId, token)
        }
    }

    override fun addItemToCart(wishItem: WishItem) {

        val auth = MainClass.getAuth()
        val token = auth.access_token

        if (!token.isEmpty()) {
            Log.d("Test", "token not empty")
            val addCall: AddItemToCart = AddItemToCartImpl(object : ApiResult<Cart?> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: Cart?) {

                    if (result != null && !result.cartId.isEmpty()) {
                        val auth = MainClass.getAuth()
                        auth.cart_id = result.cartId
                        MainClass.saveAuth(auth)
                    }
                    view?.deleteItem(wishItem)
                    view?.updateCartBadge() //increase badge
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("Test", "add item cart error:" + message)

                    displayMessage(message, code)
                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

            val stockId = wishItem.stock.stock_id
            if (!stockId.isEmpty())
                addCall.addItemToCart(wishItem.stock.stock_id, 1.toString(), token)
        }
    }

    fun displayMessage(message: String, code: Int){

        var codeError = -1
        val jsonObject = JSONObject(message)
        codeError = jsonObject.getInt("error_code", -1)

        view?.displayErrorMessage("", codeError)
    }
}