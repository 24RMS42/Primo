package com.primo.database

import android.view.View
import com.primo.network.models.Country
import com.primo.network.models.State
import com.primo.network.models.UserData
import com.primo.network.new_models.Auth
import com.primo.network.new_models.CreditCard
import com.primo.network.new_models.UserProfile
import com.primo.utils.base.BaseDB


abstract class OrderDB : BaseDB() {

    abstract fun saveUserData(v: View, s: CharSequence)

    abstract fun saveEmailPermission(isChecked: Boolean)

    abstract fun saveDeliveryTime(time: Int)

    abstract fun saveCountry(countryValue: Int)

    abstract fun saveState(stateCode: String)

    abstract fun saveExp(expM: String, expY: String)

    abstract fun getUserData(): UserProfile

    //abstract fun getCreditCard(): CreditCard

    abstract fun getAllCountries(): MutableList<Country>

    abstract fun getCountryByName(name: String): Country?

    abstract fun getStatesByKey(key: String): MutableList<State>

    abstract fun getStateByName(name: String): State
}