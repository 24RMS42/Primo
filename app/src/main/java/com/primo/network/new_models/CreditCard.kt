package com.primo.network.new_models

open class CreditCard(open var cardId: String = "",
                      open var cardname: String = "",
                      open var cardyear: String = "",
                      open var cardmonth: String = "",
                      open var lastFour: String = "",
                      open var is_default: Int = 0 /* 0 or 1 */) {
}