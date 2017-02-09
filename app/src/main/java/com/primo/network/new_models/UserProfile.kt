package com.primo.network.new_models

import io.realm.RealmObject


open class UserProfile(open var email: String = "",
                       open var phone: String = "",
                       open var cell_phone: String = "",
                       open var firstname: String = "",
                       open var lastname: String = "",
                       open var address: String = "",
                       open var city: String = "",
                       open var state: String = "",
                       open var country: Int = -1,
                       open var postcode: String = "",
                       open var delivery_preference: Int = 2,
                       open var is_mail_campaign: Int = 0,
                       open var is_default: Int = 1,
                       open var shipping_id: String = ""): RealmObject() {
}

//it is used during signup process
open class TempUserProfile(open var email: String = "",
                       open var phone: String = "",
                       open var password: String = "",
                       open var repassword: String = "",
                       open var firstname: String = "",
                       open var lastname: String = "",
                       open var address: String = "",
                       open var address2: String = "",
                       open var city: String = "",
                       open var state: String = "",
                       open var country: Int = -1,
                       open var countryName: String = "",
                       open var postcode: String = "",
                       open var delivery_preference: Int? = -1,
                       open var is_mail_campaign: Int = 0,
                       open var cardN: String = "",
                       open var cardname: String = "",
                       open var cardyear: String = "",
                       open var cardmonth: String = "",
                       open var lastFour: String = "")