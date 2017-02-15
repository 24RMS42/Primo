/**
 * Changes:
 *
 * - Stripe error code handling
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.network.api_new

import android.util.Log
import com.primo.network.new_models.CartItem
import com.primo.network.parsers.PrimoParsers
import com.primo.network.requests.OrderRequests
import com.primo.network.requests.APIPrimo
import com.primo.utils.NetworkException
import com.primo.utils.consts.*
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.net.UnknownHostException
import java.util.*


class PlaceAnOrderImpl(result: ApiResult<Boolean>) : PlaceAnOrder {

    private val result = result

    override fun placeAnOrder(creditcardId: String, shippingId: String, cartId: String, token: String,
                              lat: Float?, lng: Float?) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<Boolean> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(OrderRequests.placeAnOrder(creditcardId,
                        shippingId, cartId, token, lat, lng))?.execute()
                val body = response?.body()?.string() ?: ""
                Log.d("Test", "response body:" + body)
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false)) {
                    val error = PrimoParsers.errorParser(body)
                    sub.onError(/*if (error.code > -1) error else */NetworkException(body, code))
                }
                else {
                    val count = PrimoParsers.countParser(PrimoParsers.dataParser(body))
                    Log.d("Test", "PARSE count:" + count)
                    when (count) {
                        PARSE_ORDER        -> sub.onNext(PrimoParsers.orderParser(PrimoParsers.dataParser(body)))
                        PARSE_ORDER_REJECT -> sub.onError(NetworkException(body, OWN_ORDER_REJECT_CODE))
                        PARSE_REJECT       -> sub.onError(NetworkException(body, OWN_REJECT_CODE))
                    }
                }
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Boolean>() {

                    override fun onCompleted() {
                        result.onComplete()
                    }

                    override fun onError(e: Throwable) {
                        if (e is NetworkException) {
                            result.onError(e.report, e.code)
                        } else if (e is UnknownHostException) {
                            result.onError(e.message.orEmpty(), CONNECTION_ERROR)
                        }
                        result.onComplete()
                    }

                    override fun onNext(response: Boolean) {
                        result.onResult(response)
                    }
                })
    }

}

class ReprocessAFailedOrderImpl(result: ApiResult<String>) : ReprocessAFailedOrder {

    private val result = result

    override fun reprocessAFailedOrder(orderId: String, creditcardId: String, token: String) {

        result.onStart()


    }
}

class GetOrderHistoryImpl(result: ApiResult<ArrayList<CartItem>>) : GetOrderHistory {

    val result = result

    override fun getOrderHistory(page: Int, token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<ArrayList<CartItem>> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(OrderRequests.getOrderHistory(page, token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.orderHistoryParser(PrimoParsers.dataParser(body)))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ArrayList<CartItem>>() {

                    override fun onCompleted() {
                        result.onComplete()
                    }

                    override fun onError(e: Throwable) {
                        if (e is NetworkException) {
                            result.onError(e.report, e.code)
                        } else if (e is UnknownHostException) {
                            result.onError(e.message.orEmpty(), CONNECTION_ERROR)
                        }
                        result.onComplete()
                    }

                    override fun onNext(response: ArrayList<CartItem>) {
                        result.onResult(response)
                    }
                })
    }

}

class CheckShippingCardImpl(result: ApiResult<Array<Boolean?>>) : CheckShippingCard {

    val result = result

    override fun checkShippingCard(token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<Array<Boolean?>> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(OrderRequests.checkShippingCard(token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.checkShippingCardParser(PrimoParsers.dataParser(body)))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Array<Boolean?>>() {

                    override fun onCompleted() {
                        result.onComplete()
                    }

                    override fun onError(e: Throwable) {
                        if (e is NetworkException) {
                            result.onError(e.report, e.code)
                        } else if (e is UnknownHostException) {
                            result.onError(e.message.orEmpty(), CONNECTION_ERROR)
                        }
                        result.onComplete()
                    }

                    override fun onNext(response: Array<Boolean?>) {
                        result.onResult(response)
                    }
                })
    }
}