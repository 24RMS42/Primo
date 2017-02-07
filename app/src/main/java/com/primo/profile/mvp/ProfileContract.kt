/**
 * Changes:
 *
 * - Add PostCode api
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.profile.mvp

import android.support.v7.widget.SwitchCompat
import com.primo.network.models.Country
import com.primo.network.models.State
import com.primo.network.new_models.Address
import com.primo.network.new_models.CreditCard
import com.primo.network.new_models.CreditCardData
import com.primo.network.new_models.UserProfile
import com.primo.utils.base.BasePresenter
import com.primo.utils.base.BaseView
import com.primo.utils.views.DateChooserView
import com.primo.utils.views.PrefixedEditText
import java.util.*

interface OrderView : BaseView {

    fun updateUserData(userData: UserProfile?)

    fun updateCardData(card: CreditCard?)

    fun onSigned()

    fun onSignUped()

    fun onCountrySelected()

    fun onStateSelected()

    fun backToPreviousScreen()

    fun getAddressData(addressData: Address?)

    fun updateListShippingAddress(listShippingAddress: ArrayList<UserProfile>)

    fun updateShippingAddress()

    fun updateListCreditCard(listCreditCard: ArrayList<CreditCardData>)
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

    abstract fun signUpBasic(email: String, password: String, repassword: String, phone: String, firstname: String, lastname: String, delivery: String, isMailCompaign: Boolean)

    abstract fun signUpNOCC(email: String, password: String, repassword: String, phone: String, firstname: String, lastname: String,
                        address: String, city: String, state: String, country: String, postcode: String,
                        delivery: String, isMailCompaign: Boolean)

    abstract fun update(phone: String, firstname: String, lastname: String, address: String,
                        city: String, state: String, country: String, postcode: String,
                        cardName: String, cardExp: String, delivery: String, isMailCompaign: Boolean)

    abstract fun postCode(postcode: String)

    abstract fun getStatesByKey(key: String): MutableList<State>?

    abstract fun retrieveUserData()

    abstract fun retrieveCardData()

    abstract fun retrieveListCardData()

    abstract fun addCreditCard(cardnumber: String, cardname: String, cardyear: String, cardmonth: String,
                               cardcvc: String, is_default: Int)

    abstract fun updateCreditCard(creditcard_id:String, cardname: String, cardyear: String, cardmonth: String, is_default: Int)

    abstract fun updateDefaultCreditCard(creditcard_id:String)

    abstract fun deleteCreditCard(creditcard_id:String)

    abstract fun saveCountry(countryValue: Int)

    abstract fun saveState(stateCode: String)

    abstract fun retrieveListShippingAddress()

    abstract fun addShippingAddress(phone: String, firstname: String, lastname: String, address: String,
                                    city: String, state: String, country: Int, postcode: String,
                                    is_default: Int)

    abstract fun updateShippingAddress(shipping_id: String, phone: String, firstname: String, lastname: String, address: String,
                                       city: String, state: String, country: Int, postcode: String,
                                       is_default: Int)

    abstract fun updateDefaultShippingAddress(shipping_id: String)

    abstract fun deleteShippingAddress(shipping_id: String)

    abstract fun setCountry(country: Int)
}