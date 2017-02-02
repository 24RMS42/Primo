/**
 * Add:
 * - Card Page Fragment
 * - Show error field in signup validation
 * - Integrated credit card api (get, add, update, update default)
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
import android.text.InputType
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.jakewharton.rxbinding.view.enabled
import com.primo.R
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
import kotlinx.android.synthetic.main.page_card_fragment.*
import rx.subscriptions.CompositeSubscription
import java.util.*
import com.primo.auth.fragment.AuthFragment
import android.view.*
import android.widget.LinearLayout
import android.view.LayoutInflater

class PageCardFragment : BasePresenterFragment<OrderView, OrderPresenter>(), OrderView, View.OnClickListener,
        GestureRelativeLayout.OnSwipeListener, MultipleTextWatcher, PlaceBottomSheet.ListDialogResult {

    private val CARD_LENGTH = 14
    private val CARD_CVC_LENGTH = 3
    private val MIN_PASSWORD_LENGTH = 6

    private var gestureLayout: GestureRelativeLayout? = null

    private var cardN: PrefixedEditText? = null
    private var cardHolder: PrefixedEditText? = null
    private var cardExp: PrefixedEditText? = null
    private var cardCvc: PrefixedEditText? = null

    private var dateView: DateChooserView? = null
    private var emailTxt: TextView? = null
    private var emailSwitch: SwitchCompat? = null
    private var checkoutBtn: Button? = null
    private var termsTxt: TextView? = null
    private var deleteBtn: Button? = null
    private var updateBtn: Button? = null
    private var defaultSwitch: SwitchCompat? = null
    private var defaultSwitchContainer: LinearLayout? = null

    private var cardNErr: TextView? = null
    private var cardHolderErr: TextView? = null
    private var cardExpErr: TextView? = null
    private var cardCvcErr: TextView? = null
    private var passwordEye: ImageView? = null
    private var cvcEye: ImageView? = null

    private var _subscriptions: CompositeSubscription? = null
    private var _rxBus: RxBus? = null

    private var isSigned = false
    private var action_kind = ""
    private var card_year = ""
    private var card_month = ""
    private var is_default = 0
    private var showPassword = false

    override fun onStart() {
        super.onStart()

        _subscriptions = CompositeSubscription();
        _subscriptions?.add(_rxBus?.toObserverable()
                ?.subscribe({
                    Log.d("Test", "card fragment get event:" + it.key)
                    when (it.key) {

                        Events.SIGNED -> updateData(it.sentObject as Auth)

                        Events.CONFIRMED -> {
                            backToPreviousScreen()
                            MainClass.getRxBus()?.send(RxEvent(Events.CONFIRMED))
                        }

                        //Fixed inactive address submenu clicking
//                        Events.TAB_ADDRESS_FROM_CARD -> {
//                            onPreviousButtonClick()
//                        }
                    }
                }));
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (rootView == null) {
            rootView = inflater?.inflate(R.layout.page_card_fragment, container, false)
            rootView?.isLongClickable = true

            initSwipe()
            init()
            initPresenter()

            changeViewsState()
//            presenter?.retrieveUserData()
//            presenter?.retrieveCardData()

        }

        (activity as MainActivity).showToolbar(true)
        return rootView
    }

    private fun init() {

        gestureLayout = rootView?.findViewById(R.id.gesture_layout) as GestureRelativeLayout
        gestureLayout?.onSwipeListener = this

        cardN = rootView?.findViewById(R.id.card_n) as PrefixedEditText
        cardHolder = rootView?.findViewById(R.id.card_holder) as PrefixedEditText
        cardExp = rootView?.findViewById(R.id.card_exp) as PrefixedEditText
        cardCvc = rootView?.findViewById(R.id.card_cvc) as PrefixedEditText

        dateView = rootView?.findViewById(R.id.date_view) as DateChooserView
        emailTxt = rootView?.findViewById(R.id.email_txt) as TextView
        emailSwitch = rootView?.findViewById(R.id.email_switch) as SwitchCompat
        checkoutBtn = rootView?.findViewById(R.id.checkout_btn) as Button
        termsTxt = rootView?.findViewById(R.id.terms_txt) as TextView
        deleteBtn = rootView?.findViewById(R.id.delete_btn) as Button
        updateBtn = rootView?.findViewById(R.id.update_btn) as Button
        defaultSwitch = rootView?.findViewById(R.id.default_switch) as SwitchCompat
        defaultSwitchContainer = rootView?.findViewById(R.id.default_switch_container) as LinearLayout

        cardNErr = rootView?.findViewById(R.id.card_n_err) as TextView
        cardHolderErr= rootView?.findViewById(R.id.card_holder_err) as TextView
        cardExpErr = rootView?.findViewById(R.id.card_exp_err) as TextView
        cardCvcErr = rootView?.findViewById(R.id.card_cvc_err) as TextView
        passwordEye = rootView?.findViewById(R.id.passwordEye) as ImageView
        cvcEye= rootView?.findViewById(R.id.cvcEye) as ImageView

        cardN?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                activity.hideKeyboard()
                showDatePicker()
                return@OnEditorActionListener true
            }
            false
        })

        setOnClickListener(this, checkoutBtn, termsTxt, cardExp, deleteBtn, updateBtn, passwordEye, cvcEye)
        defaultSwitch?.setOnCheckedChangeListener(onCheckedChanged())

        _rxBus = MainClass.getRxBus()

    }

    private fun onCheckedChanged(): CompoundButton.OnCheckedChangeListener {
        return CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            when (buttonView.id) {
                R.id.default_switch -> {

                    if (!isChecked && is_default == 1){
                        showMessage(MainClass.context.getString(R.string.must_have_default_card))
                        defaultSwitch?.isChecked = true
                    }
                }
            }
        }
    }

    private fun changeViewsState() {

        (activity as MainActivity).setProfilePageState(MainActivity.ProfileTabStates.CARD_PAGE)

        val auth = MainClass.getAuth()

        if (!auth.access_token.isEmpty()) {

            cardN?.isEnabled = false
            cardCvc?.isEnabled = false
            //emailTxt?.text = getString(R.string.update_text)
            emailTxt?.visibility = View.GONE
            emailSwitch?.visibility = View.GONE //View.VISIBLE
            termsTxt?.visibility = View.GONE

            checkoutBtn?.visibility = View.GONE
            updateBtn?.visibility = View.VISIBLE
            defaultSwitchContainer?.visibility = View.VISIBLE

        } else{
            checkoutBtn?.visibility = View.VISIBLE
            updateBtn?.visibility = View.GONE
            defaultSwitchContainer?.visibility = View.GONE
            deleteBtn?.visibility = View.GONE
        }

        if (arguments != null)
            action_kind = arguments.getString("kind")

        if (action_kind == CREDIT_CARD_ADD) {
            updateBtn?.text = MainClass.context.getString(R.string.add)
            cardN?.isEnabled = true
            cardCvc?.isEnabled = true

            var userData: TempUserProfile? = null
            userData = (activity as MainActivity)?.user

            var card_holder = ""
            if (!(userData?.firstname!!.isEmpty() && userData?.lastname!!.isEmpty())) {
                card_holder = userData?.firstname + " " + userData?.lastname
                cardHolder?.setText(card_holder)
            }
        }
        else if (action_kind == CREDIT_CARD_UPDATE) {

            cardHolder?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    activity.hideKeyboard()
                    showDatePicker()
                    return@OnEditorActionListener true
                }
                false
            })

            updateBtn?.text = MainClass.context.getString(R.string.update)
            deleteBtn?.visibility = View.VISIBLE

            is_default = arguments.getInt(IS_DEFAULT)
            //load temp data
            loadCardData()
            showPassword = true
        }
        else {
            checkoutBtn?.text = MainClass.context.getString(R.string.sign_up)

            var userData: TempUserProfile? = null
            userData = (activity as MainActivity)?.user

            var card_holder = ""
            if (!(userData?.firstname!!.isEmpty() && userData?.lastname!!.isEmpty())) {
                card_holder = userData?.firstname + " " + userData?.lastname
                cardHolder?.setText(card_holder)
            }
        }
    }

    override fun onCountrySelected(country: Country) {

    }

    override fun onStateSelected(state: State) {

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

    private fun saveCardData(){

        (activity as MainActivity).user?.cardN = cardN?.text.toString()
        (activity as MainActivity).user?.cardname = cardHolder?.text.toString()
        (activity as MainActivity).user?.cardyear = card_year
        (activity as MainActivity).user?.cardmonth = card_month
        (activity as MainActivity).user?.lastFour = cardCvc?.text.toString()
    }

    private fun loadCardData(){

        var userData: TempUserProfile? = null
        userData = (activity as MainActivity)?.user

        card_year = userData!!.cardyear
        card_month = userData!!.cardmonth
        cardN?.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

        cardN?.setText("************${userData?.lastFour}")
        cardHolder?.setText(userData?.cardname)
        cardExp?.setText(dateFormat(card_month, card_year))
        cardCvc?.setText(userData?.lastFour)

        defaultSwitch?.isChecked = if (is_default == 1) true else false
    }

    //show error field
    private fun checkValidate(): Boolean{

        initErrorField()
        var isPassed = true

        if (cardN?.text.toString().isEmpty()) {
            isPassed = false
            showErrorField(1, MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        }
        if (cardHolder?.text.toString().isEmpty()) {
            isPassed = false
            showErrorField(2, MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        }
        if (cardExp?.text.toString().isEmpty()) {
            isPassed = false
            showErrorField(3, MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        }
        if (cardCvc?.text.toString().isEmpty()) {
            isPassed = false
            showErrorField(4, MainClass.context.getString(R.string.please_fill_in_all_required_fields))
        }

        if (!cardN?.text.toString().isEmpty() && cardN?.text.toString().length < CARD_LENGTH){
            isPassed = false
            showErrorField(1, MainClass.context.getString(R.string.card_number_should_contain))
        }
        if (!cardCvc?.text.toString().isEmpty() && cardCvc?.text.toString().length < CARD_CVC_LENGTH){
            isPassed = false
            showErrorField(4, MainClass.context.getString(R.string.your_cvc_number_should_contain))
        }
        if (!cardExp?.text.toString().isEmpty()) {

            var cardYear = ""
            var cardMonth = ""

            val exp = cardExp?.text.toString().split("/")
            if (exp.size == 2) {
                cardMonth = exp[0]
                cardYear = exp[1]
            }

            if (!isValidDate(cardMonth, cardYear)) {
                isPassed = false
                showErrorField(3, MainClass.context.getString(R.string.the_expration_date_of_your_credit_card))
            }
        }

        return isPassed
    }

    private fun initErrorField() {

        cardNErr?.visibility = View.GONE
        cardHolderErr?.visibility = View.GONE
        cardExpErr?.visibility = View.GONE
        cardCvcErr?.visibility = View.GONE

        cardN?.setBackgroundResource(R.drawable.edit_text_bottom_line)
        cardHolder?.setBackgroundResource(R.drawable.edit_text_bottom_line)
        cardExp?.setBackgroundResource(R.drawable.edit_text_bottom_line)
        cardCvc?.setBackgroundResource(R.drawable.edit_text_bottom_line)
    }

    private fun showErrorField (field: Int, message: String){

        if (field == 1) {
            cardNErr?.visibility = View.VISIBLE
            cardNErr?.text = message
            cardN?.setBackgroundResource(R.drawable.error_border)
        }
        else if (field == 2) {
            cardHolderErr?.visibility = View.VISIBLE
            cardHolderErr?.text = message
            cardHolder?.setBackgroundResource(R.drawable.error_border)
        }
        else if (field == 3) {
            cardExpErr?.visibility = View.VISIBLE
            cardExpErr?.text = message
            cardExp?.setBackgroundResource(R.drawable.error_border)
        }
        else if (field == 4) {
            cardCvcErr?.visibility = View.VISIBLE
            cardCvcErr?.text = message
            cardCvc?.setBackgroundResource(R.drawable.error_border)
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

    private fun isUserAddressValid(firstname: String, lastname: String, cell_phone: String, address: String, city: String, postcode: String, state: String, country: Int): Boolean{

        if (isFieldsEmpty(firstname, lastname, cell_phone, address, city, postcode) || country <= 0) {
            return false
        } else
            return true
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
        val activity = activity
        activity?.onBackPressed()
    }

    override fun updateListCreditCard(listCreditCard: ArrayList<CreditCardData>) {
        //added to go to back when set default or delete from detail page
        val activity = activity
        activity?.onBackPressed()
    }

    override fun updateUserData(userData: UserProfile?) {

//        val time = userData?.delivery_preference ?: 2
//
//        dateView?.setTime(if (time == 0) DateChooserView.time.EVENING else time)

//        emailSwitch?.isChecked = if (userData?.is_mail_campaign == 1) true else false
    }

    override fun updateCardData(card: CreditCard?) {

        cardN?.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        cardN?.setText("************${card?.lastFour}")
        cardExp?.setText(dateFormat(card?.cardmonth, card?.cardyear))
        cardHolder?.setText(card?.cardname)

        //fill some value to pass checkExistence
        cardCvc?.setText("....")
    }

    private fun showDatePicker() {

        val myp = MonthYearPicker(activity);
        val exp = getDateFromField(cardExp?.text.toString())

        myp.build(exp.x - 1, exp.y, DialogInterface.OnClickListener { dialogInterface, i ->
            cardExp?.setText(dateFormat((myp.selectedMonth + 1).toString(), myp.selectedYear.toString()))
            cardCvc?.isFocusableInTouchMode = true
            cardCvc?.requestFocus()
            showKeyboard(cardCvc)
            card_year = (myp.selectedYear).toString()
            card_month = (myp.selectedMonth + 1).toString()
        }, null)
        myp.show()
    }

    override fun onSigned() {
        //TODO DELETE
        activity?.onBackPressed()
        MainClass.getRxBus()?.send(RxEvent(Events.SIGNED))
    }

    override fun onSignUped() {
        showFragment(AuthFragment(), true, R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right)
    }

    override fun onCountrySelected() {

    }

    override fun onStateSelected() {

    }

    private fun onPreviousButtonClick(){

        (activity as MainActivity).changeProfileTabState(MainActivity.ProfileTabStates.ADDRESS_PAGE)

        val auth = MainClass.getAuth()
        val token = auth.access_token

        if (token.isEmpty())
            showFragment(PageAddressFragment(), true,
                    R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageAddressFragment::class.java.simpleName)
        else
            showFragment(AddAddressFragment(), true,
                    R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, AddAddressFragment::class.java.simpleName)
    }

    private fun onNextButtonClick(){

            saveCardData()

            val auth = MainClass.getAuth()
            val token = auth.access_token

            //get saved Profile Data
            val userData: TempUserProfile?
            userData = (activity as MainActivity).user

            if (!token.isEmpty()) { //after signin

                if (checkValidate()) {
                    if (action_kind == CREDIT_CARD_ADD) {

                        presenter?.addCreditCard(cardN?.text.toString(), cardHolder?.text.toString(), card_year, card_month, cardCvc?.text.toString(), if (defaultSwitch!!.isChecked)  1 else 0)
                    } else if (action_kind == CREDIT_CARD_UPDATE) {
                        confirmDialogShow(3)
                    }
                }

            } else { //before signin, entire signup validation

                val email = userData!!.email
                val password = userData!!.password

                if (!isUserProfileValid(email, password)){
                    val bundle = Bundle()
                    bundle.putString("kind", TAB_PROFILE_FROM_ERROR)
                    val fragObj = PageProfileFragment()
                    fragObj.arguments = bundle

                    showFragment(fragObj, true,
                            R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageProfileFragment::class.java.simpleName)
                }
                else if (!isUserAddressValid(userData!!.firstname, userData!!.lastname, userData!!.phone, userData!!.address, userData!!.city, userData!!.postcode, userData!!.state, userData!!.country)) {
                    val bundle = Bundle()
                    bundle.putString("kind", TAB_ADDRESS_FROM_ERROR)
                    val fragObj = PageAddressFragment()
                    fragObj.arguments = bundle

                    showFragment(fragObj, true,
                            R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageAddressFragment::class.java.simpleName)
                }
                else if (!checkValidate()){} // card validation
                else {
                    presenter?.signUp(email, password,
                            userData!!.repassword, userData!!.phone,
                            userData!!.firstname, userData!!.lastname,
                            userData!!.address, userData!!.city, userData!!.state,
                            userData!!.countryName, userData!!.postcode, cardN?.text.toString(),
                            cardHolder?.text.toString(), cardExp?.text.toString(), cardCvc?.text.toString(),
                            userData!!.delivery_preference.toString(), emailSwitch?.isChecked ?: true)

                    MainClass.saveLoginData(email, password)
                    MainClass.saveSignUpTime()
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
            alertDialogBuilder.setMessage(MainClass.context.getString(R.string.are_you_sure_update_card))

        alertDialogBuilder.setPositiveButton("Yes") { dialog, id ->

            if (dialogID == 1)
                presenter?.deleteCreditCard(arguments.getString(CREDITCARD_ID))

            else if (dialogID == 2)
                presenter?.updateDefaultCreditCard(arguments.getString(CREDITCARD_ID))

            else if (dialogID == 3){
                presenter?.updateCreditCard(arguments.getString(CREDITCARD_ID), cardHolder?.text.toString(), card_year, card_month, if (defaultSwitch!!.isChecked)  1 else 0)
            }

        }

        alertDialogBuilder.setNegativeButton("No") { dialog, id ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun cvcDialogShow() {

        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton(MainClass.context.getString(R.string.ok)) {
            dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.cvc_dialog, null)
        dialog.setView(dialogLayout)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.show()
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.checkout_btn -> {

                onNextButtonClick()
            }

            R.id.card_exp -> showDatePicker()

            R.id.terms_txt -> {
                val activity = activity
                if (activity is MainActivity)
                    showFragment(fragment = ProfileTermsFragment(), isAddToBackStack = true,
                            enter = R.anim.down_center, exit = R.anim.center_up,
                            popEnter = R.anim.up_center, popExit = R.anim.center_down)
            }

            R.id.update_btn -> {
                onNextButtonClick()
            }

            R.id.delete_btn -> {
                if (is_default == 1)
                    showMessage(MainClass.context.getString(R.string.cannot_delete_default_card))
                else
                    confirmDialogShow(1)
            }

            R.id.passwordEye -> {

                if (showPassword) {
                    showPassword = false
                    cardN?.inputType = 129
                    passwordEye?.setBackgroundResource(R.drawable.password_show)
                }
                else {
                    showPassword = true
                    cardN?.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                    passwordEye?.setBackgroundResource(R.drawable.password_nshow)
                }
            }

            R.id.cvcEye -> {
                cvcDialogShow()
            }
        }
    }

    override fun onSwipeToLeft() {
        super.onSwipeToLeft()

//        val activity = activity
//        activity?.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        if (MainClass.getAuth().access_token.isEmpty())
            changeToolbarState(MainActivity.ToolbarStates.BACK_BTN_WITH_LOGIN)
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

