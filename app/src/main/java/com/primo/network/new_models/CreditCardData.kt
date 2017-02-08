package com.primo.network.new_models

open class CreditCardData(open var cardId: String = "",
                      open var cardname: String = "",
                      open var cardyear: Int = -1,
                      open var cardmonth: Int = -1,
                      open var cardtype: Int = -1,
                      open var cardtype_name: String = "",
                      open var lastFour: String = "",
                      open var is_default: Int = 0 /* 0 or 1 */) {
}