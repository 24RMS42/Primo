package com.primo.goods.mvp


import android.util.Log
import com.primo.main.MainClass
import com.primo.network.api_new.ApiResult
import com.primo.network.api_new.PlaceAnOrder
import com.primo.network.api_new.PlaceAnOrderImpl

class GoodsPagerPresenterImpl(view: GoodsPagerView) : GoodsPagerPresenter(view) {


    override fun placeAnOrder(location: Pair<Float, Float>) {

        val auth = MainClass.getAuth()
        val token = auth.access_token
        val shippingId = auth.shipping_id
        val cartId = auth.cart_id
        val creditCardId = auth.creditcard_id

        if (!token.isEmpty() && !shippingId.isEmpty() && !cartId.isEmpty() && !creditCardId.isEmpty()) {
            val orderCall: PlaceAnOrder = PlaceAnOrderImpl(object : ApiResult<Boolean> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: Boolean) {
                    view?.onBought(result)
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("TEST", message)
                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

//            val lat = if (location.first == 0f) "" else location.first.toString()
//            val lng = if (location.second == 0f) "" else location.second.toString()

            val lat = if (location.first == 0f) null else location.first
            val lng = if (location.second == 0f) null else location.second

            orderCall.placeAnOrder(creditCardId, shippingId, cartId, token, lat, lng)
        } else {
            view?.showErrorMessage("Some auth fields are empty: ${MainClass.getAuth()}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}