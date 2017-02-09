/**
 * Changes:
 *
 * - change location value string to float
 * - handling null data
 *
 * 2015 © Primo . All rights reserved.
 */


package com.primo.network.requests

import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject


object OrderRequests {

    fun placeAnOrder(creditcardId: String, shippingId: String, cartId: String, token: String,
                     lat: Float?, lng: Float?): Request {

        val bodyObject = JSONObject()
        bodyObject.put("creditcard_id", creditcardId)
                .put("shipping_id", shippingId)
                .put("cart_id", cartId)

        bodyObject.put("lat", lat)
                .put("lng", lng)

//        if (!lat.isEmpty() && !lng.isEmpty()) {
//            bodyObject.put("lat", lat)
//                    .put("lng", lng)
//        } else {
//            if (lat.isEmpty()) {
//                lat = null!!
//            }
//            if (lng.isEmpty()) {
//                lng = null!!
//            }
//        }

        if (lat == null && lng == null)
        {
            bodyObject.put("lat", JSONObject.NULL)
                    .put("lng", JSONObject.NULL)
        }

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_ORDER)
                        .build()).post(body).header(APIPrimo.AUTHORIZATION, token).build()
    }

    fun reprocessAFailedOrder(orderId: String, creditcardId: String, token: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("order_id", orderId)
                .put("creditcard_id", creditcardId)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_ORDER)
                        .addPathSegment(APIPrimo.API_REPROCESS)
                        .build()).post(body).header(APIPrimo.AUTHORIZATION, token).build()
    }

    fun getOrderHistory(page: Int, token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_ORDER)
                        .addPathSegment(APIPrimo.API_HISTORY)
                        .addQueryParameter("page", page.toString())
                        .build()).get().header(APIPrimo.AUTHORIZATION, token).build()
    }

    fun checkShippingCard(token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_USER)
                        .addPathSegment(APIPrimo.API_CHECK)
                        .addPathSegment(APIPrimo.API_SHIPPING)
                        .addPathSegment(APIPrimo.API_PAYMENT)
                        .build())
                .get()
                .header(APIPrimo.AUTHORIZATION, token)
                .build()
    }
}