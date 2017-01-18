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

                    when (it.key) {

                        Events.CONFIRMED,
                        Events.SIGNED -> {
                            if (pager?.currentItem == 0)
                                slideNext()
                        }
                        Events.LOCATION_PERMISSION -> {

                            Log.d("TEST", "LOCATION_PERMISSION")

                            if (it.sentObject is Int && it.sentObject == 0) {
                                getOrderPlace()
                            } else {
                                getOrderPlace(false)
                            }
                        }
                    }
                }));
    }

    fun slideNext() {
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

                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_LOCATION_REQUEST)
            } else {
                getLocation(context.applicationContext, this)
            }
        } else {
            presenter?.placeAnOrder()
        }
    }

    override fun onReceiveLocation(location: Pair<Float, Float>) {
        presenter?.placeAnOrder(location)
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
        showDialog(message)
    }

    override fun onStop() {
        super.onStop()
        _subscriptions?.clear()
    }
}