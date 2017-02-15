/**
 * Changes:
 *
 * - 503 HTTP status handling
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.goods.mvp

import android.util.Log
import com.primo.R
import com.primo.main.MainClass
import com.primo.network.api_new.*
import com.primo.network.new_models.Cart
import com.primo.network.new_models.CartItem
import com.primo.network.new_models.WishItem
import com.primo.utils.consts.*
import com.primo.utils.getInt
import org.json.JSONObject
import java.util.*


class GoodsHistoryPresenterImpl(view: GoodsHistoryView) : GoodsHistoryPresenter(view) {


    override fun getOrderHistory(page: Int) {

        val auth = MainClass.getAuth()
        val token = auth.access_token

        if (!token.isEmpty()) {

            val orderHistoryCall: GetOrderHistory = GetOrderHistoryImpl(object: ApiResult<ArrayList<CartItem>> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: ArrayList<CartItem>) {
                    view?.showHistoryResult(result)
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

            orderHistoryCall.getOrderHistory(page, token)
        }
    }

    override fun addItemToCart(item: CartItem) {

        val auth = MainClass.getAuth()
        val token = auth.access_token

        if (!token.isEmpty()) {
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

            val stockId = item.stock.stock_id
            if (!stockId.isEmpty())
                addCall.addItemToCart(item.stock.stock_id, 1.toString(), token)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun displayMessage(message: String, code: Int){

        var codeError = -1
        val jsonObject = JSONObject(message)
        codeError = jsonObject.getInt("error_code", -1)

        view?.displayErrorMessage("", codeError)
    }
}