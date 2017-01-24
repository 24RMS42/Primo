/**
 * Changes:
 *
 * - Add control toolbar function
 * - Handle auto login
 * - Add Deeplink feature
 * - Create Profile tab and Main tab
 * - Add OK button When Reject page
 * - Show cart badge
 * - Fixed inactive address submenu clicking
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.jakewharton.rxbinding.view.visibility
import com.primo.R
import com.primo.auth.fragment.AuthFragment
import com.primo.goods.fragments.GoodsHistoryFragment
import com.primo.goods.fragments.GoodsPagerFragment
import com.primo.goods.fragments.GoodsWishlistFragment
import com.primo.network.new_models.Auth
import com.primo.network.new_models.TempUserProfile
import com.primo.utils.clearBackStack
import com.primo.utils.consts.AUTOLOGIN_DELAY_TIME
import com.primo.utils.consts.PERMISSION_CAMERA_REQUEST
import com.primo.utils.consts.PERMISSION_LOCATION_REQUEST
import com.primo.utils.other.Events
import com.primo.utils.other.RxEvent
import com.primo.utils.setOnClickListener
import com.primo.utils.showFragment
import com.primo.utils.slideAnimation
import android.widget.RelativeLayout
import com.primo.profile.fragments.*


class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {

    object ToolbarStates {
        val DEFAULT = 0
        val BACK_BTN_ONLY = 1
        val BACK_BTN_WITH_LOGIN = 2
        val BACK_BTN_WITH_LOGOUT = 3
        val BACK_BTN_ROTATED_ONLY = 4
        val OK_BTN = 5
        val BACK_BTN_AND_LOGOUT = 6
    }

    object ProfileTabStates {
        val INVISIBLE = 0
        val PROFILE_PAGE = 1 //default
        val ADDRESS_PAGE = 2
        val CARD_PAGE = 3
    }

    object SignUpStates {
        val NORMAL = 4 // normal
        val BASIC = 5 // no product
        val NOCC = 6 // 0 stock item
    }

    object TabbarStates {
        val SETTING = 1
        val HISTORY = 2
        val CAMERA = 3
        val CART = 4 // default
        val WISHLIST = 5
    }

    private var backBtn: ImageView? = null
    private var toolbarLogin: View? = null
    private var toolbarLogout: View? = null
    private var toolbarOK: View? = null
    private var toolbarShadow: View? = null
    private var thankMessage: TextView? = null

    private var action:String = ""
    private var productId:String = ""

    //these are for profile tab
    private var profileTabLayout: LinearLayout? = null
    private var profileBtn: ImageButton? = null
    private var addressBtn: ImageButton? = null
    private var cardBtn: ImageButton? = null

    //these are for main tabar
    private var tabbarLayout: LinearLayout? = null
    private var settingBtn: ImageButton? = null
    private var historyBtn: ImageButton? = null
    private var cameraBtn: ImageButton? = null
    private var cartBtn: ImageButton? = null
    private var wishlistBtn: ImageButton? = null
    private var badgeText: TextView? = null

    var user: TempUserProfile? = null
    private var signUpState = SignUpStates.NORMAL // normal value
    private var pageState = 0
    private var profilePageState = ProfileTabStates.PROFILE_PAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("Test", "===== open onCreate =====")
        val data = intent.data

        if (data != null){

            val host = data.host
            val params = data.pathSegments
            if (params.size != 0) { //from deeplink
                productId = params[0]
                action = host
            }
            else{ // from verify email
                //Don't do anything if app was closed before
            }
        }

        backBtn = findViewById(R.id.back_btn) as ImageView
        toolbarLogin = findViewById(R.id.toolbar_login)
        toolbarLogout = findViewById(R.id.toolbar_logout)
        toolbarOK = findViewById(R.id.toolbar_ok)
        toolbarShadow = findViewById(R.id.toolbar_shadow)
        thankMessage = findViewById(R.id.thank_message_txt) as TextView

        profileTabLayout = findViewById(R.id.profile_tab) as LinearLayout
        profileBtn = findViewById(R.id.btn_profile) as ImageButton
        addressBtn = findViewById(R.id.btn_address) as ImageButton
        cardBtn    = findViewById(R.id.btn_card)    as ImageButton

        tabbarLayout = findViewById(R.id.tabbar) as LinearLayout
        settingBtn = findViewById(R.id.btn_setting) as ImageButton
        historyBtn = findViewById(R.id.btn_history) as ImageButton
        cameraBtn = findViewById(R.id.btn_camera) as ImageButton
        cartBtn = findViewById(R.id.btn_cart) as ImageButton
        wishlistBtn = findViewById(R.id.btn_wishlist) as ImageButton
        badgeText = findViewById(R.id.badge) as TextView

        user = TempUserProfile()

        setOnClickListener(this, backBtn, toolbarLogin, toolbarLogout, toolbarOK, profileBtn, addressBtn, cardBtn, settingBtn, historyBtn, cameraBtn, cartBtn, wishlistBtn)

        showFragment(fragment = GoodsPagerFragment(), isAddToBackStack = false)

        // it is invisible in out of profile fragment
        changeProfileTabState(ProfileTabStates.INVISIBLE)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val handler = Handler()
        handler.postDelayed(
                {
                    this.clearBackStack()
                    Log.d("Test", "onNewIntent")
                }, 100)

        Log.d("Test", "==== open onNewIntent ===")
        val data = intent?.data

        if (data != null){

            val host = data.host
            val params = data.pathSegments
            if (params.size != 0) { //from deeplink
                productId = params[0]
                action = host
            }
            else{ // from verify email
                val signupTime = MainClass.getSignUpTime()
                val currentTime = MainClass.getCurrentTime()

                if (currentTime - signupTime > AUTOLOGIN_DELAY_TIME * 60)
                    MainClass.deleteLoginData()
                else
                    handler.postDelayed(
                        {
                            Log.d("Test", "NewIntent, send confirmed event")
                            MainClass.getRxBus()?.send(RxEvent(Events.CONFIRMED))
                        }, 150)
            }
        }
    }

    fun getDeeplinkData(): Pair<String, String>{
        return Pair(action, productId)
    }

    fun initDeeplinkData(){
        action = ""
        productId = ""
    }

    fun setSignUpState(state: Int){
        signUpState = state
    }

    fun getSignUpState():Int{
        return signUpState;
    }

    fun setPageState(state: Int){
        pageState = state
    }

    fun getPageState():Int{
        return pageState
    }

    fun setProfilePageState(state: Int){
        profilePageState = state
    }

    fun getProfilePageState():Int{
        return profilePageState
    }

    fun showToolbar(state: Boolean){

        val toolbarLayout = findViewById(R.id.app_bar) as RelativeLayout
        if (state)
            toolbarLayout.visibility = View.VISIBLE
        else
            toolbarLayout.visibility = View.GONE
    }

    fun showMainTabbar(state: Boolean){

        if (state)
            tabbarLayout?.visibility = View.VISIBLE
        else {
            tabbarLayout?.visibility = View.GONE
        }
    }

    fun changeToolbarState(state: Int) {

        when (state) {

            ToolbarStates.DEFAULT -> {
                backBtn?.visibility = View.INVISIBLE
                toolbarLogin?.visibility = View.GONE
                toolbarLogout?.visibility = View.GONE
                toolbarOK?.visibility = View.GONE
            }
            ToolbarStates.BACK_BTN_ONLY,
            ToolbarStates.BACK_BTN_ROTATED_ONLY -> {
                backBtn?.visibility = View.INVISIBLE // I think no need back button any more as we added tabbar
                val res =
                        if (state == ToolbarStates.BACK_BTN_ONLY) R.drawable.ic_chevron_left_black
                        else R.drawable.ic_expand_less_black
                backBtn?.setImageResource(res)
                toolbarOK?.visibility = View.GONE
            }
            ToolbarStates.BACK_BTN_WITH_LOGIN -> {
                backBtn?.visibility = View.INVISIBLE // I think no need back button any more as we added tabbar
                toolbarLogin?.visibility = View.VISIBLE
                toolbarLogout?.visibility = View.GONE
                toolbarOK?.visibility = View.GONE
                backBtn?.setImageResource(R.drawable.ic_chevron_left_black)
            }

            ToolbarStates.BACK_BTN_WITH_LOGOUT -> {
                backBtn?.visibility = View.INVISIBLE
                toolbarLogin?.visibility = View.GONE
                toolbarLogout?.visibility = View.VISIBLE
                toolbarOK?.visibility = View.GONE
                backBtn?.setImageResource(R.drawable.ic_chevron_left_black)
            }

            ToolbarStates.OK_BTN -> {
                backBtn?.visibility = View.INVISIBLE
                toolbarOK?.visibility = View.VISIBLE
                toolbarLogin?.visibility = View.GONE
                toolbarLogout?.visibility = View.GONE
            }

            ToolbarStates.BACK_BTN_AND_LOGOUT -> {
                backBtn?.visibility = View.VISIBLE
                toolbarLogin?.visibility = View.GONE
                toolbarLogout?.visibility = View.VISIBLE
                toolbarOK?.visibility = View.GONE
                backBtn?.setImageResource(R.drawable.ic_chevron_left_black)
            }
        }
    }

    fun showThankLayout(isSuccess: Boolean) {

        if (isSuccess)
            thankMessage?.text = getString(R.string.thank_you)
        else
            thankMessage?.text = getString(R.string.problem_with_order)

        thankMessage?.slideAnimation()
    }

    fun changeProfileTabState(state: Int) {

        when (state) {

            ProfileTabStates.INVISIBLE -> {
                profileTabLayout?.visibility = View.GONE
            }

            ProfileTabStates.PROFILE_PAGE -> {
                profileTabLayout?.visibility = View.VISIBLE
                profileBtn?.setBackgroundResource(R.drawable.tab_profile_selected)
                addressBtn?.setBackgroundResource(R.drawable.tab_homes)
                cardBtn?.setBackgroundResource(R.drawable.tab_cards)
            }

            ProfileTabStates.ADDRESS_PAGE -> {
                profileTabLayout?.visibility = View.VISIBLE
                profileBtn?.setBackgroundResource(R.drawable.tab_profile)
                addressBtn?.setBackgroundResource(R.drawable.tab_homes_selected)
                cardBtn?.setBackgroundResource(R.drawable.tab_cards)
            }

            ProfileTabStates.CARD_PAGE -> {
                profileTabLayout?.visibility = View.VISIBLE
                profileBtn?.setBackgroundResource(R.drawable.tab_profile)
                addressBtn?.setBackgroundResource(R.drawable.tab_homes)
                cardBtn?.setBackgroundResource(R.drawable.tab_cards_selected)
            }

            SignUpStates.BASIC -> {
                profileBtn?.visibility = View.VISIBLE
                addressBtn?.visibility = View.INVISIBLE
                cardBtn?.visibility = View.INVISIBLE
                setSignUpState(SignUpStates.BASIC)
            }

            SignUpStates.NOCC -> {
                profileBtn?.visibility = View.VISIBLE
                addressBtn?.visibility = View.VISIBLE
                cardBtn?.visibility = View.INVISIBLE
                setSignUpState(SignUpStates.NOCC)
            }

            SignUpStates.NORMAL -> {
                profileBtn?.visibility = View.VISIBLE
                addressBtn?.visibility = View.VISIBLE
                cardBtn?.visibility = View.VISIBLE
                setSignUpState(SignUpStates.NORMAL)
            }
        }
    }

    fun changeTabbarState(state: Int){

        when (state) {

            TabbarStates.SETTING -> {
                settingBtn?.setBackgroundResource(R.drawable.tab_settings_selected)
                historyBtn?.setBackgroundResource(R.drawable.tab_history)
                cartBtn?.setBackgroundResource(R.drawable.tab_cart)
                wishlistBtn?.setBackgroundResource(R.drawable.tab_wishlist)
            }
            TabbarStates.HISTORY -> {
                settingBtn?.setBackgroundResource(R.drawable.tab_settings)
                historyBtn?.setBackgroundResource(R.drawable.tab_history_selected)
                cartBtn?.setBackgroundResource(R.drawable.tab_cart)
                wishlistBtn?.setBackgroundResource(R.drawable.tab_wishlist)
            }
            TabbarStates.CART -> {
                settingBtn?.setBackgroundResource(R.drawable.tab_settings)
                historyBtn?.setBackgroundResource(R.drawable.tab_history)
                cartBtn?.setBackgroundResource(R.drawable.tab_cart_selected)
                wishlistBtn?.setBackgroundResource(R.drawable.tab_wishlist)
            }
            TabbarStates.WISHLIST -> {
                settingBtn?.setBackgroundResource(R.drawable.tab_settings)
                historyBtn?.setBackgroundResource(R.drawable.tab_history)
                cartBtn?.setBackgroundResource(R.drawable.tab_cart)
                wishlistBtn?.setBackgroundResource(R.drawable.tab_wishlist_selected)
            }
            TabbarStates.CAMERA -> {
                settingBtn?.setBackgroundResource(R.drawable.tab_settings)
                historyBtn?.setBackgroundResource(R.drawable.tab_history)
                cartBtn?.setBackgroundResource(R.drawable.tab_cart)
                wishlistBtn?.setBackgroundResource(R.drawable.tab_wishlist)
            }
        }
    }

    //update cart badge
    fun updateBadge(number: Int?){

        if (number == 0) {
            badgeText?.visibility = View.INVISIBLE
        }
        else {
            badgeText?.visibility = View.VISIBLE
            badgeText?.text = number.toString()
        }
    }

    fun increaseBadge(){

        badgeText?.visibility = View.VISIBLE
        val originBadge = Integer.parseInt(badgeText?.text.toString())
        badgeText?.text = (originBadge + 1).toString()
    }

    fun backToGoodsPage(){
        showFragment(GoodsPagerFragment(), true,
                R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, GoodsPagerFragment::class.java.simpleName)
        changeProfileTabState(ProfileTabStates.INVISIBLE)
    }

    override fun onClick(v: View?) {

        val auth = MainClass.getAuth()
        val token = auth.access_token

        when (v?.id) {

            R.id.back_btn -> {
                onBackPressed()
                //backToGoodsPage()
            }

            R.id.toolbar_ok -> {
                showThankLayout(true)
                onBackPressed()
            }

            R.id.toolbar_login -> {
                showFragment(AuthFragment(), true, R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right)
            }

            R.id.toolbar_logout -> {
                user = TempUserProfile() //initialize
                changeProfileTabState(ProfileTabStates.PROFILE_PAGE)
                MainClass.saveAuth(Auth())

                //go to Cart page, so cart number is refreshed
                changeTabbarState(TabbarStates.CART)
                setPageState(1);
                backToGoodsPage()
                MainClass.getRxBus()?.send(RxEvent(Events.UPDATE_PRODUCTS))
            }

            R.id.btn_profile -> {
                showFragment(PageProfileFragment(), true,
                        R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageProfileFragment::class.java.simpleName)
                changeProfileTabState(ProfileTabStates.PROFILE_PAGE)
                profilePageState = ProfileTabStates.PROFILE_PAGE
            }

            R.id.btn_address -> {

                changeProfileTabState(ProfileTabStates.ADDRESS_PAGE)

                if (token.isEmpty()){

                    val bundle = Bundle()
                    bundle.putString("kind", "none")
                    val fragObj = PageAddressFragment()
                    fragObj.arguments = bundle

                    showFragment(fragObj, true,
                            R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageAddressFragment::class.java.simpleName)
                }else
                    showFragment(AddAddressFragment(), true,
                            R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, AddAddressFragment::class.java.simpleName)

                /*if (profilePageState == ProfileTabStates.PROFILE_PAGE) {
                    MainClass.getRxBus()?.send(RxEvent(Events.TAB_ADDRESS)) // working
                }
                //Fixed inactive address submenu clicking from card page
                else if (profilePageState == ProfileTabStates.CARD_PAGE) {
                    //MainClass.getRxBus()?.send(RxEvent(Events.TAB_ADDRESS_FROM_CARD)) // so many call

                    changeProfileTabState(ProfileTabStates.ADDRESS_PAGE)
                    val auth = MainClass.getAuth()
                    val token = auth.access_token

                    if (token.isEmpty())
                        showFragment(PageAddressFragment(), true,
                                R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageAddressFragment::class.java.simpleName)
                    else
                        showFragment(AddAddressFragment(), true,
                                R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, AddAddressFragment::class.java.simpleName)
                }*/
            }

            R.id.btn_card -> {
                //MainClass.getRxBus()?.send(RxEvent(Events.TAB_CARD))

                changeProfileTabState(ProfileTabStates.CARD_PAGE)

                if (token.isEmpty()){

                    val bundle = Bundle()
                    bundle.putString("kind", "none")
                    val fragObj = PageCardFragment()
                    fragObj.arguments = bundle

                    showFragment(fragObj, true,
                            R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageCardFragment::class.java.simpleName)
                }else
                    showFragment(AddCardFragment(), true,
                            R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, AddCardFragment::class.java.simpleName)
            }

            R.id.btn_setting -> {
                profilePageState = ProfileTabStates.PROFILE_PAGE
                changeTabbarState(TabbarStates.SETTING)
                showFragment(PageProfileFragment(), true,
                        R.anim.left_center, R.anim.center_right, R.anim.right_center, R.anim.center_left, PageProfileFragment::class.java.simpleName)
            }

            R.id.btn_history -> {
                changeProfileTabState(MainActivity.ProfileTabStates.INVISIBLE)
                changeTabbarState(TabbarStates.HISTORY)
                showFragment(GoodsHistoryFragment(), true,
                        R.anim.down_center, R.anim.center_up, R.anim.up_center, R.anim.center_down)
            }

            R.id.btn_camera -> {
                changeTabbarState(TabbarStates.CAMERA)
                setPageState(0);
                backToGoodsPage()
            }

            R.id.btn_cart -> {
                //MainClass.getRxBus()?.send(RxEvent(Events.PAGER_FIRST_ITEM)) // not working
                changeTabbarState(TabbarStates.CART)
                setPageState(1); // Set Total fragment
                backToGoodsPage()
            }

            R.id.btn_wishlist -> {
                changeProfileTabState(MainActivity.ProfileTabStates.INVISIBLE)
                changeTabbarState(TabbarStates.WISHLIST)
                showFragment(GoodsWishlistFragment(), true, R.anim.right_center, R.anim.center_left,
                        R.anim.left_center, R.anim.center_right, GoodsWishlistFragment::class.java.simpleName)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.d("TEST", requestCode.toString() + "<<")

        if (requestCode == PERMISSION_CAMERA_REQUEST && grantResults.size > 0)
            MainClass.getRxBus()?.send(RxEvent(Events.CAMERA_PERMISSION, grantResults[0]))
        else if (requestCode == PERMISSION_LOCATION_REQUEST && grantResults.size > 0)
            MainClass.getRxBus()?.send(RxEvent(Events.LOCATION_PERMISSION, grantResults[0]))
    }
}
