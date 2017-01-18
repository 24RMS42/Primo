package com.primo.profile.mvp

import android.support.v7.widget.SwitchCompat
import com.primo.network.models.Country
import com.primo.network.models.State
import com.primo.network.new_models.CreditCard
import com.primo.network.new_models.UserProfile
import com.primo.utils.base.BasePresenter
import com.primo.utils.base.BaseView
import com.primo.utils.views.DateChooserView
import com.primo.utils.views.PrefixedEditText

interface OrderView : BaseView {

    fun updateUserData(userData: UserProfile?)

    fun updateCardData(card: CreditCard?)

    fun onSigned()

    fun onCountrySelected()

    fun onStateSelected()

    fun backToPreviousScreen()

}

abstract class OrderPresenter(view: OrderView) : BasePresenter<OrderView>(view) {

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    abstract fun addTextWatcher(vararg views: PrefixedEditText?)

    abstract fun onExpChanged(month: String, year: String)

    abstract fun addCheckListener(view: SwitchCompat?)

    abstract fun addTimePositionChangeListener(view: DateChooserView?)

    abstract fun getCountries(): List<Country>?

    abstract fun signUp(email: String, password: String, repassword: String, phone: String, firstname: String, lastname: String,
                        address: String, city: String, state: String, country: String, postcode: String,
                        cardNumber: String, cardName: String, cardExp: String,
                        cardCvc: String, delivery: String, isMailCompaign: Boolean)

    abstract fun update(phone: String, firstname: String, lastname: String, address: String,
                        city: String, state: String, country: String, postcode: String,
                        cardName: String, cardExp: String, delivery: String, isMailCompaign: Boolean)

    abstract fun getStatesByKey(key: String): MutableList<State>?

    abstract fun retrieveUserData()

    abstract fun retrieveCardData()

    abstract fun saveCountry(countryValue: Int)

    abstract fun saveState(stateCode: String)
}