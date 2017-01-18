package com.primo.network.api_new

import com.primo.network.new_models.Cart
import com.primo.network.parsers.PrimoParsers
import com.primo.network.requests.APIPrimo
import com.primo.network.requests.CartRequests
import com.primo.utils.NetworkException
import com.primo.utils.consts.CONNECTION_ERROR
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.net.UnknownHostException


class AddItemToTempCartImpl(result: ApiResult<Cart?>) : AddItemToTempCart {

    private val result = result

    override fun addItemToTempCart(stock_id: String, quantity: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<Cart> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(CartRequests.addItemToTempCart(stock_id, quantity))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.cartParser(PrimoParsers.dataParser(body)))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Cart>() {

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

                    override fun onNext(response: Cart) {
                        result.onResult(response)
                    }
                })
    }
}

class RemoveItemFromTempCartImpl(result: ApiResult<String>) : RemoveItemFromTempCart {

    private val result = result

    override fun removeItemFromTempCart(cart_item_id: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<String> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(CartRequests.removeItemFromTempCart(cart_item_id))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false)) sub.onError(NetworkException(body, code))
                sub.onNext(body)
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<String>() {

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

                    override fun onNext(response: String) {
                        result.onResult(response)
                    }
                })
    }
}

class RetrieveTempCartDetailImpl(result: ApiResult<Cart?>) : RetrieveTempCartDetail {

    private val result = result

    override fun retrieveTempCartDetail() {

        result.onStart()

        Observable.create(Observable.OnSubscribe<Cart?> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(CartRequests.retrieveTempCartDetail())?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.cartParser(PrimoParsers.dataParser(body)))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Cart?>() {

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

                    override fun onNext(response: Cart?) {
                        result.onResult(response)
                    }
                })
    }
}

class UpdateTempCartItemImpl(result: ApiResult<Cart?>) : UpdateTempCartItem {

    private val result = result

    override fun updateTempCartItem(stock_id: String, cart_item_id: String, quantity: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<Cart?> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(CartRequests.updateTempCartItem(stock_id, cart_item_id, quantity))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.cartParser(PrimoParsers.cartsParser(PrimoParsers.dataParser(body))))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Cart?>() {

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

                    override fun onNext(response: Cart?) {
                        result.onResult(response)
                    }
                })
    }
}

class AddItemToCartImpl(result: ApiResult<Cart?>) : AddItemToCart {

    private val result = result

    override fun addItemToCart(stock_id: String, quantity: String, token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<Cart?> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(CartRequests.addItemToCart(stock_id, quantity, token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.cartParser(PrimoParsers.dataParser(body)))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Cart?>() {

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

                    override fun onNext(response: Cart?) {
                        result.onResult(response)
                    }
                })
    }

}

class RetrieveCartDetailImpl(result: ApiResult<Cart?>) : RetrieveCartDetail {

    private val result = result

    override fun retrieveCartDetail(cart_id: String, token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<Cart?> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(CartRequests.retrieveCartDetail(cart_id, token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1

                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.cartParser(PrimoParsers.dataParser(body)))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Cart?>() {

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

                    override fun onNext(response: Cart?) {
                        result.onResult(response)
                    }
                })
    }
}

class RemoveItemFromCartImpl(result: ApiResult<String>) : RemoveItemFromCart {

    private val result = result

    override fun removeItemFromCart(stock_id: String, token: String, cart_id: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<String> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(CartRequests.removeItemFromCart(stock_id, token, cart_id))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false)) sub.onError(NetworkException(body, code))
                sub.onNext(body)
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<String>() {

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

                    override fun onNext(response: String) {
                        result.onResult(response)
                    }
                })
    }

}

class UpdateCartItemImpl(result: ApiResult<Cart?>) : UpdateCartItem {

    private val result = result

    override fun updateCartItem(stock_id: String, cart_item_id: String, quantity: String, token: String, cart_id: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<Cart?> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(CartRequests.updateCartItem(stock_id, cart_item_id, quantity, token, cart_id))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.cartParser(PrimoParsers.cartsParser(PrimoParsers.dataParser(body))))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Cart?>() {

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

                    override fun onNext(response: Cart?) {
                        result.onResult(response)
                    }
                })
    }
}