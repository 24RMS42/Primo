package com.primo.network.api_new

import com.primo.network.new_models.Auth
import com.primo.network.parsers.PrimoParsers
import com.primo.network.requests.APIPrimo
import com.primo.network.requests.UserOperationRequests
import com.primo.utils.NetworkException
import com.primo.utils.consts.CONNECTION_ERROR
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.net.UnknownHostException

class SignUpImpl(result: ApiResult<Auth?>): SignUp {

    private val result = result

    override fun signUp(email: String, password: String, repassword: String, phone: String,
                        firstname: String, lastname: String, address: String, city: String,
                        state: String, country: String, postcode: String, cardnumber: String,
                        cardname: String, cardyear: String, cardmonth: String, cardcvc: String,
                        delivery_preference: String, is_mail_campaign: String, unique_id: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<Auth?> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserOperationRequests.signUp(email,
                        password, repassword, phone, firstname, lastname, address, city, state,
                        country, postcode, cardnumber, cardname, cardyear, cardmonth, cardcvc,
                        delivery_preference, is_mail_campaign, unique_id))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.authParser(PrimoParsers.dataParser(body)))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Auth?>() {

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

                    override fun onNext(response: Auth?) {
                        result.onResult(response)
                    }
                })
    }

}

class ResendConfirmImpl(result: ApiResult<String?>): ResendConfirm {

    private val result = result

    override fun resendConfirm(email: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<String> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserOperationRequests.resendConfirm(email))?.execute()
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

class ForgotPasswordImpl(result: ApiResult<String>): ForgotPassword {

    private val result = result

    override fun forgotPassword(email: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<String> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserOperationRequests.forgotPassword(email))?.execute()
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
