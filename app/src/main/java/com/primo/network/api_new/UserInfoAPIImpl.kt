package com.primo.network.api_new

import com.primo.network.new_models.CreditCard
import com.primo.network.new_models.UserProfile
import com.primo.network.parsers.PrimoParsers
import com.primo.network.requests.APIPrimo
import com.primo.network.requests.UserInfoRequest
import com.primo.utils.NetworkException
import com.primo.utils.consts.CONNECTION_ERROR
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.net.UnknownHostException

class RetrieveUserProfileImpl(result: ApiResult<UserProfile?>): RetrieveUserProfile {

    private val result = result

    override fun retrieveUserProfile(token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<UserProfile?> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserInfoRequest.retrieveUserProfile(token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.userProfileParser(PrimoParsers.dataParser(body)))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<UserProfile?>() {

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

                    override fun onNext(response: UserProfile?) {
                        result.onResult(response)
                    }
                })
    }
}

class UpdateUserProfileImpl(result: ApiResult<String?>): UpdateUserProfile {

    private val result = result

    override fun updateUserProfile(phone: String, firstname: String, lastname: String, address: String,
                                   city: String, state: String, country: String, postcode: String,
                                   delivery_preference: String, is_mail_campaign: String, token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<String?> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserInfoRequest.updateUserProfile(phone,
                        firstname, lastname, address, city, state, country, postcode, delivery_preference,
                        is_mail_campaign, token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(body)
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<String?>() {

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

                    override fun onNext(response: String?) {
                        result.onResult(response)
                    }
                })
    }

}

class RetrieveCreditCardImpl(result: ApiResult<CreditCard?>): RetrieveCreditCard {

    private val result = result

    override fun retrieveCreditCards(creditcard_id: String, token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<CreditCard?> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserInfoRequest.retrieveCreditCard(creditcard_id, token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.creditCardParser(PrimoParsers.dataParser(body)))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<CreditCard?>() {

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

                    override fun onNext(response: CreditCard?) {
                        result.onResult(response)
                    }
                })
    }
}

class UpdateCreditCardImpl(result: ApiResult<CreditCard?>): UpdateCreditCard {

    private val result = result

    override fun updateCreditCard(creditcard_id: String, cardname: String, cardyear: String, cardmonth: String,
                                  token: String, is_default: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<CreditCard?> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserInfoRequest
                        .updateCreditCard(creditcard_id, cardname, cardyear, cardmonth, token, is_default))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.creditCardParser(PrimoParsers.dataParser(body)))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<CreditCard?>() {

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

                    override fun onNext(response: CreditCard?) {
                        result.onResult(response)
                    }
                })
    }

}
