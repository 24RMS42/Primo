/**
 * Changes:
 *
 * - Save encrypted token
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.main

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.primo.network.new_models.Auth
import com.primo.utils.SecurePreferences
import com.primo.utils.consts.*
import com.primo.utils.getDeviceLanguage
import com.primo.utils.other.RxBus
import java.util.*

object MainClass {

    lateinit var context : Context
    private var _rxBus: RxBus? = null

    fun init(context: Context){
        this.context = context
    }

    fun getRxBus() : RxBus? {
        if (_rxBus == null) {
            _rxBus = RxBus()
            return _rxBus
        } else {
            return _rxBus;
        }
    }

    fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(APP, Context.MODE_PRIVATE)
    }

    fun getSecurePreferences(): SecurePreferences {
        return SecurePreferences(context, APP, SECUREKEY, true)
    }

    fun saveLoginData(email: String, password: String) {
        getSharedPreferences().edit()
                .putString(LOGIN_EMAIL, email)
                .putString(LOGIN_PASSWORD, password).apply()
    }

    fun getLoginData(): Pair<String, String> {

        val email = getSharedPreferences().getString(LOGIN_EMAIL, "")
        val password = getSharedPreferences().getString(LOGIN_PASSWORD, "")

        return Pair(email, password)
    }

    fun deleteLoginData() {
        getSharedPreferences().edit()
                .putString(LOGIN_EMAIL, "")
                .putString(LOGIN_PASSWORD, "").apply()
    }

    fun saveAuth(auth: Auth?) {

        auth?.let {
            getSharedPreferences().edit()
                    //.putString(ACCESS_TOKEN, auth.access_token)
                    .putLong(EXPIRES_IN, auth.expires_in)
                    .putInt(USER_STATUS, auth.user_status)
                    .putString(CART_ID, auth.cart_id)
                    .putString(CREDITCARD_ID, auth.creditcard_id)
                    .putString(SHIPPING_ID, auth.shipping_id).apply()
        }
        getSecurePreferences().put(ACCESS_TOKEN, auth?.access_token)
        Log.d("Test", "save token:" + auth?.access_token)
        Log.d("Test", "save cartID:" + auth?.cart_id)
        Log.d("Test", "save shippingID:" + auth?.shipping_id)
        Log.d("Test", "save creditcardID:" + auth?.creditcard_id)
    }

    fun getAuth(): Auth {

        val prefs = getSharedPreferences()

        var token = getSecurePreferences().getString(ACCESS_TOKEN)
        if (token == null) token = ""

        //val token = prefs.getString(ACCESS_TOKEN, "")
        val expires = prefs.getLong(EXPIRES_IN, 0)
        val status = prefs.getInt(USER_STATUS, 0)
        val cartId = prefs.getString(CART_ID, "")
        val creditId = prefs.getString(CREDITCARD_ID, "")
        val shippingId = prefs.getString(SHIPPING_ID, "")

        val auth = Auth(token, expires, status, cartId, creditId, shippingId)

        return auth
    }

    fun getCurrentTime(): Long{
        val s = System.currentTimeMillis() / 1000
        return s
    }

    fun saveSignUpTime(){
        Log.d("Test", "signUptime:" + getCurrentTime())
        getSharedPreferences().edit().putLong(SIGNUP_TIME, getCurrentTime()).apply()
    }

    fun getSignUpTime(): Long{
        val s = getSharedPreferences().getLong(SIGNUP_TIME,0)
        return s
    }

    fun saveUserLanguage(){
        getSharedPreferences().edit().putString(USER_LANGUAGE, getDeviceLanguage()).apply()
    }

    fun getSavedLanguage(): String{
        return getSharedPreferences().getString(USER_LANGUAGE, "")
    }

    fun saveUserCountry(countryCode: String){
        getSharedPreferences().edit().putString(USER_COUNTRY, countryCode).apply()
    }

    fun getSavedCountry(): String{
        return getSharedPreferences().getString(USER_COUNTRY, "")
    }
}