/**
 * Changes:
 *
 * - Add Deeplink feature
 * - Show Reject Fragment
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.goods.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.primo.R
import com.primo.goods.mvp.GoodsPagerPresenter
import com.primo.goods.mvp.GoodsPagerPresenterImpl
import com.primo.goods.mvp.GoodsPagerView
import com.primo.main.MainActivity
import com.primo.main.MainClass
import com.primo.utils.base.BasePresenterFragment
import com.primo.utils.base.BaseViewPagerAdapter
import com.primo.utils.consts.PERMISSION_LOCATION_REQUEST
import com.primo.utils.getLocation
import com.primo.utils.hideKeyboard
import com.primo.utils.interfaces.OnReceiveLocationListener
import com.primo.utils.other.Events
import com.primo.utils.other.RxBus
import com.primo.utils.other.RxEvent
import com.primo.utils.views.VerticalViewPager
import rx.subscriptions.CompositeSubscription

class GoodsPagerFragment : BasePresenterFragment<GoodsPagerView, GoodsPagerPresenter>(), GoodsPagerView, OnReceiveLocationListener {

    private var pager: VerticalViewPager? = null

    private var _rxBus: RxBus? = null
    private var _subscriptions: CompositeSubscription? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (rootView == null) {
            rootView = inflater?.inflate(R.layout.goods_pager_fragment, container, false)

            initSwipe()

            pager = rootView?.findViewById(R.id.pager) as VerticalViewPager

            val pagerAdapter = BaseViewPagerAdapter(childFragmentManager)
            pagerAdapter.addFragment(GoodsScannerFragment())
            pagerAdapter.addFragment(GoodsTotalFragment())

            pager?.adapter = pagerAdapter
            pager?.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {

                    if (position == 1){
                        (activity as MainActivity).showMainTabbar(true)
                        (activity as MainActivity).changeTabbarState(MainActivity.TabbarStates.CART)
                    }
                    else
                        (activity as MainActivity).showMainTabbar(false)

                    super.onPageSelected(position)
                    activity.hideKeyboard()
                }
            })

            _rxBus = MainClass.getRxBus()

            initPresenter()
        }

        return rootView
    }

    override fun initPresenter() {
        presenter = GoodsPagerPresenterImpl(this)
    }

    override fun onStart() {
        super.onStart()

        Log.d("Test", "onStart")

        _subscriptions = CompositeSubscription();
        _subscriptions?.add(_rxBus?.toObserverable()
                ?.subscribe({

                    Log.d("Test", "Pager get event" + it.key)
                    when (it.key) {

                        Events.CONFIRMED,
                        Events.SIGNED -> {
                            if (pager?.currentItem == 0)
                                slideNext()
                        }
                        Events.LOCATION_PERMISSION -> {

                            Log.d("Test", "LOCATION_PERMISSION")

                            getLocation(context.applicationContext, this)
                            if (it.sentObject is Int && it.sentObject == 0) {
                                //getOrderPlace()
                            } else {
                                //getOrderPlace(false)
                            }
                        }
                    }
                }));

        val pair = (activity as MainActivity).getDeeplinkData()
        if (pair.first != "" && pair.second != "")
            slideNext()

        (activity as MainActivity).showMainTabbar(false)

        //when click camera tab
        val pageState = (activity as MainActivity).getPageState()
        if (pageState == 1)
            slideNext()
    }

    fun slideNext() {
        (activity as MainActivity).showMainTabbar(true)
        pager?.currentItem = 1
    }

    fun slidePrevious() {
        pager?.currentItem = 0
    }

    fun getOrderPlace(withLocation: Boolean = true) {

        Log.d("TEST", "getOrderPlace")

        if (withLocation) {

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                presenter?.placeAnOrder()

            } else {
                presenter?.placeAnOrder()
            }
        }
    }

    override fun onReceiveLocation(location: Pair<Float, Float>) {

    }

    override fun onBought(isSuccess: Boolean) {
        val activity = activity
        if (activity is MainActivity)
            activity.showThankLayout(isSuccess)

        if (pager?.currentItem == 1)
            slidePrevious()

        MainClass.getRxBus()?.send(RxEvent(Events.UPDATE_PRODUCTS))
    }

    override fun showProgress() {
        swipe?.post({ swipe?.isRefreshing = true })
    }

    override fun hideProgress() {
        swipe?.post({ swipe?.isRefreshing = false })
    }

    override fun showMessage(message: String?, event: RxEvent?) {

        //If reject case, it shows Reject fragment
        if (event?.key == Events.ORDER_REJECT) {

            //Clear Cart list
            MainClass.getRxBus()?.send(RxEvent(Events.UPDATE_PRODUCTS))

            //Set data to RejectFragment
            val bundle = Bundle()
            bundle.putString("body_data", message)
            bundle.putString("kind", "order_reject")
            val fragObj = RejectFragment()
            fragObj.arguments = bundle

            showFragment(fragObj, true,
                    R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, RejectFragment::class.java.simpleName)
        }
        else if (event?.key == Events.REJECT) {

            //Clear Cart list
            MainClass.getRxBus()?.send(RxEvent(Events.UPDATE_PRODUCTS))

            //Set data to RejectFragment
            val bundle = Bundle()
            bundle.putString("body_data", message)
            bundle.putString("kind", "reject")
            val fragObj = RejectFragment()
            fragObj.arguments = bundle

            showFragment(fragObj, true,
                    R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, RejectFragment::class.java.simpleName)
        }
        else
            showDialog(message)
    }

    override fun displayErrorMessage(message : String?, code: Int?, event: RxEvent?){
        showErrorDialog(message, code)
    }

    override fun onStop() {
        super.onStop()
        _subscriptions?.clear()
    }
}