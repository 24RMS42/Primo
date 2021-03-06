/**
 * Changes:
 *
 * - Calculate total price without 0 stock item
 * - Fix price for 0 stock item on camera page
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.goods.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.primo.R
import com.primo.main.MainClass
import com.primo.network.models.ShippingQuote
import com.primo.network.new_models.CartItem
import com.primo.utils.*
import com.primo.utils.consts.ADD_TO_WISHLIST
import com.primo.utils.other.Events
import com.primo.utils.other.RxBus
import com.primo.utils.other.RxEvent
import com.primo.utils.views.SquareImageView

class GoodsFooterView : RelativeLayout, View.OnClickListener {

    private val DEFAULT_DEGREE = 90f

    private var shippingLayout: View? = null
    private var shippingBase: View? = null
    private var shippingImg: SquareImageView? = null
    private var shippingCount: TextView? = null
    private var shippingTxt: TextView? = null
    private var shippingMore: View? = null
    private var shippingContainer: LinearLayout? = null
    private var totalPrice: TextView? = null
    private var checkoutBtn: View? = null
    private var sellerMessage: EditText? = null
    private var historyBtn: View? = null
    private var historyTxt: View? = null
    private var isExpanded: Boolean = false
    private var itemHeight: Int = 0
    private var cost: Double = 0.0
    private var shippingCost: Double = 0.0
    private var currency: String = ""

    private var _rxBus: RxBus? = null

    var footerViewListener : FooterViewListener? = null
    var shippings: MutableList<ShippingQuote>? = null

    constructor(context: Context) : this(context, null) {
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {

        inflate(context, R.layout.footer_view, this)

        _rxBus = MainClass.getRxBus()

        shippingLayout = rootView?.findViewById(R.id.shipping_layout)
        shippingBase = rootView?.findViewById(R.id.shipping_base)
        shippingImg = rootView?.findViewById(R.id.shipping_img) as? SquareImageView
        shippingCount = rootView?.findViewById(R.id.shipping_count) as? TextView
        shippingTxt = rootView?.findViewById(R.id.shipping_txt) as? TextView
        shippingMore = rootView?.findViewById(R.id.shipping_more)

        shippingContainer = rootView?.findViewById(R.id.shipping_container) as LinearLayout

        totalPrice = rootView?.findViewById(R.id.total_price) as TextView
        sellerMessage = rootView?.findViewById(R.id.seller_message) as EditText

        checkoutBtn = rootView?.findViewById(R.id.checkout_btn)
        historyBtn = rootView?.findViewById(R.id.history_btn)
        historyTxt = rootView?.findViewById(R.id.history_txt)

        com.primo.utils.setOnClickListener(this, checkoutBtn, historyBtn, historyTxt, shippingBase)

        itemHeight = resources.getDimensionPixelSize(R.dimen.shipping_item_height)
    }

    private fun changeViewState() {

        isExpanded = !isExpanded

        if (isExpanded) {
            shippingMore?.rotate(0f, DEFAULT_DEGREE)
            shippingContainer?.expandAnimation(itemHeight * (shippings?.size ?: 0))
        } else {
            shippingMore?.rotate(DEFAULT_DEGREE, 0f)
            shippingContainer?.collapseAnimation()
        }
    }

    private fun selectShipping(id: Int) {

        Log.d("Test", "select shipping")
        var shippingObj: ShippingQuote? = null

        val index = shippings.orEmpty().indexOf(ShippingQuote(shippingId = id))

        if (index >= 0)
            shippingObj = shippings?.get(index)

        if (shippingObj != null) {
            shippingImg?.setColorFilter(ContextCompat.getColor(context, R.color.color_red))
            shippingTxt?.text = shippingObj.shipping

            //Print shipping cost
            shippingCount?.text = "$currency ${shippingObj.rate.toStringWithoutZeros()}"

            //Print total cost
            totalPrice?.text = "$currency ${(cost + shippingObj.rate).round(2).toStringWithoutZeros()}"
            footerViewListener?.onCostChanged((cost + shippingObj.rate).round(2), 1)
        }
    }

    fun updateShipping() {

        if (shippings != null && !(shippings?.isEmpty() ?: true)) {

            //shippingMore?.visibility = VISIBLE
            //fillShippingContainer()

            if ((shippings?.size ?: 0) > 0)
                selectShipping(shippings?.get(0)?.shippingId ?: -1)

        } else {
            shippingImg?.clearColorFilter()
            shippingMore?.visibility = INVISIBLE
        }
    }

    fun recalculate(productList : MutableList<CartItem>?) {

        Log.d("Test", "=====recaluate price")
        val size = productList?.size ?: 0
        var quantity = 0

        val isFilled = size > 0

        shippingLayout?.visibility = if (isFilled) View.VISIBLE else View.GONE

        cost = 0.0
        shippingCost = 0.0
        currency = ""

        if (isFilled) {
            for (product in productList.orEmpty()) {

                if (product.status != ADD_TO_WISHLIST) {
                    Log.d("Test", "=== product:" + product)
                    Log.d("Test", "=== total shipping:" + product.total_shipping + ", total price:" + product.total_price + ", total discount:" + product.total_discount)
                    //because added new attribute: total_price, total_shipping, total_discount
                    shippingCost += product.total_shipping
                    cost += ( product.total_price - product.total_discount)
                    currency = getCurrency(product.currency)
                }
                quantity += product.quantity
            }
        }

        cost = cost.round(2)
        Log.d("Test", "recalculate cost" + cost)
        footerViewListener?.onCostChanged(cost + shippingCost, productList!!.size)

        shippingCount?.text = "$currency ${shippingCost.toStringWithoutZeros()}"
        totalPrice?.text = "$currency ${(cost + shippingCost).toStringWithoutZeros()}"

        //send event of recalculated cost
        _rxBus?.send(RxEvent(Events.CHANGE_COST, Triple(quantity, cost, currency)))
    }

    fun fillShippingContainer() {

        val inflater =  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val size = shippings?.size?: 0

        shippingContainer?.removeAllViews()

        for (i in 0..size - 1) {

            val subView = inflater.inflate(R.layout.shipping_item, shippingContainer, false)
            val shippingObj = shippings?.get(i)

            with (shippingObj as ShippingQuote) {

                val title: TextView? = subView?.findViewById(R.id.shipping_title) as? TextView
                val price: TextView? = subView?.findViewById(R.id.shipping_price) as? TextView

                title?.text = shipping
                price?.text = "US$ $rate"
                subView.tag = shippingId
            }

            subView.setOnClickListener(this)
            shippingContainer?.addView(subView)
        }
    }

    override fun onClick(v: View?) {

        when(v?.id) {

            R.id.checkout_btn -> footerViewListener?.onCheckoutClick(sellerMessage?.text.toString())

            R.id.history_btn,
            R.id.history_txt -> footerViewListener?.onHistoryClick()

            //R.id.shipping_base -> changeViewState()
        }

        /*if (v?.tag != null) {
            changeViewState()
            selectShipping(v?.tag as? Int ?: -1)
        }*/
    }

    interface FooterViewListener {

        fun onCheckoutClick(message : String)

        fun onHistoryClick()

        //add quantity parameter, determine product exist by quantity, not price
        fun onCostChanged(cost: Double, quantity: Int)
    }
}
