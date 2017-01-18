package com.primo.profile.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import com.primo.R
import com.primo.main.MainActivity
import com.primo.main.MainClass
import com.primo.network.models.Country
import com.primo.network.models.State
import com.primo.network.new_models.Auth
import com.primo.network.new_models.CreditCard
import com.primo.network.new_models.UserProfile
import com.primo.profile.mvp.OrderPresenter
import com.primo.profile.mvp.OrderView
import com.primo.profile.mvp.ProfilePresenterImpl
import com.primo.utils.*
import com.primo.utils.base.BasePresenterFragment
import com.primo.utils.other.Events
import com.primo.utils.other.MultipleTextWatcher
import com.primo.utils.other.RxBus
import com.primo.utils.other.RxEvent
import com.primo.utils.views.DateChooserView
import com.primo.utils.views.GestureRelativeLayout
import com.primo.utils.views.MonthYearPicker
import com.primo.utils.views.PrefixedEditText
import rx.subscriptions.CompositeSubscription

class ProfileFragment : BasePresenterFragment<OrderView, OrderPresenter>(), OrderView, View.OnClickListener,
        GestureRelativeLayout.OnSwipeListener, MultipleTextWatcher, PlaceBottomSheet.ListDialogResult {

    private var gestureLayout: GestureRelativeLayout? = null

    private var firstName: PrefixedEditText? = null
    private var lastName: PrefixedEditText? = null
    private var phoneNumber: PrefixedEditText? = null
    private var email: PrefixedEditText? = null
    private var password: PrefixedEditText? = null
    private var repassword: PrefixedEditText? = null
    private var address: PrefixedEditText? = null
    private var city: PrefixedEditText? = null
    private var zip: PrefixedEditText? = null
    private var cardN: PrefixedEditText? = null
    private var cardHolder: PrefixedEditText? = null
    private var cardExp: PrefixedEditText? = null
    private var cardCvc: PrefixedEditText? = null

    private var country: PrefixedEditText? = null
    private var state: PrefixedEditText? = null

    private var dateView: DateChooserView? = null
    private var emailTxt: TextView? = null
    private var emailSwitch: SwitchCompat? = null
    private var checkoutBtn: Button? = null
    private var termsTxt: TextView? = null

    private var _subscriptions: CompositeSubscription? = null
    private var _rxBus: RxBus? = null

    private var isSigned = false

    override fun onStart() {
        super.onStart()

        _subscriptions = CompositeSubscription();
        _subscriptions?.add(_rxBus?.toObserverable()
                ?.subscribe({
                    when (it.key) {

                        Events.SIGNED -> updateData(it.sentObject as Auth)

                        Events.CONFIRMED -> {
                            backToPreviousScreen()
                            MainClass.getRxBus()?.send(RxEvent(Events.CONFIRMED))
                        }
                    }
                }));
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (rootView == null) {
            rootView = inflater?.inflate(R.layout.profile_fragment, container, false)
            rootView?.isLongClickable = true

            initSwipe()
            init()
            initPresenter()

            changeViewsState()

            presenter?.retrieveUserData()
            presenter?.retrieveCardData()
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
        address = rootView?.findViewById(R.id.address) as PrefixedEditText
        city = rootView?.findViewById(R.id.city) as PrefixedEditText
        country = rootView?.findViewById(R.id.country) as PrefixedEditText
        state = rootView?.findViewById(R.id.state) as PrefixedEditText
        zip = rootView?.findViewById(R.id.zip) as PrefixedEditText
        cardN = rootView?.findViewById(R.id.card_n) as PrefixedEditText
        cardHolder = rootView?.findViewById(R.id.card_holder) as PrefixedEditText
        cardExp = rootView?.findViewById(R.id.card_exp) as PrefixedEditText
        cardCvc = rootView?.findViewById(R.id.card_cvc) as PrefixedEditText

        dateView = rootView?.findViewById(R.id.date_view) as DateChooserView
        emailTxt = rootView?.findViewById(R.id.email_txt) as TextView
        emailSwitch = rootView?.findViewById(R.id.email_switch) as SwitchCompat
        checkoutBtn = rootView?.findViewById(R.id.checkout_btn) as Button
        termsTxt = rootView?.findViewById(R.id.terms_txt) as TextView

        city?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
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

        cardHolder?.setOnFocusChangeListener { view, b ->

            if (b && !firstName?.text.toString().isEmpty()
                    && !lastName?.text.toString().isEmpty()
                    && cardHolder?.text.toString().isEmpty())
                cardHolder?.setText("${firstName?.text.toString()} ${lastName?.text.toString()}")
        }

        cardHolder?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                activity.hideKeyboard()
                showDatePicker()
                return@OnEditorActionListener true
            }
            false
        })

        setOnClickListener(this, country, state, checkoutBtn, termsTxt, cardExp)

        _rxBus = MainClass.getRxBus()
    }

    private fun changeViewsState() {

        val auth = MainClass.getAuth()

        if (!auth.access_token.isEmpty()) {
            email?.visibility = View.GONE
            password?.visibility = View.GONE
            repassword?.visibility = View.GONE
            cardN?.isEnabled = false
            cardCvc?.isEnabled = false
            //emailTxt?.text = getString(R.string.update_text)
            emailTxt?.visibility = View.GONE
            emailSwitch?.visibility = View.GONE //View.VISIBLE
            termsTxt?.visibility = View.GONE
            checkoutBtn?.text = getString(R.string.update)
        } else {
            checkoutBtn?.text = getString(R.string.sign_up)
        }
    }

    override fun onCountrySelected(country: Country) {

        if (!this.country?.text.toString().equals(country.name)) {
            state?.setText("")
            presenter?.saveState("")
        }

        this.country?.setText(country.name)
        this.country?.tag = country.fileName
        presenter?.saveCountry(country.value)

        this.state?.postDelayed({ this.state?.performClick() }, 700)
    }

    override fun onStateSelected(state: State) {
        this.state?.setText(state.name)
        presenter?.saveState(state.name)

        zip?.isFocusableInTouchMode = true
        zip?.requestFocus()
        showKeyboard(zip)
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
            presenter?.retrieveCardData()

            changeViewsState()
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

    override fun updateUserData(userData: UserProfile?) {

        firstName?.setText(userData?.firstname ?: "")
        lastName?.setText(userData?.lastname ?: "")
        phoneNumber?.setText(userData?.cell_phone ?: "")
        email?.setText(userData?.email ?: "")
        address?.setText(userData?.address ?: "")
        city?.setText(userData?.city ?: "")
        zip?.setText(userData?.postcode ?: "")


        val countryList: MutableList<Country> = presenter?.getCountries().orEmpty().toMutableList()
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

        emailSwitch?.isChecked = if (userData?.is_mail_campaign == 1) true else false
    }

    override fun updateCardData(card: CreditCard?) {

        cardN?.setText("XXXXXXXXXXXX${card?.lastFour}")
        cardExp?.setText(dateFormat(card?.cardmonth, card?.cardyear))
        cardHolder?.setText(card?.cardname)
    }

    private fun showDatePicker() {

        val myp = MonthYearPicker(activity);
        val exp = getDateFromField(cardExp?.text.toString())

        myp.build(exp.x - 1, exp.y, DialogInterface.OnClickListener { dialogInterface, i ->
            cardExp?.setText(dateFormat((myp.selectedMonth + 1).toString(), myp.selectedYear.toString()))
            cardCvc?.isFocusableInTouchMode = true
            cardCvc?.requestFocus()
            showKeyboard(cardCvc)
        }, null)
        myp.show()
    }

    override fun onSigned() {
        //TODO DELETE
        activity?.onBackPressed()
        MainClass.getRxBus()?.send(RxEvent(Events.SIGNED))
    }

    override fun onCountrySelected() {
        state?.setText("")
    }

    override fun onStateSelected() {
        zip?.isFocusableInTouchMode = true
        zip?.requestFocus()
        showKeyboard(zip)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.checkout_btn -> {

                isSigned = false

                val auth = MainClass.getAuth()
                val token = auth.access_token

                if (!token.isEmpty()) {

                    presenter?.update(phoneNumber?.text.toString(), firstName?.text.toString(), lastName?.text.toString(),
                            address?.text.toString(), city?.text.toString(), state?.text.toString(),
                            country?.text.toString(), zip?.text.toString(), cardHolder?.text.toString(),
                            cardExp?.text.toString(), dateView?.timePosition.toString(),
                            emailSwitch?.isChecked ?: false)
                } else {

                    val email = email?.text?.trim().toString()
                    val password = password?.text?.trim().toString()

                    presenter?.signUp(email, password,
                            repassword?.text.toString(), phoneNumber?.text.toString(),
                            firstName?.text.toString(), lastName?.text.toString(),
                            address?.text.toString(), city?.text.toString(), state?.text.toString(),
                            country?.text.toString(), zip?.text.toString(), cardN?.text.toString(),
                            cardHolder?.text.toString(), cardExp?.text.toString(), cardCvc?.text.toString(),
                            dateView?.timePosition.toString(), emailSwitch?.isChecked ?: false)

                    MainClass.saveLoginData(email, password)
                }
            }

            R.id.card_exp -> showDatePicker()

            R.id.country -> {
                activity.hideKeyboard()
                showPickerDialog()
            }

            R.id.state -> {
                activity.hideKeyboard()
                showPickerDialog(country?.tag.toString())

            }

            R.id.terms_txt -> {
                val activity = activity
                if (activity is MainActivity)
                    showFragment(fragment = ProfileTermsFragment(), isAddToBackStack = true,
                            enter = R.anim.down_center, exit = R.anim.center_up,
                            popEnter = R.anim.up_center, popExit = R.anim.center_down)
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

    override fun onSwipeToLeft() {
        super.onSwipeToLeft()

        val activity = activity
        activity?.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
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

