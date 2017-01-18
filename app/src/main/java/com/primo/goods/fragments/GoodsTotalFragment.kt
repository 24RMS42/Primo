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
import com.primo.R
import com.primo.goods.adapter.GoodsListAdapter
import com.primo.goods.decoration.VerticalSpaceItemDecoration
import com.primo.goods.dialog.GoodsDescriptionDialogFragment
import com.primo.goods.mvp.GoodsTotalPresenter
import com.primo.goods.mvp.GoodsTotalPresenterImpl
import com.primo.goods.mvp.GoodsTotalView
import com.primo.goods.view.GoodsFooterView
import com.primo.main.MainClass
import com.primo.network.models.ShippingQuote
import com.primo.network.new_models.Auth
import com.primo.network.new_models.CartItem
import com.primo.network.new_models.Product
import com.primo.network.new_models.Stock
import com.primo.profile.fragments.ProfileFragment
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
    }

    override fun initPresenter() {
        presenter = GoodsTotalPresenterImpl(this)
    }

    override fun onResume() {
        super.onResume()
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

        if (isProductExist && token.isEmpty())
            loadOrderFragment()
        else if (isProductExist) {

            if(sensorAccel != null) {
                if (isPhoneMove()) {
                    orderPlace()
                } else {
                    waitPhoneMove()
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

    override fun onCostChanged(cost: Double) {
        isProductExist = cost > 0
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

        _subscriptions = CompositeSubscription();
        _subscriptions?.add(_rxBus?.toObserverable()
                ?.subscribe({

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
    }

    override fun onSigned(result: Auth) {

        MainClass.deleteLoginData()

        MainClass.saveAuth(result)
        presenter?.getProductList()
    }

    private fun loadOrderFragment() {
        showFragment(ProfileFragment(), true,
                R.anim.left_center, R.anim.center_right, R.anim.right_center, R.anim.center_left, ProfileFragment::class.java.simpleName)
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

        if (isProductExist)
            loadOrderFragment()
        else
            showDialog(getString(R.string.please_choose_at_least_one_product))
    }

    override fun onSwipeToLeft() {
        showFragment(GoodsWishlistFragment(), true, R.anim.right_center, R.anim.center_left,
                R.anim.left_center, R.anim.center_right, GoodsWishlistFragment::class.java.simpleName)
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
            parent.showProgress()
        }
    }

    override fun hideProgress() {
        isLoading = false
        val parent = parentFragment
        if (parent != null && parent is GoodsPagerFragment) {
            parent.hideProgress()
        }
    }

    override fun showMessage(message: String?, event: RxEvent?) {

    }

    private fun calculateCost(shippingCost: Double = 0.0) {

        var cost = 0.0
        var quantity = 0
        var currency = ""

        for (product in productList.orEmpty()) {
            cost += product.price * product.quantity
            quantity += product.quantity
            currency = getCurrency(product.currency)
        }

        cost += shippingCost

        _rxBus?.send(RxEvent(Events.CHANGE_COST, Triple(quantity, cost, currency)))
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

        var wa: Thread = Thread(this)
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
        var timeStop: Long = System.currentTimeMillis() + TIME_WAIT
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
        val parent = parentFragment
        if (parent != null && parent is GoodsPagerFragment) {
            parent.getOrderPlace()
        }
    }
}