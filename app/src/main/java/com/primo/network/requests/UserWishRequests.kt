package com.primo.network.requests

import com.primo.main.MainClass
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject


object UserWishRequests {

    val country_code = MainClass.getSavedCountry()

    fun getListOfWishes(token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_WHISHLIST)
                        .build()).get().header(APIPrimo.AUTHORIZATION, token).header(APIPrimo.COUNTRY_ID, country_code).build()
    }

    fun addWishlistItem(productId: String, stockId: String, quantity: String, token: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("product_id", productId).put("stock_id", stockId).put("quantity", quantity)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_WHISHLIST)
                        .build()).post(body).header(APIPrimo.AUTHORIZATION, token).header(APIPrimo.COUNTRY_ID, country_code).build()
    }

    fun retrieveWishlistItem(wishlistId: String, token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_WHISHLIST)
                        .addPathSegment(wishlistId)
                        .build()).get().header(APIPrimo.AUTHORIZATION, token).header(APIPrimo.COUNTRY_ID, country_code).build()
    }

    fun deleteWishlistItem(wishlistId: String, token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_WHISHLIST)
                        .addPathSegment(wishlistId)
                        .build()).delete().header(APIPrimo.AUTHORIZATION, token).header(APIPrimo.COUNTRY_ID, country_code).build()
    }
}