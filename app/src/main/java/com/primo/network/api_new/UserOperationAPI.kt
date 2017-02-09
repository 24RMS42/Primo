/**
 * Changes:
 *
 * - Add SignUp api for basic and nocc
 * - Add PostCode api
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.network.api_new

interface SignUp {

    fun signUp(email: String, password: String, repassword: String,
               phone: String, firstname: String, lastname: String,
               address: String, city: String, state: String,
               country: String, postcode: String, cardnumber: String,
               cardname: String, cardyear: String, cardmonth: String,
               cardcvc: String, delivery_preference: String, is_mail_campaign: String,
               unique_id: String)

    fun signUpBasic(email: String, password: String, repassword: String,
               phone: String, firstname: String, lastname: String,
               delivery_preference: String, is_mail_campaign: String,
               unique_id: String)

    fun signUpNOCC(email: String, password: String, repassword: String,
               phone: String, firstname: String, lastname: String,
               address: String, city: String, state: String,
               country: String, postcode: String,
               delivery_preference: String, is_mail_campaign: String,
               unique_id: String)
}

interface ResendConfirm {

    fun resendConfirm(email: String)
}

interface ForgotPassword {

    fun forgotPassword(email: String)
}

interface PostCode {

    fun postCode(postcode: String)
}
