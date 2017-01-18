package com.primo.network.api_new

import com.primo.network.new_models.CartItem


interface SearchProductByQr {

    fun searchProductByQr(qrcode: String)
}

interface RetrieveProduct {

    fun retrieveProduct(productId: String)
}

interface SearchProductByKeyword {

    fun searchProductByKeyword(keyword: String)
}

interface RetrieveProductStock {

    fun retrieveProductStock(product: CartItem)
}