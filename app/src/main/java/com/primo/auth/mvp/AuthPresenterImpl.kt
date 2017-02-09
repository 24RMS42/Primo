/**
 * Changes:
 *
 * - 503 HTTP status handling
 * - Add Fabric Answers Event
 *
 * 2015 © Primo . All rights reserved.
 */


package com.primo.auth.mvp

import android.util.Log
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import com.primo.R
import com.primo.main.MainClass
import com.primo.network.api_new.ApiResult
import com.primo.network.api_new.*
import com.primo.network.new_models.Auth
import com.primo.utils.consts.*
import com.primo.utils.getAndroidId
import com.primo.utils.getInt
import com.primo.utils.isValidEmail
import org.json.JSONObject


//TODO ADD SEVERAL CONNECTION ERROR
class AuthPresenterImpl(view: AuthView) : AuthPresenter(view) {

    override fun signIn(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            view?.showMessage(MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        } else if (!isValidEmail(email)) {
            view?.showMessage(MainClass.context.getString(R.string.please_enter_a_valid_email))
        } else {
            val signInCall: SignIn = SignInImpl(object: ApiResult<Auth?> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: Auth?) {

                    //Fabric Answers Event
                    Answers.getInstance().logLogin(LoginEvent().putMethod("Primo").putSuccess(true))

                    if (result != null) {
                        Log.d("Test", "login result:" + result.toString())
                        view?.onSigned(result)
                    } else {
                        view?.hideProgress()
                    }
                }

                override fun onError(message: String, code: Int) {

                    Log.d("Test", "login error message:" + message)
                    Log.d("Test", "http code:" + code.toString())


                    displayMessage(message, code)
                    view?.hideProgress()
                }
            })

            signInCall.signIn(email, password, getAndroidId())

        }
    }

    override fun resendConfirm(email: String) {

        if (email.isEmpty()) {
            view?.showMessage(MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        } else if (!isValidEmail(email)) {
            view?.showMessage(MainClass.context.getString(R.string.please_enter_a_valid_email))
        } else {

            val resend: ResendConfirm = ResendConfirmImpl(object : ApiResult<String?> {
                override fun onResult(result: String?) {
                    Log.d("Test", result)
                    view?.onEmailSent()
                }

                override fun onError(message: String, code: Int) {
                    Log.d("Test", message)
                    view?.showErrorMessage(message)

                    displayMessage(message, code)
                }
            })

            resend.resendConfirm(email)
        }
    }

    override fun restore(email: String) {

        if (email.isEmpty()) {
            return
        } else if (!isValidEmail(email)) {
            view?.showMessage(MainClass.context.getString(R.string.please_enter_a_valid_email))
        } else {

            /*val restoreCall : RestorePassword = RestorePasswordImpl(object : ApiResult<String>{

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: String) {
                    view?.showMessage(MainClass.context.getString(R.string.password_recovery_instructions_have_been_sent))
                }

                override fun onError(message: String, code: Int) {
                    when (code) {

                        NOT_FOUND_ERROR ->
                            view?.showMessage(MainClass.context.getString(R.string.the_email_address_is_not_registered))

                        CONNECTION_ERROR ->
                            view?.showMessage(MainClass.context.getString(R.string.please_check_your_internet_connection))
                    }
                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

            restoreCall.restorePassword(email)*/

            val restoreCall: ForgotPassword = ForgotPasswordImpl(object: ApiResult<String> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: String) {
                    view?.showMessage(MainClass.context.getString(R.string.password_recovery_instructions_have_been_sent))
                    Log.d("Test", result)
                }

                override fun onError(message: String, code: Int) {
                    Log.d("Test", message)
                    Log.d("Test", code.toString())

                    displayMessage(message, code)
                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

            restoreCall.forgotPassword(email)
        }
    }

    fun displayMessage(message: String, code: Int){

        var codeError = -1
        val jsonObject = JSONObject(message)
        codeError = jsonObject.getInt("error_code", -1)

        when (codeError) {

//            ACCESS_NOT_GRANTED_DATA_IS_NOT_VALID,
//            NOT_FOUND_ERROR ->
//                view?.showMessage(MainClass.context.getString(R.string.invalid_user_credentials))

            ACCESS_NOT_GRANTED_USER_NOT_CONFIRMED -> view?.onNotConfirmed()
            else -> view?.displayErrorMessage("", codeError)

//            UNPROCESSABLE_ENTITY ->
//                view?.showMessage(MainClass.context.getString(R.string.the_email_address_is_not_registered))
//
//            CONNECTION_ERROR ->
//                view?.showMessage(MainClass.context.getString(R.string.please_check_your_internet_connection))
//
//            SERVICE_UNAVAILABLE_ERROR,
//            UNAVAILABLE_ERROR ->
//                view?.showMessage(MainClass.context.getString(R.string.performing_system_maintenance))
//
//            SERVICE_INTERNAL_ERROR,
//            INTERNAL_ERROR ->
//                view?.showMessage(MainClass.context.getString(R.string.server_is_having_some_issue))

        }
    }
}