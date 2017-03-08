/**
 * Changes:
 *
 * - Add set country page
 * - New implement of country search
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.profile.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.primo.R
import com.primo.network.models.Country
import com.primo.network.models.State
import com.primo.network.new_models.*
import com.primo.profile.mvp.OrderPresenter
import com.primo.profile.mvp.OrderView
import com.primo.profile.mvp.ProfilePresenterImpl
import com.primo.utils.*
import com.primo.utils.base.BasePresenterFragment
import com.primo.utils.other.RxEvent
import java.util.*
import android.widget.EditText
import com.primo.goods.fragments.GoodsPagerFragment
import com.primo.main.MainActivity
import com.primo.main.MainClass
import search.reel.android.RAMReel

class SetCountryFragment : BasePresenterFragment<OrderView, OrderPresenter>(), OrderView, View.OnClickListener, PlaceBottomSheet.ListDialogResult{

    private var country: EditText? = null
    private var updateBtn: Button? = null
    private var selectedCountry = -1
    private var language = "en"
    private var name_key = "name"

    override fun onStart() {
        super.onStart()

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (rootView == null) {
            rootView = inflater?.inflate(R.layout.set_country_fragment, container, false)
            rootView?.isLongClickable = true

            initSwipe()
            init()
            initPresenter()
        }

        return rootView
    }

    private fun init() {

        country = rootView?.findViewById(R.id.country) as EditText
        updateBtn = rootView?.findViewById(R.id.updateBtn) as Button

        setOnClickListener(this, country, updateBtn)

        //new implementation of country search
        language = getDeviceLanguage()

        if (language == "en")
            name_key = "name"
        else if (language == "ja")
            name_key = "name_ja"
        else if (language == "ch")
            name_key = "name_ch"
        else if (language == "cht")
            name_key = "name_cht"

        val realm = io.realm.Realm.getDefaultInstance()
        val countryList: MutableList<Country> = realm.copyFromRealm(realm.allObjects(Country::class.java)).orEmpty().toMutableList()
        val picker = rootView?.findViewById(R.id.picker) as RAMReel

        val newCountryList = getNewCountryList(countryList)
        val size = newCountryList.size
        val array = arrayOfNulls<String>(size)
        Arrays.fill(array, "")

        for (i in 0..size - 1) {
            array[i] = newCountryList[i].name
        }
        picker.setValues(array)
        picker.setOnItemClickListener { text ->
            val countryCode = realm.allObjects(Country::class.java).where().equalTo(name_key, text).findFirst()?.value ?: -1
            selectedCountry = countryCode
            if (selectedCountry != -1)
                activity.hideKeyboard()
        }
    }

    override fun onCountrySelected(country: Country) {

        this.country?.setText(country.name)
        selectedCountry = country.value
    }

    override fun onStateSelected(state: State) {

    }

    override fun backToPreviousScreen() {
        val activity = activity
        if (activity != null && activity is MainActivity) {

            activity.changeTabbarState(MainActivity.TabbarStates.CAMERA)
            activity.setPageState(0)
            activity.changeProfileTabState(MainActivity.ProfileTabStates.INVISIBLE)
            showFragment(GoodsPagerFragment(), true,
                    R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, GoodsPagerFragment::class.java.simpleName)
        }
    }

    override fun initPresenter() {
        presenter = ProfilePresenterImpl(this)
    }

    override fun showProgress() {
        swipe?.post({ swipe?.isRefreshing = true })
    }

    override fun hideProgress() {
        swipe?.post({ swipe?.isRefreshing = false })
    }

    override fun showMessage(message: String?, event: RxEvent?) {
        showDialog(message)
    }

    override fun displayErrorMessage(message : String?, code: Int?, event: RxEvent?){
        showErrorDialog(message, code)
    }

    override fun getAddressData(addressData: Address?){

    }

    override fun updateListShippingAddress(listShippingAddress: ArrayList<UserProfile>){

    }

    override fun updateShippingAddress(){

    }

    override fun updateListCreditCard(listCreditCard: ArrayList<CreditCardData>) {

    }

    override fun updateUserData(userData: UserProfile?) {

    }

    override fun updateCardData(card: CreditCard?) {

    }

    override fun onSigned() {

    }

    override fun onSignUped() {

    }

    override fun onCountrySelected() {

    }

    override fun onStateSelected() {

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.updateBtn -> {
                if (selectedCountry == -1)
                    showMessage(MainClass.context.getString(R.string.set_your_country))
                else
                    presenter?.setCountry(selectedCountry)
            }

            R.id.country -> {
                activity.hideKeyboard()
                showPickerDialog()
            }
        }
    }

    private fun showPickerDialog(country: String? = null) {

        val dialogFragment = PlaceBottomSheet.newInstance(country)
        val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
        dialogFragment.setTargetFragment(this, 0)
        fragmentTransaction.add(dialogFragment, null)
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun getNewCountryList(countryList: List<Country>): MutableList<Country>{

        val newCountryList = mutableListOf<Country>()
        val size = countryList.size

        for (i in 0..size - 1) {

            val country: Country
            var name = ""
            val name_en = countryList[i].name // it is used to sort as it is English name

            if (language == "ja") {
                name = countryList[i].name_ja
            }
            else if (language == "en") {
                name = countryList[i].name
            }
            else if (language == "ch") {
                name = countryList[i].name_ch
            }
            else if (language == "cht")
                name = countryList[i].name_cht

            val code = countryList[i].code
            val value = countryList[i].value
            val continent = countryList[i].continent
            val fileName = countryList[i].fileName

            country = Country(name, value, code, continent, fileName, name_en, name, name)
            newCountryList.add(country)
        }

        return newCountryList
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDetach() {
        super.onDetach()
    }
}