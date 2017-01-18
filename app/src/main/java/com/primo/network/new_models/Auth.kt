package com.primo.network.new_models


open class Auth(open var access_token: String = "",
                open var expires_in: Long = -1,
                open var user_status: Int = -1,
                open var cart_id: String = "",
                open var creditcard_id: String = "",
                open var shipping_id: String = "") {

    override fun toString(): String{
        return "Auth(access_token='$access_token', expires_in=$expires_in, user_status=$user_status, cart_id='$cart_id', creditcard_id='$creditcard_id', shipping_id='$shipping_id')"
    }
}