package com.primo.network.api_new

interface GetListOfWishes {

    fun getListOfWishes(token: String)
}

interface AddWishlistItem {

    fun addWishlistItem(productId: String, stockId: String, quantity: String, token: String)
}

interface RetrieveWishlistItem {

    fun retrieveWishlistItem(wishlistId: String, token: String)
}

interface DeleteWishlistItem {

    fun deleteWishlistItem(wishlistId: String, token: String)
}