package com.primo.network.requests

import com.primo.utils.getDeviceLanguage
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

object UserOperationRequests {

    fun signUp(email: String, password: String, repassword: String,
               phone: String, firstname: String, lastname: String,
               address: String, city: String, state: String,
               country: String, postcode: String, cardnumber: String,
               cardname: String, cardyear: String, cardmonth: String,
               cardcvc: String, delivery_preference: String, is_mail_campaign: String,
               unique_id: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("email", email).put("password", password).put("repassword", repassword)
                .put("phone", phone).put("firstname", firstname).put("lastname", lastname)
                .put("address", address).put("city", city).put("state", state).put("country", country)
                .put("postcode", postcode).put("cardnumber", cardnumber).put("cardname", cardname)
                .put("cardyear", cardyear).put("cardmonth", cardmonth).put("cardcvc", cardcvc)
                .put("delivery_preference", delivery_preference).put("is_mail_campaign", is_mail_campaign)
                .put("language", getDeviceLanguage()).put("unique_id", unique_id)
                .put(APIPrimo.CLIENT_ID, APIPrimo.CLIENT_ID_VALUE)
                .put(APIPrimo.CLIENT_SECRET, APIPrimo.CLIENT_SECRET_VALUE)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_SIGNUP)
                        .build()).post(body).build()
    }

    fun resendConfirm(email: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("email", email).put(APIPrimo.CLIENT_ID, APIPrimo.CLIENT_ID_VALUE).put(APIPrimo.CLIENT_SECRET, APIPrimo.CLIENT_SECRET_VALUE)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_RESEND)
                        .addPathSegment(APIPrimo.API_SIGNUP)
                        .addPathSegment(APIPrimo.API_CONFIRM)
                        .build()).post(body).build()
    }

    fun forgotPassword(email: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("email", email).put(APIPrimo.CLIENT_ID, APIPrimo.CLIENT_ID_VALUE).put(APIPrimo.CLIENT_SECRET, APIPrimo.CLIENT_SECRET_VALUE)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_FORGOT)
                        .addPathSegment(APIPrimo.API_PSW)
                        .build()).post(body).build()
    }
}

