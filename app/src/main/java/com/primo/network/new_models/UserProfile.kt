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
                       open var is_mail_campaign: Int = 0): RealmObject() {
}