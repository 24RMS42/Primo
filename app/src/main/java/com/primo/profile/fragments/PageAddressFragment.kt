/**
 * Add:
 * - Address Page Fragment
 * - Show error field in signup validation
 * - Integrated shipping address api (get, add, update, update default)
 * - Add firstname, lastname, phone field
 * - Implemented entire signup validation
 * - Add toggle button to set default
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.profile.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.SwitchCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import com.jakewharton.rxbinding.view.enabled
import com.primo.R
import com.primo.auth.fragment.AuthFragment
import com.primo.database.OrderDB
import com.primo.database.OrderDBImpl
import com.primo.main.MainActivity
import com.primo.main.MainClass
import com.primo.network.models.Country
import com.primo.network.models.State
import com.primo.network.new_models.*
import com.primo.profile.mvp.OrderPresenter
import com.primo.profile.mvp.OrderView
import com.primo.profile.mvp.ProfilePresenterImpl
import com.primo.utils.*
import com.primo.utils.base.BasePresenterFragment
import com.primo.utils.consts.*
import com.primo.utils.other.Events
import com.primo.utils.other.MultipleTextWatcher
import com.primo.utils.other.RxBus
import com.primo.utils.other.RxEvent
import com.primo.utils.views.DateChooserView
import com.primo.utils.views.GestureRelativeLayout
import com.primo.utils.views.MonthYearPicker
import com.primo.utils.views.PrefixedEditText
import kotlinx.android.synthetic.main.goods_product_dialog.*
import kotlinx.android.synthetic.main.page_address_fragment.*
import rx.subscriptions.CompositeSubscription
import java.util.*

class PageAddressFragment : BasePresenterFragment<OrderView, OrderPresenter>(), OrderView, View.OnClickListener,
        GestureRelativeLayout.OnSwipeListener, MultipleTextWatcher, PlaceBottomSheet.ListDialogResult, SearchCountryFragment.ListDialogResult {

    private val MIN_PASSWORD_LENGTH = 6
    private var gestureLayout: GestureRelativeLayout? = null

    private var firstName: PrefixedEditText? = null
    private var lastName: PrefixedEditText? = null
    private var phoneNumber: PrefixedEditText? = null
    private var address: PrefixedEditText? = null
    private var address2: PrefixedEditText? = null
    private var city: PrefixedEditText? = null
    private var zip: PrefixedEditText? = null
    private var country: PrefixedEditText? = null
    private var state: PrefixedEditText? = null
    private var dateText: TextView? = null
    private var dateView: DateChooserView? = null
    private var nextBtn: Button? = null
    private var deleteBtn: Button? = null
    private var updateBtn: Button? = null
    private var defaultSwitch: SwitchCompat? = null
    private var defaultSwitchContainer: LinearLayout? = null

    private var firstNameErr: TextView? = null
    private var lastNameErr: TextView? = null
    private var phoneNumberErr: TextView? = null
    private var zipErr: TextView? = null
    private var countryErr: TextView? = null
    private var stateErr: TextView? = null
    private var cityErr: TextView? = null
    private var addressErr: TextView? = null

    private var _subscriptions: CompositeSubscription? = null
    private var _rxBus: RxBus? = null
    private var orderDB: OrderDB? = null

    private var isSigned = false
    private var signUpState = 4 //normal
    private var action_kind = ""
    private var is_default = 0
    private var shipping_address_count = 1
    private var deviceLanguage = "en"

    override fun onStart() {
        super.onStart()

        _subscriptions = CompositeSubscription();
        _subscriptions?.add(_rxBus?.toObserverable()
                ?.subscribe({
                    Log.d("Test", "address fragment get event:" + it.key)
                    when (it.key) {

                        Events.SIGNED -> updateData(it.sentObject as Auth)

                        Events.CONFIRMED -> {
                            backToPreviousScreen()
                            MainClass.getRxBus()?.send(RxEvent(Events.CONFIRMED))
                        }

//                        Events.TAB_CARD -> {
//                            onNextButtonClick()
//                        }
                    }
                }));
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (rootView == null) {
            rootView = inflater?.inflate(R.layout.page_address_fragment, container, false)
            rootView?.isLongClickable = true

            initSwipe()
            init()
            initPresenter()

//            changeViewsState()
//            presenter?.retrieveUserData()
//            presenter?.retrieveCardData()

        }

        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setProfilePageState(MainActivity.ProfileTabStates.ADDRESS_PAGE)

        return rootView
    }

    private fun init() {

        gestureLayout = rootView?.findViewById(R.id.gesture_layout) as GestureRelativeLayout
        gestureLayout?.onSwipeListener = this

        firstName = rootView?.findViewById(R.id.first_name) as PrefixedEditText
        lastName = rootView?.findViewById(R.id.last_name) as PrefixedEditText
        phoneNumber = rootView?.findViewById(R.id.phone_number) as PrefixedEditText
        address = rootView?.findViewById(R.id.address) as PrefixedEditText
        address2 = rootView?.findViewById(R.id.address2) as PrefixedEditText
        city = rootView?.findViewById(R.id.city) as PrefixedEditText
        country = rootView?.findViewById(R.id.country) as PrefixedEditText
        state = rootView?.findViewById(R.id.state) as PrefixedEditText
        zip = rootView?.findViewById(R.id.zip) as PrefixedEditText
        dateText = rootView?.findViewById(R.id.date_txt) as TextView
        dateView = rootView?.findViewById(R.id.date_view) as DateChooserView
        nextBtn = rootView?.findViewById(R.id.next_btn) as Button
        deleteBtn = rootView?.findViewById(R.id.delete_btn) as Button
        updateBtn = rootView?.findViewById(R.id.update_btn) as Button
        defaultSwitch = rootView?.findViewById(R.id.default_switch) as SwitchCompat
        defaultSwitchContainer = rootView?.findViewById(R.id.default_switch_container) as LinearLayout

        firstNameErr = rootView?.findViewById(R.id.first_name_err) as TextView
        lastNameErr= rootView?.findViewById(R.id.last_name_err) as TextView
        phoneNumberErr= rootView?.findViewById(R.id.phone_number_err) as TextView
        zipErr = rootView?.findViewById(R.id.zip_err) as TextView
        countryErr = rootView?.findViewById(R.id.country_err) as TextView
        stateErr = rootView?.findViewById(R.id.state_err) as TextView
        cityErr = rootView?.findViewById(R.id.city_err) as TextView
        addressErr = rootView?.findViewById(R.id.address_err) as TextView

        state?.isEnabled = false

        phoneNumber?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                activity.hideKeyboard()
                val handler = Handler()
                handler.postDelayed({
                    country?.performClick()
                }, 300)
                return@OnEditorActionListener true
            }
            false
        })

        deviceLanguage = getDeviceLanguage()

        if (deviceLanguage == "ja"){

            zip?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    presenter?.postCode(zip?.text.toString())
                    return@OnEditorActionListener true
                }
                false
            })
        }
        else {

            zip?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    activity.hideKeyboard()
                    val handler = Handler()
                    handler.postDelayed({
                        state?.performClick()
                    }, 300)
                    return@OnEditorActionListener true
                }
                false
            })
        }

        setOnClickListener(this, country, state, nextBtn, deleteBtn, updateBtn)
        defaultSwitch?.setOnCheckedChangeListener(onCheckedChanged())

        _rxBus = MainClass.getRxBus()

        if (arguments != null)
            action_kind = arguments.getString("kind")

        if (action_kind == SHIPPING_ADDRESS_ADD) {
            updateBtn?.text = MainClass.context.getString(R.string.add)
        }
        else if (action_kind == SHIPPING_ADDRESS_UPDATE) {
            updateBtn?.text = MainClass.context.getString(R.string.update)
            deleteBtn?.visibility = View.VISIBLE

            is_default = arguments.getInt(IS_DEFAULT)
            shipping_address_count = arguments.getInt(SHIPPING_ADDRESS_COUNT, 1)
            //load temp data
            loadProfileData()
        }
        else if (action_kind == TAB_ADDRESS_FROM_ERROR) { //entire signup validation
            loadProfileData()
            checkValidate()
            nextBtn?.text = MainClass.context.getString(R.string.next)
        }
        else {
            nextBtn?.text = MainClass.context.getString(R.string.next)
            loadProfileData()
        }

        //get SignUp state
        signUpState = (activity as MainActivity).getSignUpState()
        if (signUpState == MainActivity.SignUpStates.NOCC)
            nextBtn?.text = MainClass.context.getString(R.string.sign_up)

        //set visibility
        if (MainClass.getAuth().access_token.isEmpty()) {
            nextBtn?.visibility = View.VISIBLE
            updateBtn?.visibility = View.GONE
            defaultSwitchContainer?.visibility = View.GONE
            deleteBtn?.visibility = View.GONE
        }else{
            dateText?.visibility = View.GONE
            dateView?.visibility = View.GONE
            updateBtn?.visibility = View.VISIBLE
            defaultSwitchContainer?.visibility = View.VISIBLE
        }
    }

    private fun onCheckedChanged(): CompoundButton.OnCheckedChangeListener {
        return CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            when (buttonView.id) {
                R.id.default_switch -> {

                    if (!isChecked && is_default == 1){

                        if (shipping_address_count == 1)
                            showMessage(MainClass.context.getString(R.string.must_have_default_address))
                        //== Show message when user unset default, if user has multiple addresses or credit cards, 02/02/2017 ==//
                        else if (shipping_address_count > 1)
                            showMessage(MainClass.context.getString(R.string.please_set_another_address_as_default))

                        defaultSwitch?.isChecked = true
                    }
                }
            }
        }
    }

    private fun changeViewsState() {

        val auth = MainClass.getAuth()

        if (!auth.access_token.isEmpty()) {

        } else {

        }
    }

    override fun onCountrySelected(country: Country) {

        if (!this.country?.text.toString().equals(country.name)) {
            state?.setText("")
            presenter?.saveState("")
        }

        state?.isEnabled = true
        this.country?.setText(country.name)
        this.country?.tag = country.fileName
        presenter?.saveCountry(country.value)

        (activity as MainActivity).user?.country = country.value

        //this.state?.postDelayed({ this.state?.performClick() }, 700)
        zip?.isFocusableInTouchMode = true
        zip?.requestFocus()
        showKeyboard(zip)
    }

    override fun countrySelected(country: Country) {

        state?.isEnabled = true
        this.country?.setText(country.name)
        this.country?.tag = country.fileName
        presenter?.saveCountry(country.value)

        (activity as MainActivity).user?.country = country.value

        //this.state?.postDelayed({ this.state?.performClick() }, 700)
        zip?.isFocusableInTouchMode = true
        zip?.requestFocus()
        showKeyboard(zip)
    }

    override fun onStateSelected(state: State) {
        this.state?.setText(state.name)
        presenter?.saveState(state.name)

        city?.isFocusableInTouchMode = true
        city?.requestFocus()
        showKeyboard(city)
    }

    override fun stateSelected(state: State) {
        this.state?.setText(state.name)
        presenter?.saveState(state.name)

        city?.isFocusableInTouchMode = true
        city?.requestFocus()
        showKeyboard(city)
    }

    override fun backToPreviousScreen() {
        activity?.let {
            rootView?.postDelayed({ activity.onBackPressed() }, 100)
        }
    }

    override fun initPresenter() {
        presenter = ProfilePresenterImpl(this)
    }

    private fun updateData(auth: Auth?) {

        if (auth != null && !auth.access_token.isEmpty()) {
            isSigned = true

            //presenter?.retrieveUserData()
            //presenter?.retrieveCardData()
            //changeViewsState()
        }
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

        //country?.setText(MainClass.context.getString(R.string.japan)) // should set English Japan name to find index of Japan in country list
        city?.setText(addressData?.city)
        state?.setText(addressData?.prefecture)
        address?.setText(addressData?.town)
    }

    override fun updateListShippingAddress(listShippingAddress: ArrayList<UserProfile>){
        //added to go to back when set default or delete from detail page
        val activity = activity
        activity?.onBackPressed()
    }

    override fun updateShippingAddress(){
        val activity = activity
        activity?.onBackPressed()
    }

    override fun updateListCreditCard(listCreditCard: ArrayList<CreditCardData>) {

    }

    override fun updateUserData(userData: UserProfile?) {

        address?.setText(userData?.address ?: "")
        city?.setText(userData?.city ?: "")
        zip?.setText(userData?.postcode ?: "")

        val countryList: MutableList<Country> = presenter?.getCountries().orEmpty().toMutableList()
        Log.d("Test", "country list after signin:" + countryList)
        val countryIndex = countryList.indexOf(Country(value = userData?.country ?: -1))

        if (countryIndex >= 0) {

            val countryModel = countryList[countryIndex]
            country?.setText(countryModel.name)
            country?.tag = countryModel.fileName

            val stateList = presenter?.getStatesByKey(countryModel.fileName).orEmpty()
            presenter?.saveCountry(countryModel.value)

            val stateIndex = stateList.indexOf(State(name = userData?.state.orEmpty()))
            presenter?.saveState(userData?.state.orEmpty())
            if (stateIndex >= 0) {
                state?.setText(stateList[stateIndex].name)
            }
        }

        val time = userData?.delivery_preference ?: 2

        dateView?.setTime(if (time == 0) DateChooserView.time.EVENING else time)
    }

    override fun updateCardData(card: CreditCard?) {

    }

    override fun onSigned() {
        //TODO DELETE
        activity?.onBackPressed()
        MainClass.getRxBus()?.send(RxEvent(Events.SIGNED))
    }

    override fun onSignUped() {
        showFragment(AuthFragment(), true, R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right)

        //save user language
        MainClass.saveUserLanguage()
    }

    override fun onCountrySelected() {
        state?.setText("")
    }

    override fun onStateSelected() {
        city?.isFocusableInTouchMode = true
        city?.requestFocus()
        showKeyboard(city)
    }

    private fun saveAddressData(){

        (activity as MainActivity).user?.firstname = firstName?.text.toString()
        (activity as MainActivity).user?.lastname = lastName?.text.toString()
        (activity as MainActivity).user?.phone = phoneNumber?.text.toString()
        (activity as MainActivity).user?.postcode = zip?.text.toString()
        (activity as MainActivity).user?.state = state?.text.toString()
        (activity as MainActivity).user?.countryName = country?.text.toString()
        (activity as MainActivity).user?.city = city?.text.toString()
        (activity as MainActivity).user?.address = address?.text.toString()
        (activity as MainActivity).user?.address2 = address2?.text.toString()
        (activity as MainActivity).user?.delivery_preference = dateView?.timePosition
    }

    private fun loadProfileData() {

        var userData: TempUserProfile? = null
        userData = (activity as MainActivity)?.user

        firstName?.setText(userData?.firstname ?: "")
        lastName?.setText(userData?.lastname ?:"")
        phoneNumber?.setText(userData?.phone ?: "")
        address?.setText(userData?.address ?: "")
        city?.setText(userData?.city ?: "")
        zip?.setText(userData?.postcode ?: "")

        orderDB = OrderDBImpl()
        val countryList: MutableList<Country> = getCountries().orEmpty().toMutableList()
        Log.d("Test", "country list:" + countryList)

        val countryIndex = countryList.indexOf(Country(value = userData?.country ?: -1))

        if (countryIndex >= 0) {

            val countryModel = countryList[countryIndex]

            if (deviceLanguage == "en")
                country?.setText(countryModel.name)
            else if (deviceLanguage == "ja")
                country?.setText(countryModel.name_ja)
            else if (deviceLanguage == "ch")
                country?.setText(countryModel.name_ch)
            else if (deviceLanguage == "cht")
                country?.setText(countryModel.name_cht)

            country?.tag = countryModel.fileName

            //TODO
            /*val stateList = getStatesByKey(countryModel.fileName).orEmpty()
            presenter?.saveCountry(countryModel.value)

            val stateIndex = stateList.indexOf(State(name = userData?.state.orEmpty()))
            presenter?.saveState(userData?.state.orEmpty())
            if (stateIndex >= 0) {
                state?.setText(stateList[stateIndex].name)
                state?.isEnabled = true
            }*/

            state?.setText(userData?.state.orEmpty())
            state?.isEnabled = true
        }

        val time = userData?.delivery_preference ?: -1
        dateView?.setTime(if (time == 0) DateChooserView.time.EVENING else time)

        defaultSwitch?.isChecked = if (is_default == 1) true else false

    }

    fun getCountries(): List<Country>? {
        return orderDB?.getAllCountries()?.orEmpty()
    }

    fun getStatesByKey(key: String): MutableList<State>? {
        return orderDB?.getStatesByKey(key)
    }

    //show error field
    private fun checkValidate(): Boolean{

        initErrorField()
        var isPassed = true

        if (zip?.text.toString().isEmpty()) {
            isPassed = false
            showErrorField(1, MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        }
        if (country?.text.toString().isEmpty()) {
            isPassed = false
            showErrorField(2, MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        }
        if (state?.text.toString().isEmpty()) {
            isPassed = false
            showErrorField(3, MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        }
        if (city?.text.toString().isEmpty()) {
            isPassed = false
            showErrorField(4, MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        }
        if (address?.text.toString().isEmpty()) {
            isPassed = false
            showErrorField(5, MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        }
        if (firstName?.text.toString().isEmpty()) {
            isPassed = false
            showErrorField(6, MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        }
        if (lastName?.text.toString().isEmpty()) {
            isPassed = false
            showErrorField(7, MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        }
        if (phoneNumber?.text.toString().isEmpty()) {
            isPassed = false
            showErrorField(8, MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        }

        return isPassed
    }

    private fun initErrorField() {

        firstNameErr?.visibility = View.GONE
        lastNameErr?.visibility = View.GONE
        phoneNumberErr?.visibility = View.GONE
        zipErr?.visibility = View.GONE
        countryErr?.visibility = View.GONE
        stateErr?.visibility = View.GONE
        cityErr?.visibility = View.GONE
        addressErr?.visibility = View.GONE

        firstName?.setBackgroundResource(R.drawable.edit_text_bottom_line)
        lastName?.setBackgroundResource(R.drawable.edit_text_bottom_line)
        phoneNumber?.setBackgroundResource(R.drawable.edit_text_bottom_line)
        zip?.setBackgroundResource(R.drawable.edit_text_bottom_line)
        country?.setBackgroundResource(R.drawable.edit_text_bottom_line)
        state?.setBackgroundResource(R.drawable.edit_text_bottom_line)
        city?.setBackgroundResource(R.drawable.edit_text_bottom_line)
        address?.setBackgroundResource(R.drawable.edit_text_bottom_line)
    }

    private fun showErrorField (field: Int, message: String){

        if (field == 1) {
            zipErr?.visibility = View.VISIBLE
            zipErr?.text = message
            zip?.setBackgroundResource(R.drawable.error_border)
        }
        else if (field == 2) {
            countryErr?.visibility = View.VISIBLE
            countryErr?.text = message
            country?.setBackgroundResource(R.drawable.error_border)
        }
        else if (field == 3) {
            stateErr?.visibility = View.VISIBLE
            stateErr?.text = message
            state?.setBackgroundResource(R.drawable.error_border)
        }
        else if (field == 4) {
            cityErr?.visibility = View.VISIBLE
            cityErr?.text = message
            city?.setBackgroundResource(R.drawable.error_border)
        }
        else if (field == 5) {
            addressErr?.visibility = View.VISIBLE
            addressErr?.text = message
            address?.setBackgroundResource(R.drawable.error_border)
        }
        else if (field == 6) {
            firstNameErr?.visibility = View.VISIBLE
            firstNameErr?.text = message
            firstName?.setBackgroundResource(R.drawable.error_border)
        }
        else if (field == 7) {
            lastNameErr?.visibility = View.VISIBLE
            lastNameErr?.text = message
            lastName?.setBackgroundResource(R.drawable.error_border)
        }
        else if (field == 8) {
            phoneNumberErr?.visibility = View.VISIBLE
            phoneNumberErr?.text = message
            phoneNumber?.setBackgroundResource(R.drawable.error_border)
        }
    }

    private fun isUserProfileValid(email: String, password: String): Boolean {

        if (isFieldsEmpty(email, password)) {

        } else if (!isValidEmail(email)) {

        } else if (password.length < MIN_PASSWORD_LENGTH) {

        } else
            return true

        return false
    }

    private fun onNextButtonClick(){

        saveAddressData()
        if (checkValidate()) {

            val auth = MainClass.getAuth()
            val token = auth.access_token

            val userData: TempUserProfile?
            userData = (activity as MainActivity).user

            if (token.isEmpty()) { //before signin, entire signup validation

                if (signUpState == MainActivity.SignUpStates.NOCC){

                    val email = userData!!.email
                    val password = userData!!.password

                    if (!isUserProfileValid(email, password)) {
                        val bundle = Bundle()
                        bundle.putString("kind", TAB_PROFILE_FROM_ERROR)
                        val fragObj = PageProfileFragment()
                        fragObj.arguments = bundle

                        showFragment(fragObj, true,
                                R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageProfileFragment::class.java.simpleName)
                    }
                    else {
                        presenter?.signUpNOCC(email, password,
                                userData!!.repassword, userData!!.phone,
                                userData!!.firstname, userData!!.lastname,
                                address?.text.toString(), city?.text.toString(), state?.text.toString(),
                                country?.text.toString(), zip?.text.toString(),
                                dateView?.timePosition.toString(), true)

                        MainClass.saveLoginData(email, password)
                        MainClass.saveSignUpTime()
                    }

                } else {

                    val bundle = Bundle()
                    bundle.putString("kind", "none")
                    val fragObj = PageCardFragment()
                    fragObj.arguments = bundle

                    showFragment(fragObj, true,
                            R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageCardFragment::class.java.simpleName)
                    (activity as MainActivity).changeProfileTabState(MainActivity.ProfileTabStates.CARD_PAGE)
                }

            } else { //after signin

                if (action_kind == SHIPPING_ADDRESS_ADD){
                    presenter?.addShippingAddress(phoneNumber?.text.toString(), firstName?.text.toString(), lastName?.text.toString(),
                            address?.text.toString(), city?.text.toString(), state?.text.toString(), userData!!.country, zip?.text.toString(), if (defaultSwitch!!.isChecked)  1 else 0)
                }
                else if (action_kind == SHIPPING_ADDRESS_UPDATE){
                    confirmDialogShow(3)
                }
            }
        }
    }

    fun confirmDialogShow(dialogID: Int) {

        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setTitle(MainClass.context.getString(R.string.infromation))
        alertDialogBuilder.setCancelable(false)

        if (dialogID == 1)
            alertDialogBuilder.setMessage(MainClass.context.getString(R.string.are_you_sure_delete_shipping))
        else if (dialogID == 2)
            alertDialogBuilder.setMessage(MainClass.context.getString(R.string.are_you_sure_change_default_shipping))
        else if (dialogID == 3)
            alertDialogBuilder.setMessage(MainClass.context.getString(R.string.are_you_sure_update_address))

        alertDialogBuilder.setPositiveButton("Yes") { dialog, id ->

            if (dialogID == 1)
                presenter?.deleteShippingAddress(arguments.getString(SHIPPING_ID))

            else if (dialogID == 2)
                presenter?.updateDefaultShippingAddress(arguments.getString(SHIPPING_ID))

            else if (dialogID == 3){

                val userData: TempUserProfile?
                userData = (activity as MainActivity).user

                presenter?.updateShippingAddress(arguments.getString(SHIPPING_ID), phoneNumber?.text.toString(), firstName?.text.toString(), lastName?.text.toString(),
                        address?.text.toString(), city?.text.toString(), state?.text.toString(), userData!!.country, zip?.text.toString(), if (defaultSwitch!!.isChecked)  1 else 0)
            }

        }

        alertDialogBuilder.setNegativeButton("No") { dialog, id ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.country -> {
                activity.hideKeyboard()
                //showPickerDialog()
                // == new implementation of country search == //
                showReelPickerDialog()
            }

            R.id.state -> {
                activity.hideKeyboard()
                //showPickerDialog(country?.tag.toString())
                // == new implementation of state search == //
                showReelPickerDialog(country?.tag.toString())
            }

            R.id.next_btn -> {
                onNextButtonClick()
            }

            R.id.update_btn -> {
                onNextButtonClick()
            }

            R.id.delete_btn -> {
                if (is_default == 1)
                    showMessage(MainClass.context.getString(R.string.cannot_delete_default_address))
                else
                    confirmDialogShow(1)
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

    private fun showReelPickerDialog(country: String? = null) {

        val dialogFragment = SearchCountryFragment.newInstance(country)
        val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
        dialogFragment.setTargetFragment(this, 0)
        fragmentTransaction.add(dialogFragment, null)
        fragmentTransaction.commitAllowingStateLoss()
    }

    override fun onSwipeToLeft() {
        super.onSwipeToLeft()

//        val activity = activity
//        activity?.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        if (MainClass.getAuth().access_token.isEmpty())
            changeToolbarState(MainActivity.ToolbarStates.DEFAULT)
        else
            changeToolbarState(MainActivity.ToolbarStates.BACK_BTN)
    }

    override fun onPause() {
        super.onPause()
        changeToolbarState(MainActivity.ToolbarStates.DEFAULT)
    }

    override fun onStop() {
        super.onStop()
        _subscriptions?.clear()
    }

    override fun onDetach() {
        super.onDetach()
        if (isSigned)
            MainClass.getRxBus()?.send(RxEvent(Events.SIGNED))
    }
}

