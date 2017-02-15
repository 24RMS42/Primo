/**
 * Changes:
 *
 * - 503 HTTP status handling
 * - Stripe error code handling
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.goods.mvp


import android.app.usage.UsageEvents
import android.util.Log
import com.primo.R
import com.primo.main.MainClass
import com.primo.network.api_new.ApiResult
import com.primo.network.api_new.PlaceAnOrder
import com.primo.network.api_new.PlaceAnOrderImpl
import com.primo.utils.consts.*
import com.primo.utils.getInt
import com.primo.utils.other.Events
import com.primo.utils.other.RxEvent
import org.json.JSONObject

class GoodsPagerPresenterImpl(view: GoodsPagerView) : GoodsPagerPresenter(view) {


    override fun placeAnOrder(location: Pair<Float, Float>) {

        val auth = MainClass.getAuth()
        val token = auth.access_token
        val shippingId = auth.shipping_id
        val cartId = auth.cart_id
        val creditCardId = auth.creditcard_id

        Log.d("Test", "token:" + token + "shippingID:" + shippingId + "cartId:"+ cartId + "creditCardId:"+ creditCardId)
        if (!token.isEmpty() && !shippingId.isEmpty() && !cartId.isEmpty() && !creditCardId.isEmpty()) {
            val orderCall: PlaceAnOrder = PlaceAnOrderImpl(object : ApiResult<Boolean> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: Boolean) {
                    Log.d("Test", "bought result:" + result)
                    view?.onBought(result)
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("Test", "checkout error message: " + message)
                    Log.d("Test", "checkout error code: " + code)

                    displayMessage(code, message)
                }

                override fun onComplete() {
                    Log.d("Test", "checkout complete")
                    view?.hideProgress()
                }
            })

//            val lat = if (location.first == 0f) "" else location.first.toString()
//            val lng = if (location.second == 0f) "" else location.second.toString()

            val lat = if (location.first == 0f) null else location.first
            val lng = if (location.second == 0f) null else location.second

            orderCall.placeAnOrder(creditCardId, shippingId, cartId, token, lat, lng)
        } else {
            Log.d("Test", "some auth fields are empty")
            view?.showErrorMessage("Some auth fields are empty: ${MainClass.getAuth()}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun displayMessage(code: Int, message: String){

        var codeError = -1
        val jsonObject = JSONObject(message)
        codeError = jsonObject.getInt("error_code", -1)

        when (code) {
            //Reject Error, call showMessage func with REJECTED event
            OWN_ORDER_REJECT_CODE -> view?.showMessage(message, RxEvent(key = Events.ORDER_REJECT))
            OWN_REJECT_CODE       -> view?.showMessage(message, RxEvent(key = Events.REJECT))
            else -> view?.displayErrorMessage("", codeError)
        }
    }
}