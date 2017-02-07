/**
 * Changes:
 *
 * - 503 HTTP status handling
 * - Add Fabric Answers Event
 * - Add SignUp api for basic and nocc
 * - Add PostCode api
 * - Remove password validation
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.profile.mvp

import android.support.v7.widget.SwitchCompat
import android.util.Log
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.SignUpEvent
import com.jakewharton.rxbinding.widget.RxCompoundButton
import com.jakewharton.rxbinding.widget.RxTextView
import com.primo.R
import com.primo.database.OrderDB
import com.primo.database.OrderDBImpl
import com.primo.main.MainClass
import com.primo.network.api_new.*
import com.primo.network.models.Country
import com.primo.network.models.State
import com.primo.network.new_models.*
import com.primo.utils.*
import com.primo.utils.base.BaseFragment
import com.primo.utils.consts.CONNECTION_ERROR
import com.primo.utils.consts.INTERNAL_ERROR
import com.primo.utils.consts.UNAVAILABLE_ERROR
import com.primo.utils.views.DateChooserView
import com.primo.utils.views.PrefixedEditText
import org.json.JSONObject
import java.util.*

class ProfilePresenterImpl(view: OrderView) : OrderPresenter(view), DateChooserView.OnTimePositionChangeListener {

    private val MIN_PASSWORD_LENGTH = 6
    private val CARD_LENGTH = 14
    private val CARD_CVC_LENGTH = 3

    private var orderDB: OrderDB? = null

    init {
        orderDB = OrderDBImpl()
    }

    override fun addTextWatcher(vararg views: PrefixedEditText?) {

        for (i in views) {
            if (i != null) {
                RxTextView.textChangeEvents(i).subscribe { e -> orderDB?.saveUserData(e.view(), e.text()) }
            }
        }
    }

    override fun addCheckListener(view: SwitchCompat?) {

        if (view != null)
            RxCompoundButton.checkedChanges(view).subscribe { e ->
                orderDB?.saveEmailPermission(e)
            }
    }

    override fun addTimePositionChangeListener(view: DateChooserView?) {

        view?.timePositionChangeListener = this
    }

    override fun onTimePositionChanged(timePosition: Int) {

        orderDB?.saveDeliveryTime(timePosition)
    }


    override fun getCountries(): List<Country>? {
        return orderDB?.getAllCountries()?.orEmpty()
    }

    override fun onExpChanged(month: String, year: String) {

        orderDB?.saveExp(month, year)
    }

    override fun getStatesByKey(key: String): MutableList<State>? {
        return orderDB?.getStatesByKey(key)
    }

    private fun isUserProfileValid(email: String, firstname: String, lastname: String, cell_phone: String,
                                   address: String, city: String, postcode: String, country: Int,
                                   isUpdate: Boolean = false): Boolean {

        if (isFieldsEmpty(firstname, lastname, cell_phone,
                address, city, postcode) || country <= 0 || (!isUpdate && email.isEmpty())) {
            view?.showMessage(MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        } else if (!isValidEmail(email)) {
            view?.showMessage(MainClass.context.getString(R.string.please_enter_a_valid_email))
        } /*else if (state.isEmpty()) {
            view?.showMessage(MainClass.context.getString(R.string.please_select_state))
        }*/ else
            return true


        return false
    }

    private fun isCardValid(cardCvc: String, cardNumber: String, cardName: String, cardYear: String, cardMonth: String,
                            isUpdate: Boolean = false): Boolean {

        if (cardName.isEmpty() || (cardCvc.isEmpty() && !isUpdate)) {
            view?.showMessage(MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        } else if (cardNumber.length < CARD_LENGTH && !isUpdate) {
            view?.showMessage(MainClass.context.getString(R.string.card_number_should_contain))
        } else if (cardCvc.length < CARD_CVC_LENGTH && !isUpdate) {
            view?.showMessage(MainClass.context.getString(R.string.your_cvc_number_should_contain))
        }

//        else if (!isValidCard(cardNumber) && !isUpdate) {
//            view?.showMessage(MainClass.context.getString(R.string.looks_like_your_credit_number_isnt_correct))
//        }

        else if (!isValidDate(cardMonth, cardYear)) {
            view?.showMessage(MainClass.context.getString(R.string.the_expration_date_of_your_credit_card))
        } else
            return true

        return false
    }

    override fun signUpBasic(email: String, password: String, repassword: String, phone: String, firstname: String, lastname: String, delivery: String, isMailCompaign: Boolean){

        if (isValidEmail(email)) {

            if (password.length < MIN_PASSWORD_LENGTH /*|| repassword.length < MIN_PASSWORD_LENGTH*/) {
                view?.showMessage(MainClass.context.getString(R.string.that_password_is_too_short))
                return
            }/* else if (!password.equals(repassword)) {
                view?.showMessage(MainClass.context.getString(R.string.password_does_not_match))
                return
            }*/

            val signUp: SignUp = SignUpImpl(object : ApiResult<Auth?> {

                override fun onStart() {
                    super.onStart()
                    view?.showProgress()
                }

                override fun onResult(result: Auth?) {

                    //Fabric Answers Event
                    Answers.getInstance().logSignUp(SignUpEvent().putMethod("Primo").putSuccess(true))

                    if (result != null) {
                        view?.showMessage(MainClass.context.getString(R.string.sign_up_message))
                        view?.onSignUped()
                    }
                }

                override fun onError(message: String, code: Int) {

                    displayMessage(message, code)

                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

            signUp.signUpBasic(email, password, repassword, phone, firstname, lastname, delivery, if (isMailCompaign) 1.toString() else 0.toString(), getAndroidId())

        }
        else
            view?.showMessage(MainClass.context.getString(R.string.please_enter_a_valid_email))
    }

    override fun signUpNOCC(email: String, password: String, repassword: String, phone: String, firstname: String, lastname: String,
                            address: String, city: String, state: String, country: String, postcode: String,
                            delivery: String, isMailCompaign: Boolean){

        val countryCode = orderDB?.getCountryByName(country)?.value ?: -1

        if (isUserProfileValid(email, firstname, lastname, phone, address, city, postcode, countryCode)) {

            /*if (password.length < MIN_PASSWORD_LENGTH || repassword.length < MIN_PASSWORD_LENGTH) {
                view?.showMessage(MainClass.context.getString(R.string.that_password_is_too_short))
                return
            } else if (!password.equals(repassword)) {
                view?.showMessage(MainClass.context.getString(R.string.password_does_not_match))
                return
            }*/

            val signUp: SignUp = SignUpImpl(object : ApiResult<Auth?> {

                override fun onStart() {
                    super.onStart()
                    view?.showProgress()
                }

                override fun onResult(result: Auth?) {

                    //Fabric Answers Event
                    Answers.getInstance().logSignUp(SignUpEvent().putMethod("Primo").putSuccess(true))

                    if (result != null) {
                        view?.showMessage(MainClass.context.getString(R.string.sign_up_message))
                        view?.onSignUped()
                    }
                }

                override fun onError(message: String, code: Int) {

                    displayMessage(message, code)

                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

            signUp.signUpNOCC(email, password, repassword, phone, firstname, lastname, address, city,
                    state, countryCode.toString(), postcode,
                    delivery, if (isMailCompaign) 1.toString() else 0.toString(), getAndroidId())
        }
    }

    override fun signUp(email: String, password: String, repassword: String, phone: String,
                        firstname: String, lastname: String, address: String, city: String,
                        state: String, country: String, postcode: String, cardNumber: String,
                        cardName: String, cardExp: String, cardCvc: String,
                        delivery: String, isMailCompaign: Boolean) {

        val countryCode = orderDB?.getCountryByName(country)?.value ?: -1

        var cardYear = ""
        var cardMonth = ""

        val exp = cardExp.split("/")
        if (exp.size == 2) {
            cardMonth = exp[0]
            cardYear = exp[1]
        }

        if (isUserProfileValid(email, firstname, lastname, phone, address, city, postcode, countryCode) &&
                isCardValid(cardCvc, cardNumber, cardName, cardYear, cardMonth)) {

            /*if (password.length < MIN_PASSWORD_LENGTH || repassword.length < MIN_PASSWORD_LENGTH) {
                view?.showMessage(MainClass.context.getString(R.string.that_password_is_too_short))
                return
            } else if (!password.equals(repassword)) {
                view?.showMessage(MainClass.context.getString(R.string.password_does_not_match))
                return
            }*/

            val signUp: SignUp = SignUpImpl(object : ApiResult<Auth?> {

                override fun onStart() {
                    super.onStart()
                    view?.showProgress()
                }

                override fun onResult(result: Auth?) {

                    //Fabric Answers Event
                    Answers.getInstance().logSignUp(SignUpEvent().putMethod("Primo").putSuccess(true))

                    if (result != null) {
                        view?.showMessage(MainClass.context.getString(R.string.sign_up_message))
                        view?.onSignUped()
                    }
                }

                override fun onError(message: String, code: Int) {

                    var codeError = -1
                    val jsonObject = JSONObject(message)
                    codeError = jsonObject.getInt("error_code", -1)

                    view?.showErrorMessage(message)


                    if (codeError == 101001) {
                        view?.showMessage(MainClass.context.getString(R.string.looks_like_your_credit_number_isnt_correct))
                    }
                    if (codeError == 101002) {
                        view?.showMessage(MainClass.context.getString(R.string.looks_like_your_credit_number_isnt_correct))
                    }
                    if (codeError == 101003) {
                        view?.showMessage(MainClass.context.getString(R.string.looks_like_your_credit_number_isnt_correct))
                    }
                    if (codeError == 101004) {
                        view?.showMessage(MainClass.context.getString(R.string.looks_like_your_credit_number_isnt_correct))
                    }
                    if (codeError == 101005) {
                        view?.showMessage(MainClass.context.getString(R.string.looks_like_your_credit_number_isnt_correct))
                    }
                    if (codeError == 101006) {
                        view?.showMessage(MainClass.context.getString(R.string.looks_like_your_credit_number_isnt_correct))
                    }
                    if (codeError == 101007) {
                        view?.showMessage(MainClass.context.getString(R.string.looks_like_your_credit_number_isnt_correct))
                    }
                    if (codeError == 101008) {
                        view?.showMessage(MainClass.context.getString(R.string.looks_like_your_credit_number_isnt_correct))
                    }
                    if (codeError == 101009) {
                        view?.showMessage(MainClass.context.getString(R.string.looks_like_your_credit_number_isnt_correct))
                    }
                    if (codeError == 101010) {
                        view?.showMessage(MainClass.context.getString(R.string.looks_like_your_credit_number_isnt_correct))
                    }else
                        displayMessage(message, code)

                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

            signUp.signUp(email, password, repassword, phone, firstname, lastname, address, city,
                    state, countryCode.toString(), postcode, cardNumber, cardName, cardYear, cardMonth,
                    cardCvc, delivery, if (isMailCompaign) 1.toString() else 0.toString(), getAndroidId())

        }
    }

    override fun postCode(postcode: String){

        val postCode: PostCode = PostCodeImpl(object : ApiResult<Address?> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: Address?) {

                view?.getAddressData(result)
            }

            override fun onError(message: String, code: Int) {

                view?.hideProgress()

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        postCode.postCode(postcode)
    }

    override fun update(phone: String, firstname: String, lastname: String, address: String, city: String,
                        state: String, country: String, postcode: String, cardName: String,
                        cardExp: String, delivery: String, isMailCompaign: Boolean) {

        val countryCode = orderDB?.getCountryByName(country)?.value ?: -1

        val auth = MainClass.getAuth()
        val token = auth.access_token
        val cardId = auth.creditcard_id

        var cardYear = ""
        var cardMonth = ""

        val exp = cardExp.split("/")
        if (exp.size == 2) {
            cardMonth = exp[0]
            cardYear = exp[1]
        }

        if (!token.isEmpty()/* && isUserProfileValid("", firstname, lastname, phone, address, city,
                postcode, countryCode, true) && isCardValid("", "", cardName, cardYear, cardMonth, true)*/) {

            val updateUser: UpdateUserProfile = UpdateUserProfileImpl(object : ApiResult<String?> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: String?) {
                    Log.d("Test", "update basic profile result:" + result.toString())
                    view?.updateShippingAddress()
                }

                override fun onError(message: String, code: Int) {

                    view?.hideProgress()
                    view?.showErrorMessage(message)

                    displayMessage(message, code)
                }

                override fun onComplete() {
                    view?.hideProgress()
                    //view?.backToPreviousScreen()
                }
            })

            updateUser.updateUserProfile(phone, firstname, lastname, address, city, state,
                    countryCode.toString(), postcode, delivery,
                    if (isMailCompaign)
                        1.toString()
                    else
                        0.toString(),
                    token)

            /*val updateCard: UpdateCreditCard = UpdateCreditCardImpl(object : ApiResult<CreditCard?> {

                override fun onResult(result: CreditCard?) {

                }

                override fun onError(message: String, code: Int) {
                    Log.d("TEST", message)

                    displayMessage(message, code)
                }
            })

            updateCard.updateCreditCard(cardId, cardName, cardYear, cardMonth, token)*/
        }
    }

    override fun retrieveUserData() {

        val token = MainClass.getAuth().access_token

        val userDataCall: RetrieveUserProfile = RetrieveUserProfileImpl(object : ApiResult<UserProfile?> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: UserProfile?) {
                view?.updateUserData(result)
            }

            override fun onError(message: String, code: Int) {

                view?.showErrorMessage(message)

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        if (!token.isEmpty())
            userDataCall.retrieveUserProfile(token)
    }

    override fun retrieveCardData() {

        val auth = MainClass.getAuth()
        val token = auth.access_token
        val creditId = auth.creditcard_id

        val cardCall: RetrieveCreditCard = RetrieveCreditCardImpl(object : ApiResult<CreditCard?> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: CreditCard?) {
                view?.updateCardData(result)
            }

            override fun onError(message: String, code: Int) {

                view?.showErrorMessage(message)

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        if (!token.isEmpty() && !creditId.isEmpty())
            cardCall.retrieveCreditCards(creditId, token)
    }

    override fun retrieveListCardData() {

        val auth = MainClass.getAuth()
        val token = auth.access_token
        val creditId = auth.creditcard_id

        val listCardCall: GetListCreditCard = RetrieveListCreditCardImpl(object : ApiResult<ArrayList<CreditCardData>> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: ArrayList<CreditCardData>) {
                view?.updateListCreditCard(result)
            }

            override fun onError(message: String, code: Int) {

                view?.showErrorMessage(message)

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        if (!token.isEmpty()/* && !creditId.isEmpty()*/)
            listCardCall.getListCreditCard(token)
    }

    override fun updateCreditCard(creditcard_id:String, cardname: String, cardyear: String, cardmonth: String, is_default: Int) {

        val auth = MainClass.getAuth()
        val token = auth.access_token
        val creditId = auth.creditcard_id

        val updateCardCall: UpdateCreditCard = UpdateCreditCardImpl(object : ApiResult<CreditCard?> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: CreditCard?) {
                view?.updateShippingAddress()
            }

            override fun onError(message: String, code: Int) {

                view?.showErrorMessage(message)

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        if (!token.isEmpty()/* && !creditId.isEmpty()*/)
            updateCardCall.updateCreditCard(creditcard_id, cardname, cardyear, cardmonth, token, is_default)
    }

    override fun updateDefaultCreditCard(creditcard_id: String) {

        val auth = MainClass.getAuth()
        val token = auth.access_token
        val creditId = auth.creditcard_id

        val updateDefaultCardCall: UpdateDefaultCreditCard = UpdateDefaultCreditCardImpl(object : ApiResult<ArrayList<CreditCardData>> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: ArrayList<CreditCardData>) {
                view?.updateListCreditCard(result)
            }

            override fun onError(message: String, code: Int) {

                view?.showErrorMessage(message)

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        if (!token.isEmpty()/* && !creditId.isEmpty()*/)
            updateDefaultCardCall.updateDefaultCreditCard(creditcard_id, token)
    }

    override fun deleteCreditCard(creditcard_id: String) {

        val auth = MainClass.getAuth()
        val token = auth.access_token
        val creditId = auth.creditcard_id

        val deleteCardCall: DeleteCreditCard = DeleteCreditCardImpl(object : ApiResult<ArrayList<CreditCardData>> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: ArrayList<CreditCardData>) {
                view?.updateListCreditCard(result)
            }

            override fun onError(message: String, code: Int) {

                view?.showErrorMessage(message)

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        if (!token.isEmpty()/* && !creditId.isEmpty()*/)
            deleteCardCall.deleteCreditCard(creditcard_id, token)
    }

    override fun addCreditCard(cardnumber: String, cardname: String, cardyear: String, cardmonth: String,
                               cardcvc: String, is_default: Int) {

        val token = MainClass.getAuth().access_token

        val addCreditCardCall: AddCreditCard = AddCreditCardImpl(object : ApiResult<String?> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: String?) {
                view?.updateShippingAddress()
                Log.d("Test", "add credit card call result:" + result)
            }

            override fun onError(message: String, code: Int) {

                view?.showErrorMessage(message)

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        if (!token.isEmpty())
            addCreditCardCall.addCreditCard(cardnumber, cardname, cardyear, cardmonth, cardcvc,
                    is_default, token)
    }

    override fun retrieveListShippingAddress() {

        val token = MainClass.getAuth().access_token

        val listShippingAddressCall: GetListShippingAddress = RetrieveListShippingAddressImpl(object : ApiResult<ArrayList<UserProfile>> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: ArrayList<UserProfile>) {
                view?.updateListShippingAddress(result)
            }

            override fun onError(message: String, code: Int) {

                view?.showErrorMessage(message)

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        if (!token.isEmpty())
            listShippingAddressCall.getListShippingAddress(token)
    }

    override fun addShippingAddress(phone: String, firstname: String, lastname: String, address: String,
                                    city: String, state: String, country: Int, postcode: String,
                                    is_default: Int) {

        val token = MainClass.getAuth().access_token

        val addShippingAddressCall: AddShippingAddress = AddShippingAddressImpl(object : ApiResult<String?> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: String?) {
                view?.updateShippingAddress()
                Log.d("Test", "add shipping address call result:" + result)
            }

            override fun onError(message: String, code: Int) {

                view?.showErrorMessage(message)

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        if (!token.isEmpty())
            addShippingAddressCall.addShippingAddress(phone, firstname, lastname, address,
                    city, state, country, postcode,
                    is_default, token)
    }

    override fun updateShippingAddress(shipping_id: String, phone: String, firstname: String, lastname: String, address: String,
                                    city: String, state: String, country: Int, postcode: String,
                                    is_default: Int) {

        val token = MainClass.getAuth().access_token

        val updateShippingAddressCall: UpdateShippingAddress = UpdateShippingAddressImpl(object : ApiResult<ArrayList<UserProfile>> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: ArrayList<UserProfile>) {
                view?.updateShippingAddress()
                Log.d("Test", "update shipping address call result:" + result)
            }

            override fun onError(message: String, code: Int) {

                view?.showErrorMessage(message)

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        if (!token.isEmpty())
            updateShippingAddressCall.updateShippingAddress(shipping_id, phone, firstname, lastname, address,
                    city, state, country, postcode,
                    is_default, token)
    }

    override fun updateDefaultShippingAddress(shipping_id: String) {

        val token = MainClass.getAuth().access_token

        val updateDefaultShippingAddressCall: UpdateDefaultShippingAddress = UpdateDefaultShippingAddressImpl(object : ApiResult<ArrayList<UserProfile>> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: ArrayList<UserProfile>) {
                view?.updateListShippingAddress(result)
                Log.d("Test", "update default shipping address call result:" + result)
            }

            override fun onError(message: String, code: Int) {

                view?.showErrorMessage(message)

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        if (!token.isEmpty())
            updateDefaultShippingAddressCall.updateDefaultShippingAddress(shipping_id, token)
    }

    override fun deleteShippingAddress(shipping_id: String) {

        val token = MainClass.getAuth().access_token

        val deleteShippingAddressCall: DeleteShippingAddress = DeleteShippingAddressImpl(object : ApiResult<ArrayList<UserProfile>> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: ArrayList<UserProfile>) {
                view?.updateListShippingAddress(result)
                Log.d("Test", "delete shipping address call result:" + result)
            }

            override fun onError(message: String, code: Int) {

                view?.showErrorMessage(message)

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        if (!token.isEmpty())
            deleteShippingAddressCall.deleteShippingAddress(shipping_id, token)
    }

    override fun setCountry(country: Int) {

        val token = MainClass.getAuth().access_token

        val setCountryCall: SetCountry = SetCountryImpl(object : ApiResult<String?> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: String?) {
                view?.backToPreviousScreen()
                Log.d("Test", "set country result:" + result)
            }

            override fun onError(message: String, code: Int) {

                view?.showErrorMessage(message)

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        if (!token.isEmpty())
            setCountryCall.setCountry(country, token)
    }

    override fun saveCountry(countryValue: Int) {
        orderDB?.saveCountry(countryValue)
    }

    override fun saveState(stateCode: String) {
        orderDB?.saveState(stateCode)
    }

    override fun onDestroy() {
        super.onDestroy()
        orderDB?.onDestroy()
    }

    fun displayMessage(message: String, code: Int){

        Log.d("Test", "signup error message:" + message)
        Log.d("Test", "signup error code:" + code)

        if (code == 502) {
            view?.showMessage(MainClass.context.getString(R.string.something_went_wrong_try_again))
            return
        }

        var codeError = -1
        val jsonObject = JSONObject(message)
        codeError = jsonObject.getInt("error_code", -1)

        if (codeError == 200001) {
            val errorsObjectArr = jsonObject.getJSONArrayExt("errors")
            val errorsObject = errorsObjectArr.getJSONObject(0)
            val errorDetail = errorsObject.getString("code")
            if (errorDetail == "email_not_unique")
                codeError = 300009 // it is error code in case of duplicate email, it is set by me
        }

        view?.displayErrorMessage("", codeError)
    }
}