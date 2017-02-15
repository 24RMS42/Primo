package com.primo.network.api_new

import com.primo.network.new_models.WishItem
import com.primo.network.parsers.PrimoParsers
import com.primo.network.requests.APIPrimo
import com.primo.network.requests.UserWishRequests
import com.primo.utils.NetworkException
import com.primo.utils.consts.CONNECTION_ERROR
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.net.UnknownHostException


class GetListOfWishesImpl(result: ApiResult<MutableList<WishItem>>): GetListOfWishes {

    val result = result

    override fun getListOfWishes(token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<MutableList<WishItem>> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserWishRequests.getListOfWishes(token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false)) {
                    val error = PrimoParsers.errorParser(body)
                    sub.onError(/*if (error.code > -1) error else */NetworkException(body, code))
                } else
                    sub.onNext(PrimoParsers.wishesParser(PrimoParsers.dataParser(PrimoParsers.dataParser(body))))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<MutableList<WishItem>>() {

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

                    override fun onNext(response: MutableList<WishItem>) {
                        result.onResult(response)
                    }
                })

    }
}

class AddWishlistItemImpl(result: ApiResult<String>): AddWishlistItem {

    private val result = result

    override fun addWishlistItem(productId: String, stockId: String, quantity: String, token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<String?> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserWishRequests.addWishlistItem(productId,
                        stockId, quantity, token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false)) {
                    val error = PrimoParsers.errorParser(body)
                    sub.onError(/*if (error.code > -1) error else */NetworkException(body, code))
                } else
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

class RetrieveWishlistItemImpl(): RetrieveWishlistItem {

    override fun retrieveWishlistItem(wishlistId: String, token: String) {

    }
}

class DeleteWishlistItemImpl(result: ApiResult<String>): DeleteWishlistItem {

    val result = result

    override fun deleteWishlistItem(wishlistId: String, token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<String?> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserWishRequests.deleteWishlistItem(wishlistId, token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false)) {
                    val error = PrimoParsers.errorParser(body)
                    sub.onError(/*if (error.code > -1) error else */NetworkException(body, code))
                } else
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