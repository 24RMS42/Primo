package com.primo.network.api_new


interface PlaceAnOrder {

    fun placeAnOrder(creditcardId: String, shippingId: String, cartId: String, token: String,
                     lat: Float?, lng: Float?)
}

interface ReprocessAFailedOrder {

    fun reprocessAFailedOrder(orderId: String, creditcardId: String, token: String)
}

interface GetOrderHistory {

    fun getOrderHistory(page: Int, token: String)
}