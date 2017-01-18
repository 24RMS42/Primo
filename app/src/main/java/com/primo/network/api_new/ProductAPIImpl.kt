package com.primo.network.api_new

import com.primo.network.new_models.CartItem
import com.primo.network.new_models.Product
import com.primo.network.new_models.Stock
import com.primo.network.parsers.PrimoParsers
import com.primo.network.requests.APIPrimo
import com.primo.network.requests.ProductRequests
import com.primo.utils.NetworkException
import com.primo.utils.consts.CONNECTION_ERROR
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.net.UnknownHostException
import java.util.*


class SearchProductByQrImpl(result: ApiResult<Product?>): SearchProductByQr {

    private val result = result

    override fun searchProductByQr(qrcode: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<Product> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(ProductRequests.searchProductByQr(qrcode))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.productParser(PrimoParsers.dataParser(body)))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Product?>() {

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

                    override fun onNext(response: Product?) {
                        result.onResult(response)
                    }
                })
    }
}

class RetrieveProductImpl(result: ApiResult<String>): RetrieveProduct {

    private val result = result

    override fun retrieveProduct(productId: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<String> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(ProductRequests.retrieveProduct(productId))?.execute()
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

class SearchProductByKeywordImpl(result: ApiResult<String>): SearchProductByKeyword {

    private val result = result

    override fun searchProductByKeyword(keyword: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<String> { sub ->

            try {

                val response = APIPrimo.getClient().newCall(ProductRequests.searchProductByKeyword(keyword))?.execute()
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

class RetrieveProductStockImpl(result: ApiResult<Pair<CartItem, ArrayList<Stock>>>): RetrieveProductStock {

    private val result = result

    override fun retrieveProductStock(product: CartItem) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<Pair<CartItem, ArrayList<Stock>>> { sub ->

            try {

                val response = APIPrimo.getClient().newCall(ProductRequests.retrieveProductStock(product.productId))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false)) sub.onError(NetworkException(body, code))
                sub.onNext(Pair(product, PrimoParsers.stockListParser(PrimoParsers.dataParser(body))))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Pair<CartItem, ArrayList<Stock>>>() {

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

                    override fun onNext(response: Pair<CartItem, ArrayList<Stock>>) {
                        result.onResult(response)
                    }
                })
    }
}