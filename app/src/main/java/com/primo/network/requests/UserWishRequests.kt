package com.primo.network.requests

import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject


object UserWishRequests {

    fun getListOfWishes(token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_WHISHLIST)
                        .build()).get().header(APIPrimo.AUTHORIZATION, token).build()
    }

    fun addWishlistItem(productId: String, stockId: String, quantity: String, token: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("product_id", productId).put("stock_id", stockId).put("quantity", quantity)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_WHISHLIST)
                        .build()).post(body).header(APIPrimo.AUTHORIZATION, token).build()
    }

    fun retrieveWishlistItem(wishlistId: String, token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_WHISHLIST)
                        .addPathSegment(wishlistId)
                        .build()).get().header(APIPrimo.AUTHORIZATION, token).build()
    }

    fun deleteWishlistItem(wishlistId: String, token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_WHISHLIST)
                        .addPathSegment(wishlistId)
                        .build()).delete().header(APIPrimo.AUTHORIZATION, token).build()
    }
}