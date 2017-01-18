package com.primo.network.models

import io.realm.RealmObject


open class ShippingQuote(open var shipping: String = "",
                         open var shippingId: Int = 0,
                         open var rate: Double = 0.0): RealmObject() {

    override fun equals(other: Any?): Boolean {

        if (other is ShippingQuote && other.shippingId == shippingId)
            return true

        return false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}