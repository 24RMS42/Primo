package com.primo.network.requests

import com.primo.utils.getAndroidId
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject


object CartRequests {

    fun addItemToTempCart(stock_id: String, quantity: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("stock_id", stock_id).put("quantity", quantity).put("unique_id", getAndroidId())
                .put(APIPrimo.CLIENT_ID, APIPrimo.CLIENT_ID_VALUE)
                .put(APIPrimo.CLIENT_SECRET, APIPrimo.CLIENT_SECRET_VALUE)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_TCART)
                        .build()).post(body).build()
    }

    fun removeItemFromTempCart(cart_item_id: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("cart_item_id", cart_item_id)
                .put(APIPrimo.CLIENT_ID, APIPrimo.CLIENT_ID_VALUE)
                .put(APIPrimo.CLIENT_SECRET, APIPrimo.CLIENT_SECRET_VALUE)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_TCART)
                        .addPathSegment(getAndroidId())
                        .build()).delete(body).build()
    }

    fun retrieveTempCartDetail(): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_TCART)
                        .addPathSegment(getAndroidId())
                        .addQueryParameter(APIPrimo.CLIENT_ID, APIPrimo.CLIENT_ID_VALUE)
                        .addQueryParameter(APIPrimo.CLIENT_SECRET, APIPrimo.CLIENT_SECRET_VALUE)
                        .build()).get().build()
    }

    fun updateTempCartItem(stock_id: String, cart_item_id: String, quantity: String): Request {

        val bodyObject = JSONObject()

        val cartArr = JSONArray()
        val cartObj = JSONObject()

        cartObj.put("cart_item_id", cart_item_id)
        cartObj.put("stock_id", stock_id)
        cartObj.put("quantity", quantity)

        cartArr.put(cartObj)

        bodyObject.put("cart_items", cartArr)
                .put(APIPrimo.CLIENT_ID, APIPrimo.CLIENT_ID_VALUE)
                .put(APIPrimo.CLIENT_SECRET, APIPrimo.CLIENT_SECRET_VALUE)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_TCART)
                        .addPathSegment(getAndroidId())
                        .build()).put(body).build()
    }

    fun addItemToCart(stock_id: String, quantity: String, token: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("stock_id", stock_id).put("quantity", quantity)
        .put("lat", null).put("lng", null)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_CART)
                        .build()).post(body).header(APIPrimo.AUTHORIZATION, token).build()
    }

    fun removeItemFromCart(cart_item_id: String, token: String, cart_id: String): Request {

        val bodyObject = JSONObject()
        bodyObject.put("cart_item_id", cart_item_id)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_CART)
                        .addPathSegment(cart_id)
                        .build()).delete(body).header(APIPrimo.AUTHORIZATION, token).build()
    }

    fun retrieveCartDetail(cart_id: String, token: String): Request {

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_CART)
                        .addPathSegment(cart_id)
                        .build()).get().header(APIPrimo.AUTHORIZATION, token).build()
    }

    fun updateCartItem(stock_id: String, cart_item_id: String, quantity: String, token: String, cart_id: String): Request {

        val bodyObject = JSONObject()

        val cartArr = JSONArray()
        val cartObj = JSONObject()

        cartObj.put("cart_item_id", cart_item_id)
        cartObj.put("stock_id", stock_id)
        cartObj.put("quantity", quantity)

        cartArr.put(cartObj)

        bodyObject.put("cart_items", cartArr)
                .put(APIPrimo.CLIENT_ID, APIPrimo.CLIENT_ID_VALUE)
                .put(APIPrimo.CLIENT_SECRET, APIPrimo.CLIENT_SECRET_VALUE)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_CART)
                        .addPathSegment(cart_id)
                        .build()).put(body).header(APIPrimo.AUTHORIZATION, token).build()
    }

}