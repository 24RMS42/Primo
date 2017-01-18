package com.primo.network.api_new


interface AddItemToTempCart {

    fun addItemToTempCart(stock_id: String, quantity: String)
}

interface RemoveItemFromTempCart {

    fun removeItemFromTempCart(stock_id: String)
}

interface RetrieveTempCartDetail {

    fun retrieveTempCartDetail()
}

interface UpdateTempCartItem {

    fun updateTempCartItem(stock_id: String, cart_item_id: String, quantity: String)
}

interface AddItemToCart {

    fun addItemToCart(stock_id: String, quantity: String, token: String)
}

interface RemoveItemFromCart {

    fun removeItemFromCart(stock_id: String, token: String, cart_id: String)
}

interface RetrieveCartDetail {

    fun retrieveCartDetail(cart_id: String, token: String)
}

interface UpdateCartItem {

    fun updateCartItem(stock_id: String, cart_item_id: String, quantity: String, token: String, cart_id: String)
}
