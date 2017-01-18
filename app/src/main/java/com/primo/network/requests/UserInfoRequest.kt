package com.primo.network.requests

import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject


object UserInfoRequest {

    fun retrieveUserProfile(token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_PROFILE)
                        .build())
                .get()
                .header(APIPrimo.AUTHORIZATION, token)
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
                        .addPathSegment(APIPrimo.API_PROFILE)
                        .build())
                .put(body)
                .header(APIPrimo.AUTHORIZATION, token)
                .build()
    }

    fun getListShippingAddress(): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_SHIPPING)
                        .build()).get().build()
        //TODO ADD BEARER TOKEN
    }

    fun addShippingAddress(alias: String, firstname: String, lastname: String, phone: String, postcode: String,
          address: String, city: String, state: String, country: String, is_default: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("alias", alias).put("firstname", firstname).put("lastname", lastname)
                .put("phone", phone).put("postcode", postcode).put("address", address).put("city", city)
                .put("state", state).put("country", country).put("is_default", is_default)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())
        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_SHIPPING)
                        .build()).post(body).build()
        //TODO ADD BEARER TOKEN
    }

    fun retrieveShippingAddress(shipping_id: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_SHIPPING)
                        .addPathSegment(shipping_id)
                        .build()).get().build()
        //TODO ADD BEARER TOKEN
    }

    fun updateShippingAddress(shipping_id: String, alias: String, firstname: String, lastname: String, phone: String, postcode: String,
                              address: String, city: String, state: String, country: String, is_default: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("alias", alias).put("firstname", firstname).put("lastname", lastname)
                .put("phone", phone).put("postcode", postcode).put("address", address).put("city", city)
                .put("state", state).put("country", country).put("is_default", is_default)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_SHIPPING)
                        .addPathSegment(shipping_id)
                        .build()).put(body).build()
        //TODO ADD BEARER TOKEN
    }

    fun deleteShippingAddress(shipping_id: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_SHIPPING)
                        .addPathSegment(shipping_id)
                        .build()).delete().build()
        //TODO ADD BEARER TOKEN
    }

    fun getListCreditCard(): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_CC)
                        .build()).get().build()
        //TODO ADD BEARER TOKEN
    }

    fun addCreditCard(cardnumber: String, cardname: String, cardyear: String, cardmonth: String,
                      cardcvc: String, is_default: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("cardnumber", cardnumber).put("cardname", cardname).put("cardyear", cardyear)
                .put("cardmonth", cardmonth).put("cardcvc", cardcvc).put("is_default", is_default)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_CC)
                        .build()).post(body).build()
        //TODO ADD BEARER TOKEN
    }

    fun retrieveCreditCard(creditcard_id: String, token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_CC)
                        .addPathSegment(creditcard_id)
                        .build())
                .get().header(APIPrimo.AUTHORIZATION, token)
                .build()
    }

    fun updateCreditCard(creditcard_id: String, cardname: String, cardyear: String, cardmonth: String,
                         token: String, is_default: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("card_id", creditcard_id).put("cardname", cardname).put("cardyear", cardyear)
                .put("cardmonth", cardmonth).put("is_default", is_default)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_CC)
                        .addPathSegment(creditcard_id)
                        .build()).put(body).header(APIPrimo.AUTHORIZATION, token).build()
    }

    fun deleteCreditCard(creditcard_id: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_CC)
                        .addPathSegment(creditcard_id)
                        .build()).delete().build()
        //TODO ADD BEARER TOKEN
    }

}