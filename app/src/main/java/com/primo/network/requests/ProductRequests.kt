package com.primo.network.requests

import com.primo.main.MainClass
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject

object ProductRequests {

    val country_code = MainClass.getSavedCountry()

    fun searchProductByQr(qrcode: String): Request {
        val auth = MainClass.getAuth()
        val token = auth.access_token

        val bodyObject = JSONObject()
        bodyObject.put("qrcode", qrcode)
                .put(APIPrimo.CLIENT_ID, APIPrimo.CLIENT_ID_VALUE)
                .put(APIPrimo.CLIENT_SECRET, APIPrimo.CLIENT_SECRET_VALUE)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_PRODUCT)
                        .addPathSegment(APIPrimo.API_SEARCH)
                        .addPathSegment(APIPrimo.API_QR)
                        .build()).post(body).header(APIPrimo.AUTHORIZATION, token).header(APIPrimo.COUNTRY_ID, country_code).build()
    }

    fun retrieveProduct(productId: String): Request {
        val auth = MainClass.getAuth()
        val token = auth.access_token

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_PRODUCT)
                        .addPathSegment(productId)
                        .addQueryParameter(APIPrimo.CLIENT_ID, APIPrimo.CLIENT_ID_VALUE)
                        .addQueryParameter(APIPrimo.CLIENT_SECRET, APIPrimo.CLIENT_SECRET_VALUE)
                        .build()).get().header(APIPrimo.AUTHORIZATION, token).header(APIPrimo.COUNTRY_ID, country_code).build()
    }

    fun searchProductByKeyword(keyword: String): Request {
        val auth = MainClass.getAuth()
        val token = auth.access_token

        val bodyObject = JSONObject()
        bodyObject.put("keyword", keyword)
                .put(APIPrimo.CLIENT_ID, APIPrimo.CLIENT_ID_VALUE)
                .put(APIPrimo.CLIENT_SECRET, APIPrimo.CLIENT_SECRET_VALUE)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_PRODUCT)
                        .addPathSegment(APIPrimo.API_SEARCH)
                        .addPathSegment(APIPrimo.API_KEYWORD)
                        .build()).post(body).header(APIPrimo.AUTHORIZATION, token).header(APIPrimo.COUNTRY_ID, country_code).build()
    }

    fun retrieveProductStock(productId: String): Request {
        val auth = MainClass.getAuth()
        val token = auth.access_token

        val bodyObject = JSONObject()

        val products = JSONArray()
        products.put(JSONObject().put("product_id", productId))

        bodyObject.put("products", products)
                .put(APIPrimo.CLIENT_ID, APIPrimo.CLIENT_ID_VALUE)
                .put(APIPrimo.CLIENT_SECRET, APIPrimo.CLIENT_SECRET_VALUE)

        val body = RequestBody.create(APIPrimo.JSON, bodyObject.toString())

        return Request.Builder()
                .url(APIPrimo.getDefaultHttpBuilder()
                        .addPathSegment(APIPrimo.API_PRODUCT)
                        .addPathSegment(APIPrimo.API_STOCKS)
                        .build()).post(body).header(APIPrimo.AUTHORIZATION, token).header(APIPrimo.COUNTRY_ID, country_code).build()
    }
}

