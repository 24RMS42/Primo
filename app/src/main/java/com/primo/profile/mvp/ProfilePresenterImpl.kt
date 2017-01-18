package com.primo.profile.mvp

import android.support.v7.widget.SwitchCompat
import android.util.Log
import com.jakewharton.rxbinding.widget.RxCompoundButton
import com.jakewharton.rxbinding.widget.RxTextView
import com.primo.R
import com.primo.database.OrderDB
import com.primo.database.OrderDBImpl
import com.primo.main.MainClass
import com.primo.network.api_new.*
import com.primo.network.models.Country
import com.primo.network.models.State
import com.primo.network.new_models.Auth
import com.primo.network.new_models.CreditCard
import com.primo.network.new_models.UserProfile
import com.primo.utils.*
import com.primo.utils.base.BaseFragment
import com.primo.utils.views.DateChooserView
import com.primo.utils.views.PrefixedEditText
import org.json.JSONObject

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

            if (password.length < MIN_PASSWORD_LENGTH || repassword.length < MIN_PASSWORD_LENGTH) {
                view?.showMessage(MainClass.context.getString(R.string.that_password_is_too_short))
                return
            } else if (!password.equals(repassword)) {
                view?.showMessage(MainClass.context.getString(R.string.password_does_not_match))
                return
            }

            val signUp: SignUp = SignUpImpl(object : ApiResult<Auth?> {

                override fun onStart() {
                    super.onStart()
                    view?.showProgress()
                }

                override fun onResult(result: Auth?) {
                    Log.d("Test", result.toString())
                    if (result != null) {
                        view?.showMessage(MainClass.context.getString(R.string.sign_up_message))
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
                    }



                    Log.d("Test", message)
                    Log.d("Test", codeError.toString())
                    Log.d("Test", code.toString())
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

        if (!token.isEmpty() && isUserProfileValid("", firstname, lastname, phone, address, city,
                postcode, countryCode, true) && isCardValid("", "", cardName, cardYear, cardMonth, true)) {

            val updateUser: UpdateUserProfile = UpdateUserProfileImpl(object : ApiResult<String?> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: String?) {
                    Log.d("Test", result.toString())
                }

                override fun onError(message: String, code: Int) {
                    Log.d("Test", message)
                    Log.d("Test", code.toString())
                    view?.hideProgress()
                    view?.showErrorMessage(message)
                }

                override fun onComplete() {
                    view?.hideProgress()
                    view?.backToPreviousScreen()
                }
            })

            updateUser.updateUserProfile(phone, firstname, lastname, address, city, state,
                    countryCode.toString(), postcode, delivery,
                    if (isMailCompaign)
                        1.toString()
                    else
                        0.toString(),
                    token)

            val updateCard: UpdateCreditCard = UpdateCreditCardImpl(object : ApiResult<CreditCard?> {

                override fun onResult(result: CreditCard?) {

                }

                override fun onError(message: String, code: Int) {
                    Log.d("TEST", message)
                }
            })

            updateCard.updateCreditCard(cardId, cardName, cardYear, cardMonth, token)
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
                Log.d("TEST", message)
                view?.showErrorMessage(message)
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
                Log.d("TEST", message)
                view?.showErrorMessage(message)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        if (!token.isEmpty() && !creditId.isEmpty())
            cardCall.retrieveCreditCards(creditId, token)
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
}