package com.primo.database

import android.view.View
import com.primo.R
import com.primo.network.models.Country
import com.primo.network.models.State
import com.primo.network.new_models.Auth
import com.primo.network.new_models.CreditCard
import com.primo.network.new_models.UserProfile


class OrderDBImpl : OrderDB() {

    private var user: UserProfile? = null
    //private var card: CreditCard? = null

    init {

        user = realm.where(UserProfile::class.java).findFirst()

        if (user == null) {
            realm.beginTransaction()
            user = realm.createObject(UserProfile::class.java)
            realm.commitTransaction()
        }

        /*card = realm.where(CreditCard::class.java).findFirst()

        if (card == null) {
            realm.beginTransaction()
            card = realm.createObject(CreditCard::class.java)
            realm.commitTransaction()
        }*/
    }

    override fun saveUserData(v: View, s: CharSequence) {

        realm.beginTransaction()

        when (v.id) {

            R.id.first_name -> user?.firstname = s.toString()
            R.id.last_name -> user?.lastname = s.toString()
            R.id.phone_number -> user?.cell_phone = s.toString()
            R.id.email -> user?.email = s.toString()
            R.id.address -> user?.address = s.toString()
            R.id.city -> user?.city = s.toString()
            R.id.zip -> user?.postcode = s.toString()
            /*R.id.card_cvc -> card?.cardcvc = s.toString()
            R.id.card_n -> card?.cardId = s.toString()*/
           /* R.id.card_exp -> {
                val exp = s.split("/")
                if (exp.size == 2) {
                    card?.cardmonth = exp[0]
                    card?.cardyear = exp[1]
                }
            }*/
        }

        realm.commitTransaction()
    }

    override fun saveEmailPermission(isChecked: Boolean) {

        realm.beginTransaction()
        user?.is_mail_campaign = if (isChecked) 1 else 0
        realm.commitTransaction()
    }

    override fun saveDeliveryTime(time: Int) {

        realm.beginTransaction()
        user?.delivery_preference = time
        realm.commitTransaction()
    }

    override fun saveCountry(countryValue: Int) {

        realm.beginTransaction()
        user?.country = countryValue
        realm.commitTransaction()
    }

    override fun saveState(stateCode: String) {

        realm.beginTransaction()
        user?.state = stateCode
        realm.commitTransaction()
    }

    override fun saveExp(expM: String, expY: String) {

       /* realm.beginTransaction()
        card?.cardmonth = expM
        card?.cardyear = expY
        realm.commitTransaction()*/
    }

    override fun getUserData(): UserProfile {
        return realm.copyFromRealm(user) ?: return UserProfile()
    }

    /*override fun getCreditCard(): CreditCard {
        return realm.copyFromRealm(card) ?: return CreditCard()
    }*/

    override fun getAllCountries(): MutableList<Country> {
        val countries = realm.allObjects(Country::class.java)
        return realm.copyFromRealm(countries) ?: mutableListOf<Country>()
    }

    override fun getCountryByName(name: String): Country? {
        return realm.allObjects(Country::class.java).where().equalTo("name", name).findFirst()
    }

    override fun getStatesByKey(key: String): MutableList<State> {
        val states = realm.allObjects(State::class.java).where().equalTo("key", key).findAll()
        return realm.copyFromRealm(states) ?: mutableListOf<State>()
    }

    override fun getStateByName(name: String): State {
        return realm.allObjects(State::class.java).where().equalTo("name", name).findFirst()
    }

}