package com.primo.network.new_models

import io.realm.RealmObject


open class Discount(open var quantity: Int = 0,
                    open var amount: Double = 0.0,
                    open var discount_type: Int = 0): RealmObject() {
}