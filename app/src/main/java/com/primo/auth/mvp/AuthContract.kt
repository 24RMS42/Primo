package com.primo.auth.mvp

import com.primo.network.new_models.Auth
import com.primo.utils.base.BasePresenter
import com.primo.utils.base.BaseView

interface AuthView : BaseView {

    fun onSigned(auth: Auth)

    fun onNotConfirmed()

    fun onEmailSent()
}

abstract class AuthPresenter(view : AuthView) : BasePresenter<AuthView>(view) {

    override fun onResume() {
    }

    override fun onPause() {
    }

    abstract fun signIn(email: String, password: String)

    abstract fun resendConfirm(email: String)

    abstract fun restore(email: String)
}