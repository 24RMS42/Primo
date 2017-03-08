package com.primo.network.requests

import android.util.Log
import com.primo.main.MainClass
import com.primo.utils.getAndroidId
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject


object UserInfoRequest {

    val country_code = MainClass.getSavedCountry()

    fun retrieveUserProfile(token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_BASIC)
                        .addPathSegment(APIPrimo.API_PROFILE)
                        .build())
                .get()
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun updateUserProfile(phone: String, firstname: String, lastname: String,
                          address: String, city: String, state: String, country: String,
                          postcode: String, delivery_preference: String, is_mail_campaign: String, token: String): Request {

        val bodyObject = JSONObject()
        bodyObject
                .put("phone", phone).put("firstname", firstname).put("lastname", lastname)
                .put("address", address).put("city", city).put("state", state).put("country", country)
                .put("postcode", postcode)
                .put("delivery_preference", delivery_preference).put("is_mail_campaign", is_mail_campaign)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())
        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_BASIC)
                        .addPathSegment(APIPrimo.API_PROFILE)
                        .build())
                .put(body)
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun getListShippingAddress(token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_SHIPPING)
                        .build())
                .get()
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun addShippingAddress(alias: String, firstname: String, lastname: String, phone: String, postcode: String,
          address: String, city: String, state: String, country: Int, is_default: Int, token: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("alias", alias).put("firstname", firstname).put("lastname", lastname)
                .put("phone", phone).put("postcode", postcode).put("address", address).put("city", city)
                .put("state", state).put("country", country).put("is_default", is_default)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())
        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_SHIPPING)
                        .build())
                .post(body)
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun retrieveShippingAddress(shipping_id: String, token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_SHIPPING)
                        .addPathSegment(shipping_id)
                        .build())
                .get()
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun updateShippingAddress(shipping_id: String, alias: String, firstname: String, lastname: String, phone: String, postcode: String,
                              address: String, city: String, state: String, country: Int, is_default: Int, token: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("alias", alias).put("firstname", firstname).put("lastname", lastname)
                .put("phone", phone).put("postcode", postcode).put("address", address).put("city", city)
                .put("state", state).put("country", country).put("is_default", is_default)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_SHIPPING)
                        .addPathSegment(shipping_id)
                        .build())
                .put(body)
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun updateDefaultShippingAddress(shipping_id: String, token: String): Request {

        val bodyObject = JSONObject()
        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_SHIPPING)
                        .addPathSegment(APIPrimo.API_DEFAULT)
                        .addPathSegment(shipping_id)
                        .build())
                .put(body)
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun deleteShippingAddress(shipping_id: String, token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_SHIPPING)
                        .addPathSegment(shipping_id)
                        .build())
                .delete()
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun getListCreditCard(token: String): Request {

        Log.d("Test", " == user country code:" + country_code)

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_CC)
                        .build())
                .get()
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun addCreditCard(cardnumber: String, cardname: String, cardyear: String, cardmonth: String,
                      cardcvc: String, is_default: Int, token: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("cardnumber", cardnumber).put("cardname", cardname).put("cardyear", cardyear)
                .put("cardmonth", cardmonth).put("cardcvc", cardcvc).put("is_default", is_default)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_CC)
                        .build())
                .post(body)
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun retrieveCreditCard(creditcard_id: String, token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_CC)
                        .addPathSegment(creditcard_id)
                        .build())
                .get()
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun updateCreditCard(creditcard_id: String, cardname: String, cardyear: String, cardmonth: String,
                         token: String, is_default: Int): Request {

        val bodyObject = JSONObject()
        bodyObject.put("cardname", cardname).put("cardyear", cardyear)
                .put("cardmonth", cardmonth).put("is_default", is_default)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_CC)
                        .addPathSegment(creditcard_id)
                        .build())
                .put(body)
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun updateDefaultCreditCard(creditcard_id: String, token: String): Request {

        val bodyObject = JSONObject()

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_CC)
                        .addPathSegment(APIPrimo.API_DEFAULT)
                        .addPathSegment(creditcard_id)
                        .build())
                .put(body)
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun deleteCreditCard(creditcard_id: String, token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_CC)
                        .addPathSegment(creditcard_id)
                        .build())
                .delete()
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun setCountry(country: Int, token: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("country", country)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())
        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_COUNTRY)
                        .build())
                .put(body)
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun updateUserLanguage(language: String, token: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("language", language)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())
        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_LANGUAGE)
                        .build())
                .put(body)
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun getPublicCount(token: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("unique_id", getAndroidId())
                .put(APIPrimo.CLIENT_ID, APIPrimo.CLIENT_ID_VALUE)
                .put(APIPrimo.CLIENT_SECRET, APIPrimo.CLIENT_SECRET_VALUE)
                .put("operations", "temp_cart")

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_TEMP)
                        .addPathSegment(APIPrimo.API_COUNT)
                        .build())
                .post(body)
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

    fun getLiveCount(token: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("operations", "cart")

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_LIVE)
                        .addPathSegment(APIPrimo.API_COUNT)
                        .build())
                .post(body)
                .header(APIPrimo.AUTHORIZATION, token)
                .header(APIPrimo.COUNTRY_ID, country_code)
                .build()
    }

}