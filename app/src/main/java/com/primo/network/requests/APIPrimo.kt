package com.primo.network.requests

import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient

object APIPrimo {

    //DEV
//    const val API_SCHEME = "http"
//    const val API_HOST =  "staging.doughnut.primo.im" // "staging.primo.im" //

    //RELEASE
    const val API_SCHEME = "https"
    const val API_HOST = "primo.im"

    const val API_PATH = "api/v1"

    const val CLIENT_ID = "client_id"
    const val CLIENT_SECRET = "client_secret"
    const val AUTHORIZATION = "Authorization"

    //DEV
//    const val CLIENT_ID_VALUE = "8pdQeisGsr4lDXRncpSYXRA7"
//    const val CLIENT_SECRET_VALUE = "90u3sw1ZXRSwiIPSnYYgYGvufawBHpBK"

    //RELEASE
    const val CLIENT_ID_VALUE = "PIVeoWaMGuIyr87A5JjVSc9z"
    const val CLIENT_SECRET_VALUE = "56I3bwQk1YutAYLUQELibcHOlL9vELMu"

    const val API_USER = "user"
    const val API_PRODUCT = "product"
    const val API_TCART = "tcart"
    const val API_CART = "cart"
    const val API_ORDER = "order"


    const val API_SIGNUP = "signup"
    const val API_CC = "cc"
    const val API_ACCESS = "access"
    const val API_PROFILE = "profile"
    const val API_SHIPPING = "shipping"
    const val API_CHANGE = "change"
    const val API_PASSWORD = "password"
    const val API_TOKEN = "token"
    const val API_RESEND = "resend"
    const val API_FORGOT = "forgot"
    const val API_PSW = "psw"
    const val API_CONFIRM = "confirm"
    const val API_SEARCH = "search"
    const val API_QR = "qr"
    const val API_KEYWORD = "keyword"
    const val API_REPROCESS = "reprocess"
    const val API_WHISHLIST = "wishlist"
    const val API_HISTORY = "history"
    const val API_STOCKS = "stocks"

    val JSON = MediaType.parse("application/json; charset=utf-8")

    fun getClient() = OkHttpClient.Builder().build()

    fun getDefaultHttpBuilder(): HttpUrl.Builder {
        return HttpUrl.Builder()
                .scheme(API_SCHEME)
                .host(API_HOST)
                .addPathSegment(API_PATH)
    }

    /*fun getBasicAuthHeader() : String {
        val credentials = USERNAME + ":" + PASSWORD
        return "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    }*/


}