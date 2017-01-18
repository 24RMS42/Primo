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

    fun getListShippingAddress()
}

interface AddShippingAddress {

    fun addShippingAddress()
}

interface RetrieveShippingAddress {

    fun retrieveShippingAddress()
}

interface UpdateShippingAddress {

    fun updateShippingAddress()
}

interface DeleteShippingAddress {

    fun deleteShippingAddress()
}

interface GetListCreditCard {

    fun getListCreditCard()
}

interface AddCreditCard {

    fun addCreditCard()
}

interface RetrieveCreditCard {

    fun retrieveCreditCards(creditcard_id: String, token: String)
}

interface UpdateCreditCard {

    fun updateCreditCard(creditcard_id: String, cardname: String, cardyear: String, cardmonth: String,
                         token: String, is_default: String = 1.toString())
}

interface DeleteCreditCard {

    fun deleteCreditCard()
}