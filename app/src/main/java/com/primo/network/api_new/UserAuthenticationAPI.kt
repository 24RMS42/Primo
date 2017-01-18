package com.primo.network.api_new

interface SignIn {

    fun signIn(email: String, password: String, unique_id: String)
}

interface ChangePassword {

    fun changePassword(current_password: String, new_password: String, renew_password: String)
}