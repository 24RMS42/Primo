package com.primo.network.requests

import com.primo.main.MainClass
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

object UserAuthenticationRequests {

    val country_code = MainClass.getSavedCountry()

    fun signIn(email: String, password: String, unique_id: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("email", email).put("password", password)
                .put("unique_id", unique_id).put(APIPrimo.CLIENT_ID, APIPrimo.CLIENT_ID_VALUE)
                .put(APIPrimo.CLIENT_SECRET, APIPrimo.CLIENT_SECRET_VALUE)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_ACCESS)
                        .addPathSegment(APIPrimo.API_TOKEN)
                        .build()).post(body).header(APIPrimo.COUNTRY_ID, country_code).build()
    }

    fun changePassword(current_password: String, new_password: String, renew_password: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("current_password", current_password).put("new_password", new_password)
                .put("renew_password", renew_password)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())
        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_CHANGE)
                        .addPathSegment(APIPrimo.API_PASSWORD)
                        .build()).put(body).header(APIPrimo.COUNTRY_ID, country_code).build()
        //TODO ADD BEARER HEADER
    }
}

