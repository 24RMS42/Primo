package com.primo.network.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserData(open var id: Int = 0,
                    open var firstName: String = "",
                    open var lastName: String = "",
                    open var phone: String = "",
                    open var email: String = "",
                    open var password: String = "",
                    open var address: String = "",
                    open var city: String = "",
                    open var country: String = "",
                    open var state: String = "",
                    open var zip: String = "",
                    open var cardN: String = "",
                    open var cardExpM: String = "",
                    open var cardExpY: String = "",
                    open var cardCvc: String = "",
                    open var deliveryTime: Int = 0,
                    open var emailPermission: Boolean = false) : RealmObject() {

}