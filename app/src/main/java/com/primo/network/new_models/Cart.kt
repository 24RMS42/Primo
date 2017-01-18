package com.primo.network.new_models


data class Cart(var cartId: String, var products: MutableList<CartItem>)