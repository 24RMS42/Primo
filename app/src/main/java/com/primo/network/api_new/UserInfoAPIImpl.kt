package com.primo.network.api_new

import com.primo.network.new_models.CreditCard
import com.primo.network.new_models.CreditCardData
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
import java.util.*

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

class RetrieveListCreditCardImpl(result: ApiResult<ArrayList<CreditCardData>>): GetListCreditCard {

    private val result = result

    override fun getListCreditCard(token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<ArrayList<CreditCardData>> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserInfoRequest.getListCreditCard(token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.listCreditCardParser(body))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ArrayList<CreditCardData>>() {

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

                    override fun onNext(response: ArrayList<CreditCardData>) {
                        result.onResult(response)
                    }
                })
    }
}

class AddCreditCardImpl(result: ApiResult<String?>): AddCreditCard {

    private val result = result

    override fun addCreditCard(cardnumber: String, cardname: String, cardyear: String, cardmonth: String,
                               cardcvc: String, is_default: Int, token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<String?> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserInfoRequest
                        .addCreditCard(cardnumber, cardname, cardyear, cardmonth, cardcvc, is_default, token))?.execute()
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

class UpdateCreditCardImpl(result: ApiResult<CreditCard?>): UpdateCreditCard {

    private val result = result

    override fun updateCreditCard(creditcard_id: String, cardname: String, cardyear: String, cardmonth: String,
                                  token: String, is_default: Int) {

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

class UpdateDefaultCreditCardImpl(result: ApiResult<ArrayList<CreditCardData>>): UpdateDefaultCreditCard {

    private val result = result

    override fun updateDefaultCreditCard(creditcard_id: String, token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<ArrayList<CreditCardData>> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserInfoRequest
                        .updateDefaultCreditCard(creditcard_id, token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.listCreditCardParser(body))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ArrayList<CreditCardData>>() {

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

                    override fun onNext(response: ArrayList<CreditCardData>) {
                        result.onResult(response)
                    }
                })
    }

}

class DeleteCreditCardImpl(result: ApiResult<ArrayList<CreditCardData>>): DeleteCreditCard {

    private val result = result

    override fun deleteCreditCard(creditcard_id: String, token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<ArrayList<CreditCardData>> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserInfoRequest
                        .deleteCreditCard(creditcard_id, token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.listCreditCardParser(body))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ArrayList<CreditCardData>>() {

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

                    override fun onNext(response: ArrayList<CreditCardData>) {
                        result.onResult(response)
                    }
                })
    }

}

class RetrieveListShippingAddressImpl(result: ApiResult<ArrayList<UserProfile>>): GetListShippingAddress {

    private val result = result

    override fun getListShippingAddress(token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<ArrayList<UserProfile>> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserInfoRequest.getListShippingAddress(token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.listShippingAddressParser(body))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ArrayList<UserProfile>>() {

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

                    override fun onNext(response: ArrayList<UserProfile>) {
                        result.onResult(response)
                    }
                })
    }
}

class AddShippingAddressImpl(result: ApiResult<String?>): AddShippingAddress {

    private val result = result

    override fun addShippingAddress(phone: String, firstname: String, lastname: String, address: String,
                                    city: String, state: String, country: Int, postcode: String,
                                    is_default: Int, token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<String?> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserInfoRequest.addShippingAddress("alias",
                        firstname, lastname, phone, postcode, address, city, state, country, is_default, token))?.execute()
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

class UpdateShippingAddressImpl(result: ApiResult<ArrayList<UserProfile>>): UpdateShippingAddress {

    private val result = result

    override fun updateShippingAddress(shipping_id: String, phone: String, firstname: String, lastname: String, address: String,
                                    city: String, state: String, country: Int, postcode: String,
                                    is_default: Int, token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<ArrayList<UserProfile>> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserInfoRequest.updateShippingAddress(shipping_id, "alias",
                        firstname, lastname, phone, postcode, address, city, state, country, is_default, token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.listShippingAddressParser(body))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ArrayList<UserProfile>>() {

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

                    override fun onNext(response: ArrayList<UserProfile>) {
                        result.onResult(response)
                    }
                })
    }

}

class UpdateDefaultShippingAddressImpl(result: ApiResult<ArrayList<UserProfile>>): UpdateDefaultShippingAddress {

    private val result = result

    override fun updateDefaultShippingAddress(shipping_id: String, token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<ArrayList<UserProfile>> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserInfoRequest.updateDefaultShippingAddress(shipping_id, token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.listShippingAddressParser(body))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ArrayList<UserProfile>>() {

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

                    override fun onNext(response: ArrayList<UserProfile>) {
                        result.onResult(response)
                    }
                })
    }

}

class DeleteShippingAddressImpl(result: ApiResult<ArrayList<UserProfile>>): DeleteShippingAddress {

    private val result = result

    override fun deleteShippingAddress(shipping_id: String, token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<ArrayList<UserProfile>> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserInfoRequest.deleteShippingAddress(shipping_id, token))?.execute()
                val body = response?.body()?.string() ?: ""
                val code = response?.code() ?: -1
                if (!(response?.isSuccessful ?: false))
                    sub.onError(NetworkException(body, code))
                else
                    sub.onNext(PrimoParsers.listShippingAddressParser(body))
                sub.onCompleted()
            } catch (e: IOException) {
                sub.onError(e)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<ArrayList<UserProfile>>() {

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

                    override fun onNext(response: ArrayList<UserProfile>) {
                        result.onResult(response)
                    }
                })
    }

}

class SetCountryImpl(result: ApiResult<String?>): SetCountry {

    private val result = result

    override fun setCountry(country: Int, token: String) {

        result.onStart()

        Observable.create(Observable.OnSubscribe<String?> { sub ->
            try {
                val response = APIPrimo.getClient().newCall(UserInfoRequest.setCountry(country, token))?.execute()
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
