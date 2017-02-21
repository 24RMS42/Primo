package com.primo.network.api_new


interface RetrieveUserProfile {

    fun retrieveUserProfile(token: String)
}

interface UpdateUserProfile {

    fun updateUserProfile(phone: String, firstname: String, lastname: String, address: String,
                          city: String, state: String, country: String, postcode: String,
                          delivery_preference: String, is_mail_campaign: String, token: String)
}

interface GetListShippingAddress {

    fun getListShippingAddress(token: String)
}

interface AddShippingAddress {

    fun addShippingAddress(phone: String, firstname: String, lastname: String, address: String,
                           city: String, state: String, country: Int, postcode: String,
                           is_default: Int, token: String)
}

interface RetrieveShippingAddress {

    fun retrieveShippingAddress()
}

interface UpdateShippingAddress {

    fun updateShippingAddress(shipping_id: String, phone: String, firstname: String, lastname: String, address: String,
                              city: String, state: String, country: Int, postcode: String,
                              is_default: Int, token: String)
}

interface UpdateDefaultShippingAddress {

    fun updateDefaultShippingAddress(shipping_id: String, token: String)
}

interface DeleteShippingAddress {

    fun deleteShippingAddress(shipping_id: String, token: String)
}

interface GetListCreditCard {

    fun getListCreditCard(token: String)
}

interface AddCreditCard {

    fun addCreditCard(cardnumber: String, cardname: String, cardyear: String, cardmonth: String,
                      cardcvc: String, is_default: Int, token: String)
}

interface RetrieveCreditCard {

    fun retrieveCreditCards(creditcard_id: String, token: String)
}

interface UpdateCreditCard {

    fun updateCreditCard(creditcard_id: String, cardname: String, cardyear: String, cardmonth: String,
                         token: String, is_default: Int = 0)
}

interface UpdateDefaultCreditCard {

    fun updateDefaultCreditCard(creditcard_id: String, token: String)
}

interface DeleteCreditCard {

    fun deleteCreditCard(creditcard_id: String, token: String)
}

interface SetCountry {

    fun setCountry(country: Int, token: String)
}

interface UpdateUserLanguage {

    fun updateUserLanguage(language: String, token: String)
}

interface GetCount {

    fun getCount(token: String)
}