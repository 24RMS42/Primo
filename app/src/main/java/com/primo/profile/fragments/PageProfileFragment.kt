/**
 * Changes:
 *
 * - Control toolbar
 * - Handle auto login
 * - Change Profile Tab state
 * - Add show password feature
 * - Show error field in signup validation
 * - Add country and delivery preference field after login
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.profile.fragments

import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.primo.R
import com.primo.auth.fragment.AuthFragment
import com.primo.goods.fragments.GoodsPagerFragment
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
import com.primo.utils.consts.TAB_ADDRESS_FROM_ERROR
import com.primo.utils.consts.TAB_PROFILE_FROM_ERROR
import com.primo.utils.other.Events
import com.primo.utils.other.MultipleTextWatcher
import com.primo.utils.other.RxBus
import com.primo.utils.other.RxEvent
import com.primo.utils.views.DateChooserView
import com.primo.utils.views.GestureRelativeLayout
import com.primo.utils.views.PrefixedEditText
import kotlinx.android.synthetic.main.goods_scanner_fragment.*
import kotlinx.android.synthetic.main.page_profile_fragment.*
import org.w3c.dom.Text
import rx.subscriptions.CompositeSubscription
import java.util.*


class PageProfileFragment : BasePresenterFragment<OrderView, OrderPresenter>(), OrderView, View.OnClickListener,
        GestureRelativeLayout.OnSwipeListener, MultipleTextWatcher, PlaceBottomSheet.ListDialogResult, SearchCountryFragment.ListDialogResult {

    private var gestureLayout: GestureRelativeLayout? = null

    private var firstName: PrefixedEditText? = null
    private var lastName: PrefixedEditText? = null
    private var phoneNumber: PrefixedEditText? = null
    private var email: PrefixedEditText? = null
    private var password: PrefixedEditText? = null
    private var repassword: PrefixedEditText? = null
    private var passwordEye: ImageView? = null
    private var country: PrefixedEditText? = null
    private var dateText: TextView? = null
    private var dateView: DateChooserView? = null
    private var nextBtn: Button? = null

    private var firstNameErr: TextView? = null
    private var lastNameErr: TextView? = null
    private var phoneNumberErr: TextView? = null
    private var emailErr: TextView? = null
    private var passwordErr: TextView? = null
    private var countryErr: TextView? = null

    private var _subscriptions: CompositeSubscription? = null
    private var _rxBus: RxBus? = null

    private var isSigned = false
    private var showPassword = false
    private var signUpState = 4 //normal
    private var action_kind = ""
    private var deviceLanguage = "en"

    override fun onStart() {
        super.onStart()

        _subscriptions = CompositeSubscription();
        _subscriptions?.add(_rxBus?.toObserverable()
                ?.subscribe({
                    Log.d("Test", "profile fragment get event:" + it.key)
                    when (it.key) {

                        Events.SIGNED -> updateData(it.sentObject as Auth)

                        Events.CONFIRMED -> {
                            backToPreviousScreen()
                            MainClass.getRxBus()?.send(RxEvent(Events.CONFIRMED))
                        }

//                        Events.TAB_ADDRESS -> {
//                            onNextButtonClick()
//                        }
                    }
                }));
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (rootView == null) {
            rootView = inflater?.inflate(R.layout.page_profile_fragment, container, false)
            rootView?.isLongClickable = true

            initSwipe()
            init()
            initPresenter()

            changeViewsState()

        }

        return rootView
    }

    private fun init() {

        gestureLayout = rootView?.findViewById(R.id.gesture_layout) as GestureRelativeLayout
        gestureLayout?.onSwipeListener = this

        firstName = rootView?.findViewById(R.id.first_name) as PrefixedEditText
        lastName = rootView?.findViewById(R.id.last_name) as PrefixedEditText
        phoneNumber = rootView?.findViewById(R.id.phone_number) as PrefixedEditText
        email = rootView?.findViewById(R.id.email) as PrefixedEditText
        password = rootView?.findViewById(R.id.password) as PrefixedEditText
        repassword = rootView?.findViewById(R.id.repassword) as PrefixedEditText
        passwordEye = rootView?.findViewById(R.id.passwordEye) as ImageView
        country = rootView?.findViewById(R.id.country) as PrefixedEditText
        dateText = rootView?.findViewById(R.id.date_txt) as TextView
        dateView = rootView?.findViewById(R.id.date_view) as DateChooserView
        nextBtn = rootView?.findViewById(R.id.next_btn) as Button

        firstNameErr = rootView?.findViewById(R.id.first_name_err) as TextView
        lastNameErr= rootView?.findViewById(R.id.last_name_err) as TextView
        phoneNumberErr= rootView?.findViewById(R.id.phone_number_err) as TextView
        emailErr= rootView?.findViewById(R.id.email_err) as TextView
        passwordErr= rootView?.findViewById(R.id.password_err) as TextView
        countryErr = rootView?.findViewById(R.id.country_err) as TextView

        deviceLanguage = getDeviceLanguage()
        setOnClickListener(this, passwordEye, country, nextBtn)

        _rxBus = MainClass.getRxBus()

        //show Profile Tab
        (activity as MainActivity).changeProfileTabState(MainActivity.ProfileTabStates.PROFILE_PAGE)

        val auth = MainClass.getAuth()
        if (!auth.access_token.isEmpty()) {

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
            nextBtn?.setText(MainClass.context.getString(R.string.update))

            //always display address and card page without checking product situation after login
            (activity as MainActivity).changeProfileTabState(MainActivity.SignUpStates.NORMAL)
        } else {
            //get SignUp state
            signUpState = (activity as MainActivity).getSignUpState()
            if (signUpState == MainActivity.SignUpStates.BASIC)
                nextBtn?.setText(MainClass.context.getString(R.string.sign_up))

            //load temp data
            loadProfileData()
        }
    }

    private fun changeViewsState() {

        val auth = MainClass.getAuth()

        if (!auth.access_token.isEmpty()) {

            presenter?.retrieveUserData()

            password?.visibility = View.GONE
            passwordEye?.visibility = View.GONE
            country?.visibility = View.VISIBLE
            dateView?.visibility = View.VISIBLE
            dateText?.visibility = View.VISIBLE
            email?.isEnabled = false

        } else {

            password?.visibility = View.VISIBLE
            passwordEye?.visibility = View.VISIBLE
            country?.visibility = View.GONE
            dateView?.visibility = View.GONE
            dateText?.visibility = View.GONE
            email?.isEnabled = true
        }

        if (arguments != null)
            action_kind = arguments.getString("kind")

        if (action_kind == TAB_PROFILE_FROM_ERROR){ //entire signup validation
            checkValidate()
        }
    }

    override fun onCountrySelected(country: Country) {

        this.country?.setText(country.name)
        this.country?.tag = country.fileName
        presenter?.saveCountry(country.value)

        (activity as MainActivity).user?.country = country.value
    }

    override fun countrySelected(country: Country) {
        this.country?.setText(country.name)
        this.country?.tag = country.fileName
        (activity as MainActivity).user?.country = country.value
    }

    override fun onStateSelected(state: State) {

    }

    override fun stateSelected(state: State) {

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

            presenter?.retrieveUserData()

            changeViewsState()
        }
    }

    private fun saveProfileData(){

        (activity as MainActivity).user?.firstname = firstName?.text.toString()
        (activity as MainActivity).user?.lastname = lastName?.text.toString()
        (activity as MainActivity).user?.phone = phoneNumber?.text.toString()
        (activity as MainActivity).user?.email = email?.text.toString()
        (activity as MainActivity).user?.countryName = country?.text.toString()
        (activity as MainActivity).user?.password = password?.text.toString()
        (activity as MainActivity).user?.repassword = repassword?.text.toString()
    }

    private fun loadProfileData(){

        var userData: TempUserProfile? = null
        userData = (activity as MainActivity)?.user

        firstName?.setText(userData?.firstname ?: "")
        lastName?.setText(userData?.lastname ?:"")
        phoneNumber?.setText(userData?.phone ?: "")
        email?.setText(userData?.email ?: "")
        password?.setText(userData?.password ?: "")
        repassword?.setText(userData?.repassword ?: "")
    }

    fun backToGoodsPage(){
        showFragment(GoodsPagerFragment(), true,
                R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, GoodsPagerFragment::class.java.simpleName)
        (activity as MainActivity).changeProfileTabState(MainActivity.ProfileTabStates.INVISIBLE)
    }

    //show error field
    private fun checkValidate(): Boolean{

        initErrorField()
        var isPassed = true

        if (email?.text.toString().isEmpty()) {
            isPassed = false
            showErrorField(4, MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        }
        if (password?.text.toString().isEmpty()) {
            isPassed = false
            showErrorField(5, MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        }

        if (!email?.text.toString().isEmpty() && !isValidEmail(email?.text.toString())) {
            isPassed = false
            showErrorField(4, MainClass.context.getString(R.string.please_enter_a_valid_email))
        }
        if (!password?.text.toString().isEmpty() && password?.text.toString().length < 6) {
            isPassed = false
            showErrorField(5, MainClass.context.getString(R.string.that_password_is_too_short))
        }

        val auth = MainClass.getAuth()
        if (!auth.access_token.isEmpty()) {
            if (country?.text.toString().isEmpty()) {
                isPassed = false
                showErrorField(6, MainClass.context.getString(R.string.please_fill_in_all_required_fields))
            }
        }

        return isPassed
    }

    private fun initErrorField() {

        firstNameErr?.visibility = View.GONE
        lastNameErr?.visibility = View.GONE
        phoneNumberErr?.visibility = View.GONE
        emailErr?.visibility = View.GONE
        passwordErr?.visibility = View.GONE
        countryErr?.visibility = View.GONE

        firstName?.setBackgroundResource(R.drawable.edit_text_bottom_line)
        lastName?.setBackgroundResource(R.drawable.edit_text_bottom_line)
        phoneNumber?.setBackgroundResource(R.drawable.edit_text_bottom_line)
        email?.setBackgroundResource(R.drawable.edit_text_bottom_line)
        password?.setBackgroundResource(R.drawable.edit_text_bottom_line)
        country?.setBackgroundResource(R.drawable.edit_text_bottom_line)
    }

    private fun showErrorField (field: Int, message: String){

        if (field == 1) {
            firstNameErr?.visibility = View.VISIBLE
            firstNameErr?.text = message
            firstName?.setBackgroundResource(R.drawable.error_border)
        }
        else if (field == 2) {
            lastNameErr?.visibility = View.VISIBLE
            lastNameErr?.text = message
            lastName?.setBackgroundResource(R.drawable.error_border)
        }
        else if (field == 3) {
            phoneNumberErr?.visibility = View.VISIBLE
            phoneNumberErr?.text = message
            phoneNumber?.setBackgroundResource(R.drawable.error_border)
        }
        else if (field == 4) {
            emailErr?.visibility = View.VISIBLE
            emailErr?.text = message
            email?.setBackgroundResource(R.drawable.error_border)
        }
        else if (field == 5) {
            passwordErr?.visibility = View.VISIBLE
            passwordErr?.text = message
            password?.setBackgroundResource(R.drawable.error_border)
        }
        else if (field == 6) {
            countryErr?.visibility = View.VISIBLE
            countryErr?.text = message
            country?.setBackgroundResource(R.drawable.error_border)
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

    }

    override fun updateListShippingAddress(listShippingAddress: ArrayList<UserProfile>){

    }

    override fun updateShippingAddress(){ //used B's method
        showMessage(MainClass.context.getString(R.string.successfully_update))
    }

    override fun updateListCreditCard(listCreditCard: ArrayList<CreditCardData>) {

    }

    override fun updateUserData(userData: UserProfile?) {

        firstName?.setText(userData?.firstname ?: "")
        lastName?.setText(userData?.lastname ?: "")
        phoneNumber?.setText(userData?.phone ?: "")
        email?.setText(userData?.email ?: "")

        val countryList: MutableList<Country> = presenter?.getCountries().orEmpty().toMutableList()
        val countryIndex = countryList.indexOf(Country(value = userData?.country ?: -1))

        (activity as MainActivity).user?.country = userData?.country ?: -1

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
        }

        val time = userData?.delivery_preference ?: 2
        dateView?.setTime(if (time == 0) DateChooserView.time.EVENING else time)

        //fill some value to pass checkExistence
        password?.setText(".........")
        repassword?.setText(".")

        saveProfileData()
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

    }

    override fun onStateSelected() {

    }

    private fun onNextButtonClick(){

        if (checkValidate()) {

            val auth = MainClass.getAuth()
            val token = auth.access_token

            if (signUpState == MainActivity.SignUpStates.BASIC){

                if (!token.isEmpty()) {

                    presenter?.update(phoneNumber?.text.toString(), firstName?.text.toString(), lastName?.text.toString(), "", "", "", country?.text.toString(), "",
                            "", "", dateView?.timePosition.toString(), true)
                } else {

                    val email = email?.text?.trim().toString()
                    val password = password?.text?.trim().toString()

                    presenter?.signUpBasic(email, password,
                            repassword?.text.toString(), phoneNumber?.text.toString(),
                            firstName?.text.toString(), lastName?.text.toString(),
                            "", true)

                    MainClass.saveLoginData(email, password)
                    MainClass.saveSignUpTime()
                }
            }
            else {

                if (token.isEmpty()) {

                    val bundle = Bundle()
                    if (action_kind == TAB_PROFILE_FROM_ERROR)
                        bundle.putString("kind", TAB_ADDRESS_FROM_ERROR)
                    else
                        bundle.putString("kind", "none")

                    val fragObj = PageAddressFragment()
                    fragObj.arguments = bundle

                    showFragment(fragObj, true,
                            R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageAddressFragment::class.java.simpleName)

                    (activity as MainActivity).changeProfileTabState(MainActivity.ProfileTabStates.ADDRESS_PAGE)
                    saveProfileData()

                } else {

                    presenter?.update(phoneNumber?.text.toString(), firstName?.text.toString(), lastName?.text.toString(), "", "", "", country?.text.toString(), "",
                            "", "", dateView?.timePosition.toString(), true)
                }
            }
        }
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.next_btn -> {

                onNextButtonClick()
            }

            R.id.passwordEye -> {

                if (showPassword) {
                    showPassword = false
                    password?.inputType = 129
                    passwordEye?.setBackgroundResource(R.drawable.password_show)
                }
                else {
                    showPassword = true
                    password?.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                    passwordEye?.setBackgroundResource(R.drawable.password_nshow)
                }
            }

            R.id.country -> {
                activity.hideKeyboard()
                //showPickerDialog()
                // == new implementation of country search == //
                showReelPickerDialog()
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
//        super.onSwipeToLeft()
//        val activity = activity
//        activity?.onBackPressed()
        (activity as MainActivity).changeTabbarState(MainActivity.TabbarStates.CART)
        (activity as MainActivity).setPageState(1); // Set Total fragment
        backToGoodsPage()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).changeTabbarState(MainActivity.TabbarStates.SETTING)
        if (MainClass.getAuth().access_token.isEmpty())
            changeToolbarState(MainActivity.ToolbarStates.BACK_BTN_WITH_LOGIN)
        else
            changeToolbarState(MainActivity.ToolbarStates.BACK_BTN_WITH_LOGOUT)
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

