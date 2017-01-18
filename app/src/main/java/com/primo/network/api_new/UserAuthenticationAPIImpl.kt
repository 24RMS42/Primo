package com.primo.network.api_new

import com.primo.network.new_models.Auth
import com.primo.network.parsers.PrimoParsers
import com.primo.network.requests.APIPrimo
import com.primo.network.requests.UserAuthenticationRequests
import com.primo.utils.NetworkException
import com.primo.utils.consts.CONNECTION_ERROR
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.net.UnknownHostException

class SignInImpl(result: ApiResult<Auth?>): SignIn {

    private val result = result

    override fun signIn(email: String, password: String, unique_id: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<Auth?> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserAuthenticationRequests.signIn(email, password, unique_id))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false)) {
                    val error = PrimoParsers.errorParser(body)
                    sub.onError(if (error.code > -1) error else NetworkException(body, code))
                } else
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

class ChangePasswordImpl(result: ApiResult<String?>): ChangePassword {

    private val result = result

    override fun changePassword(current_password: String, new_password: String, renew_password: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<String> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserAuthenticationRequests.changePassword(current_password, new_password, renew_password))?.execute()
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
