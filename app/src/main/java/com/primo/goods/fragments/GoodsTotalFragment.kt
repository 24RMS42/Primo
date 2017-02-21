/**
 * Changes:
 *
 * - change custom dialog
 * - allow to go to signup page without product
 * - control toolbar
 * - add Deeplink feature
 * - check Shipping address and Credit Card before checkout
 * - add swipe down effect
 * - integrate Count api
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.goods.fragments

import android.app.ProgressDialog
import android.content.Context
import android.graphics.PixelFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kaopiz.kprogresshud.KProgressHUD
import com.primo.R
import com.primo.goods.adapter.GoodsListAdapter
import com.primo.goods.decoration.VerticalSpaceItemDecoration
import com.primo.goods.dialog.GoodsDescriptionDialogFragment
import com.primo.goods.mvp.GoodsTotalPresenter
import com.primo.goods.mvp.GoodsTotalPresenterImpl
import com.primo.goods.mvp.GoodsTotalView
import com.primo.goods.view.GoodsFooterView
import com.primo.main.MainActivity
import com.primo.main.MainClass
import com.primo.network.models.ShippingQuote
import com.primo.network.new_models.*
import com.primo.profile.fragments.PageProfileFragment
import com.primo.profile.fragments.SetCountryFragment
import com.primo.utils.DialogUtils
import com.primo.utils.base.BasePresenterFragment
import com.primo.utils.consts.*
import com.primo.utils.getCurrency
import com.primo.utils.interfaces.OnDialogClickListener
import com.primo.utils.interfaces.OnItemClickListener
import com.primo.utils.other.Events
import com.primo.utils.other.RxBus
import com.primo.utils.other.RxEvent
import com.primo.utils.tapScaleAnimation
import com.primo.utils.views.GestureRelativeLayout
import rx.subscriptions.CompositeSubscription
import java.util.*


class GoodsTotalFragment : BasePresenterFragment<GoodsTotalView, GoodsTotalPresenter>(),
        GoodsFooterView.FooterViewListener, OnItemClickListener, GoodsTotalView,
        GestureRelativeLayout.OnSwipeListener, OnDialogClickListener, SensorEventListener, Runnable {

    //=====
    private var sensorManager: SensorManager? = null
    private var sensorAccel: Sensor? = null
    private var valueAccel: FloatArray = floatArrayOf(0f, 0f, 0f)
    private var valueAccelGravity: FloatArray = floatArrayOf(0f, 0f, 0f)
    private var valueAccelMotion: FloatArray = floatArrayOf(0f, 0f, 0f)

    private var isWait: Boolean = false
    private var handler = Handler()

    private var progressDialog: ProgressDialog? = null
    //=====

    private var gestureLayout: GestureRelativeLayout? = null
    private var layoutManager: LinearLayoutManager? = null
    private var cartList: RecyclerView? = null
    private var isProductExist = false
    private var _rxBus: RxBus? = null
    private var _subscriptions: CompositeSubscription? = null
    private var productList: MutableList<CartItem>? = null
    private var goodsListAdapter: GoodsListAdapter? = null
    private var isLoading = false
    private var mDialogUtils: DialogUtils? = null
    private var totalCost = 0.0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (rootView == null) {
            rootView = inflater!!.inflate(R.layout.goods_total_fragment, container, false)

            _rxBus = MainClass.getRxBus()

            init()
            initPresenter()
        }

        return rootView
    }

    private fun init() {

        gestureLayout = rootView?.findViewById(R.id.gesture_layout) as GestureRelativeLayout
        gestureLayout?.onSwipeListener = this

        cartList = rootView?.findViewById(R.id.cart_list) as RecyclerView
        layoutManager = LinearLayoutManager(context)
        cartList?.layoutManager = layoutManager
        goodsListAdapter = GoodsListAdapter()
        productList = goodsListAdapter?.list as MutableList<CartItem>
        goodsListAdapter?.goodsFooterViewListener = this
        goodsListAdapter?.itemClickListener = this
        cartList?.adapter = goodsListAdapter

        cartList?.isNestedScrollingEnabled = false
        cartList?.addItemDecoration(VerticalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.decoration_space)))

        //=====
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorAccel = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        //=====
        mDialogUtils = DialogUtils()
    }

    override fun initPresenter() {
        presenter = GoodsTotalPresenterImpl(this)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).changeTabbarState(MainActivity.TabbarStates.CART)
        presenter?.getProductList()

        sensorManager?.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()

        sensorManager?.unregisterListener(this)
    }

    override fun onCheckoutClick(message: String) {

        val auth = MainClass.getAuth()
        val token = auth.access_token

        if (isProductExist && token.isEmpty()) {
            loadOrderFragment()
        }
        else if (isProductExist) {

            if(sensorAccel != null) {
                if (isPhoneMove()) {
                    orderPlace()
                } else {
                    //waitPhoneMove()
                    wait_PhoneMove()
                }
            } else {
                orderPlace()
            }

        } else
            showDialog(getString(R.string.please_choose_at_least_one_product))
    }

    override fun updateProductList(products: List<CartItem>) {

        goodsListAdapter?.list?.clear()
        goodsListAdapter?.list?.addAll(products)
        goodsListAdapter?.notifyDataSetChanged()
        calculateCost()
        isProductExist = products.size > 0

    }

    override fun onHistoryClick() {

        showFragment(GoodsHistoryFragment(), true,
                R.anim.down_center, R.anim.center_up, R.anim.up_center, R.anim.center_down)
    }

    override fun onCostChanged(cost: Double, quantity: Int) {
        isProductExist = quantity > 0 // determine product exist by quantity, not cost
        checkProductBeforeSignUp() // it should be calculated before go to SignUp page as user may click Setting tab to go to SignUp page
    }

    override fun onItemClick(view: View?, position: Int) {

        val item = productList?.get(position)
        val quantity = item?.quantity ?: 1

        if (item == null || isLoading)
            return

        when (view?.id) {

            R.id.product -> {
                presenter?.retrieveProductStock(item)
            }

            R.id.plus_btn -> {
                view?.tapScaleAnimation()
                presenter?.updateCartItem(item, true)
            }
            R.id.minus_btn -> {
                view?.tapScaleAnimation()
                if (quantity > 1) {
                    presenter?.updateCartItem(item, false)
                } else {
                    deleteItem(item)
                }
            }
        }

        calculateCost()
        goodsListAdapter?.notifyItemChanged(productList?.size ?: 0)
    }

    override fun onStart() {
        super.onStart()

        (activity as MainActivity).showToolbar(false)
        Log.d("Test", "=======Total Fragment start")

        _subscriptions = CompositeSubscription();
        _subscriptions?.add(_rxBus?.toObserverable()
                ?.subscribe({

                    Log.d("Test", "TOTAL fragment get event:" + it.key)
                    when (it.key) {

                        Events.CONFIRMED -> {
                            val pair = MainClass.getLoginData()
                            presenter?.signIn(pair.first, pair.second)
                        }

                        Events.ADD_PRODUCT -> presenter?.addProduct(it.sentObject as Product)

                        Events.SIGNED -> presenter?.getProductList()

                        Events.COST_REQUEST -> calculateCost((it.sentObject as? Double) ?: 0.0)

                        Events.UPDATE_PRODUCTS -> {
                            productList?.clear()
                            calculateCost()
                            goodsListAdapter?.notifyDataSetChanged()
                            presenter?.getProductList()
                        }
                    }
                }));

        val pair = (activity as MainActivity).getDeeplinkData()
        if (pair.first != "" && pair.second != ""){
            presenter?.searchProductById(pair.second)
            (activity as MainActivity).initDeeplinkData()
        }

    }

    override fun onSigned(result: Auth) {

        MainClass.deleteLoginData()

        MainClass.saveAuth(result)

        // After login automatically, fix screen issue //
        val activity = activity
        if (activity != null && activity is MainActivity) {
            if (result.country < 1 || result.country > 253) { // if country field is empty, then show the page to set country
                activity.showMainTabbar(false)
                activity.changeProfileTabState(MainActivity.ProfileTabStates.INVISIBLE)
                showFragment(SetCountryFragment(), true,
                        R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, SetCountryFragment::class.java.simpleName)
            } else {
                Log.d("Test", " === onSigned === ")
                activity.changeTabbarState(MainActivity.TabbarStates.CART)
                activity.setPageState(1); // Set Total fragment
                showFragment(GoodsPagerFragment(), true, R.anim.left_center, R.anim.center_right, R.anim.right_center, R.anim.center_left, GoodsPagerFragment::class.java.simpleName)

                presenter?.getProductList()
            }
        }
    }

    override fun onCheckShippingCardBeforeCheckout(result: Array<String?>) {

        if (result[0] == "") {
            showMessage(MainClass.context.getString(R.string.please_add_shipping_address))
            (activity as MainActivity).changeProfileTabState(MainActivity.ProfileTabStates.PROFILE_PAGE)
            showFragment(PageProfileFragment(), true,
                    R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageProfileFragment::class.java.simpleName)
        }
        else if (result[1] == "") {
            showMessage(MainClass.context.getString(R.string.please_add_credit_card))
            (activity as MainActivity).changeProfileTabState(MainActivity.ProfileTabStates.PROFILE_PAGE)
            showFragment(PageProfileFragment(), true,
                    R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageProfileFragment::class.java.simpleName)
        }
        else {
            MainClass.getSharedPreferences().edit().putString(SHIPPING_ID, result[0]).apply()
            MainClass.getSharedPreferences().edit().putString(CREDITCARD_ID, result[1]).apply()

            val parent = parentFragment
            if (parent != null && parent is GoodsPagerFragment) {
                parent.getOrderPlace()
            }
        }
    }

    private fun checkProductBeforeSignUp(){

        (activity as MainActivity).changeProfileTabState(MainActivity.ProfileTabStates.INVISIBLE)

        if (isProductExist && totalCost > 0) //normal
            (activity as MainActivity).changeProfileTabState(MainActivity.SignUpStates.NORMAL)
        else if (isProductExist && totalCost == 0.0) //0 stock item
            (activity as MainActivity).changeProfileTabState(MainActivity.SignUpStates.NOCC)
        else //no product
            (activity as MainActivity).changeProfileTabState(MainActivity.SignUpStates.BASIC)

    }

    private fun loadOrderFragment() {
        showFragment(PageProfileFragment(), true,
                R.anim.left_center, R.anim.center_right, R.anim.right_center, R.anim.center_left, PageProfileFragment::class.java.simpleName)
    }

    override fun showDescriptionDialog(data: Pair<CartItem, ArrayList<Stock>>) {
        showDialogFragment(GoodsDescriptionDialogFragment.newInstance(data.first, data.second), this@GoodsTotalFragment)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) goodsListAdapter?.notifyDataSetChanged()
    }

    override fun onUpdateShipping(shippings: MutableList<ShippingQuote>) {

        goodsListAdapter?.shippings = shippings
        goodsListAdapter?.notifyItemChanged(productList?.size ?: 0)

        var shippingCost = 0.0

        for (i in shippings)
            shippingCost += i.rate

        calculateCost(shippingCost)
    }

    override fun deleteItem(cartItem: CartItem) {

        val index = productList?.indexOf(cartItem) ?: -1
        if (index >= 0) {
            presenter?.removeProduct(cartItem)
            productList?.removeAt(index)
            goodsListAdapter?.notifyItemRemoved(index)
            calculateCost()
        }
    }

    override fun onSwipeToRight() {

        loadOrderFragment()
    }

    override fun onSwipeToLeft() {
        showFragment(GoodsWishlistFragment(), true, R.anim.right_center, R.anim.center_left,
                R.anim.left_center, R.anim.center_right, GoodsWishlistFragment::class.java.simpleName)
    }

    override fun onSwipeDown() {
        Log.d("Test", "== swiping down == ")
        onHistoryClick()
    }

    override fun onDialogClick(code: Int, dataObject: Any?) {

        if (dataObject != null && dataObject is CartItem) {

            val index = productList?.indexOf(dataObject) ?: -1
            if (index < 0) return

            val product = productList?.get(index)

            product?.let {
                when (code) {

                    DELETE -> {

                        deleteItem(product)
                    }

                    ADD -> {

                        presenter?.addToWishList(product)
                    }

                    else -> {
                    }
                }
            }

        } else if (dataObject != null && code == UPDATE) {

            val data = dataObject as Pair<CartItem, String>
            val stockId = data.second
            val cartItemId = data.first.cartItemId
            val quantity = data.first.quantity

            if (!stockId.isEmpty())
                presenter?.updateCartItem(stockId, cartItemId, quantity.toString())
        }
    }

    override fun showProgress() {
        isLoading = true
        val parent = parentFragment
        if (parent != null && parent is GoodsPagerFragment) {
            //parent.showProgress()
            mDialogUtils?.showLoadingWithoutLabel(context)
        }
    }

    override fun hideProgress() {
        Log.d("Test", "search add complete")
        isLoading = false
        val parent = parentFragment
        if (parent != null && parent is GoodsPagerFragment) {
            //parent.hideProgress()
            mDialogUtils?.hideLoading()
        }
    }

    override fun showMessage(message: String?, event: RxEvent?) {
        showDialog(message = message, event = event)
    }

    override fun displayErrorMessage(message : String?, code: Int?, event: RxEvent?){
        Log.d("Test", "total fragment error message" + code)
        showErrorDialog(message, code)
    }

    private fun calculateCost(shippingCost: Double = 0.0) {

        var cost = 0.0
        var quantity = 0
        var currency = ""

        for (product in productList.orEmpty()) {
            if (product.status != ADD_TO_WISHLIST) {
                cost += product.price * product.quantity
                currency = getCurrency(product.currency)
            }
            quantity += product.quantity // for no stock item, it is calculated in count
        }

        cost += shippingCost
        totalCost = cost
        Log.d("Test", "total cost:" + totalCost)

        _rxBus?.send(RxEvent(Events.CHANGE_COST, Triple(quantity, cost, currency)))

        //update cart badge
        //productList?.size : product count
        //quantity : product total count with quantity

        // == replace by calling count api to update cart badge == //
        //(activity as MainActivity).updateBadge(quantity)
        val auth = MainClass.getAuth()
        val token = auth.access_token

        if (token.isEmpty())
            presenter?.getPublicCount()
        else
            presenter?.getLiveCount()

        checkProductBeforeSignUp()
    }

    override fun getCountResult(counts: Count) {
        (activity as MainActivity).updateBadge(counts.cart_count)
        _rxBus?.send(RxEvent(Events.CHANGE_COUNT, Triple(counts.currency, counts.total_final_price, counts.cart_count)))
    }

    override fun onStop() {
        super.onStop()
        _subscriptions?.clear()
    }

    fun isPhoneMove(): Boolean {
        if (valueAccelMotion[0] >= SENSITIVITY_GIGGLE ||
                valueAccelMotion[1] >= SENSITIVITY_GIGGLE ||
                valueAccelMotion[2] >= SENSITIVITY_GIGGLE) {
            return true
        } else {
            return false
        }
    }

    fun waitPhoneMove() {

        progressDialog = ProgressDialog(context)

        val layoutParams = progressDialog?.getWindow()!!.getAttributes()
        layoutParams.format = PixelFormat.TRANSPARENT
        progressDialog?.getWindow()!!.setAttributes(layoutParams)
        progressDialog?.setMessage(getString(R.string.check_out_giggle))
        progressDialog?.setIndeterminate(false)
        progressDialog?.setCancelable(true)
        progressDialog?.show()

        isWait = true
        handler = object: Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when(msg.what){
                    GIGGLE_OK -> {
                        progressDialog?.dismiss()
                        orderPlace()
                    }
                    GIGGLE_NO -> {
                        progressDialog?.dismiss()
                    }
                }
            }
        }

        val wa: Thread = Thread(this)
        wa.start()
    }

    fun wait_PhoneMove() {

        var mkProgressHUD: KProgressHUD? = null
        mkProgressHUD = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setDetailsLabel(getString(R.string.check_out_giggle))
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show()

        isWait = true
        handler = object: Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when(msg.what){
                    GIGGLE_OK -> {
                        mkProgressHUD?.dismiss()
                        orderPlace()
                    }
                    GIGGLE_NO -> {
                        mkProgressHUD?.dismiss()
                    }
                }
            }
        }

        val wa: Thread = Thread(this)
        wa.start()
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        when (p0?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                for (i in 0..2) {
                    valueAccel[i] = p0!!.values[i]
                    valueAccelGravity[i] = (0.1 * p0!!.values[i] + 0.9 * valueAccelGravity[i]).toFloat()
                    valueAccelMotion[i] = p0!!.values[i] - valueAccelGravity[i];
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun run() {
        val timeStop: Long = System.currentTimeMillis() + TIME_WAIT
        while (isWait && timeStop > System.currentTimeMillis()){
            if (isPhoneMove()){
                isWait = false
                handler.sendEmptyMessage(GIGGLE_OK)
            }
        }
        if(isWait) {
            isWait = false
            handler.sendEmptyMessage(GIGGLE_NO)
        }
    }

    private fun orderPlace() {
        // check Shipping address and Credit Card before checkout
        presenter?.checkShippingCardBeforeCheckout()
    }
}