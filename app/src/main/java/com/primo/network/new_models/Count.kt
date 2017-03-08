package com.primo.network.new_models

open class Count(open var cart_count: Int = 0,
                   open var total_price: Double = 0.0,
                   open var total_final_price: Double = 0.0,
                   open var total_discount: Double = 0.0,
                   open var currency: Int = 0,
                   open var shipping_count: Int = 0) {}