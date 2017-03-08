/**
 * Changes:
 *
 * - change custom dialog
 * - add turn on camera button
 * - control toolbar
 * - fix price for 0 stock item on camera page
 * - update user language
 * - check product and setting before checkout
 * - integrate Count api
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.goods.fragments

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.kaopiz.kprogresshud.KProgressHUD
import com.primo.R
import com.primo.goods.mvp.GoodsScannerPresenter
import com.primo.goods.mvp.GoodsScannerView
import com.primo.goods.mvp.GoodsScannerPresenterImpl
import com.primo.main.MainActivity
import com.primo.main.MainClass
import com.primo.network.new_models.Count
import com.primo.network.new_models.Product
import com.primo.profile.fragments.PageProfileFragment
import com.primo.utils.*
import com.primo.utils.base.BasePresenterFragment
import com.primo.utils.consts.*
import com.primo.utils.interfaces.OnReceiveLocationListener
import com.primo.utils.other.Events
import com.primo.utils.other.RxBus
import com.primo.utils.other.RxEvent
import rx.subscriptions.CompositeSubscription


class GoodsScannerFragment : BasePresenterFragment<GoodsScannerView, GoodsScannerPresenter>(), GoodsScannerView, View.OnClickListener, SensorEventListener, Runnable, OnReceiveLocationListener {

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

    private var turnonCameraBtn: Button? = null
    private var turnCameraLayout: LinearLayout? = null

    private var barcodeView: CompoundBarcodeView? = null
    private var infoTxt: TextView? = null
    private var totalContainer: View? = null
    private var totalPrice: TextView? = null
    private var count: TextView? = null
    private var countArrow: View? = null
    private var progress: View? = null

    private var preview: View? = null
    private var previewImage: SimpleDraweeView? = null
    private var previewTitle: TextView? = null
    private var previewPrice: TextView? = null

    private var isProductExist = false

    private var _rxBus: RxBus? = null
    private var _subscriptions: CompositeSubscription? = null

    private var isFirstRun: Boolean = false
    var TAG = "matata"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (rootView == null) {
            rootView = inflater!!.inflate(R.layout.goods_scanner_fragment, container, false)

            _rxBus = MainClass.getRxBus()

            init()
            initScanner()
            initPresenter()
            isFirstRun = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPermission()
            } else {
                presenter?.setPermission(true)
            }

            (activity as MainActivity).showToolbar(false)
            Log.d("Test", "=======Scanner Fragment create")

        }

        return rootView
    }

    private fun init() {

        turnonCameraBtn = rootView?.findViewById(R.id.turn_camera_btn) as Button
        turnCameraLayout = rootView?.findViewById(R.id.turn_camera_layout) as LinearLayout

        val galleryImg = rootView?.findViewById(R.id.gallery_img)
        val checkoutBtn = rootView?.findViewById(R.id.checkout_btn)
        progress = rootView?.findViewById(R.id.progress)
        infoTxt = rootView?.findViewById(R.id.info_txt) as TextView
        totalContainer = rootView?.findViewById(R.id.total_container)
        totalPrice = rootView?.findViewById(R.id.total_price) as TextView

        preview = rootView?.findViewById(R.id.preview)
        previewImage = rootView?.findViewById(R.id.preview_image) as SimpleDraweeView
        previewTitle = rootView?.findViewById(R.id.preview_title) as TextView
        previewPrice = rootView?.findViewById(R.id.preview_price) as TextView

        count = rootView?.findViewById(R.id.count) as TextView
        countArrow = rootView?.findViewById(R.id.count_arrow)

        //=====
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorAccel = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        //=====

        setOnClickListener(this, galleryImg, checkoutBtn, turnonCameraBtn, count, countArrow)
    }

    private fun initScanner() {

        barcodeView = rootView?.findViewById(R.id.barcode_view) as CompoundBarcodeView
        barcodeView?.statusView?.visibility = View.GONE
        barcodeView?.viewFinder?.visibility = View.GONE
    }

    override fun initPresenter() {

        presenter = GoodsScannerPresenterImpl(this, barcodeView)
    }

    private fun checkPermission() {

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CAMERA)) {

                showMessage(getString(R.string.access_to_camera))

                //visible turn on camera button
                turnCameraLayout?.setVisibility(View.VISIBLE);

            } else {

                ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission.CAMERA), PERMISSION_CAMERA_REQUEST)

            }
        } else {
            presenter?.setPermission(true)

            val country_code = MainClass.getSavedCountry()
            if (country_code.isEmpty())
                getUserCountry()
        }
    }

    private fun changeTotalStateFromCountAPI(amount: Triple<Int, Double, Int>) {

        val total = getCurrency(amount.first) + " " + amount.second.toString()
        totalPrice?.text = total
    }

    private fun changeTotalState(amount: Triple<Int, Double, String>) {

        isProductExist = amount.first > 0 // determine product exist by quantity, not price
        infoTxt?.visibility = if (isProductExist) View.INVISIBLE else View.VISIBLE
        totalContainer?.visibility = if (isProductExist) View.VISIBLE else View.INVISIBLE

        // == replace by calling count api to update total price == //
        //totalPrice?.post({ totalPrice?.text = "${amount.third} ${amount.second.round(2).toStringWithoutZeros()}" })

        if (!isProductExist)
            count?.visibility = View.INVISIBLE
        else
            count?.visibility = View.VISIBLE

        // == replace by calling count api to update cart badge == //
        //count?.text = amount.first.toString()
        val auth = MainClass.getAuth()
        val token = auth.access_token

        if (token.isEmpty())
            presenter?.getPublicCount()
        else
            presenter?.getLiveCount()

        count?.bouncingAnimation()

        // check Product Before SignUp as Cart fragment
        (activity as MainActivity).changeProfileTabState(MainActivity.ProfileTabStates.INVISIBLE)

        if (isProductExist && amount.second > 0) //normal
            (activity as MainActivity).changeProfileTabState(MainActivity.SignUpStates.NORMAL)
        else if (isProductExist && amount.second == 0.0) //0 stock item
            (activity as MainActivity).changeProfileTabState(MainActivity.SignUpStates.NOCC)
        else //no product
            (activity as MainActivity).changeProfileTabState(MainActivity.SignUpStates.BASIC)
    }

    override fun getCountResult(counts: Count) {
        count?.text = counts.cart_count.toString()
        val total = getCurrency(counts.currency) + " " + counts.total_final_price.toString()
        totalPrice?.text = total
    }

    override fun showProgress() {
        progress?.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress?.visibility = View.GONE
    }

    override fun addProduct(product: Product?) {

        if (product != null) {
            _rxBus?.send(RxEvent(Events.ADD_PRODUCT, product))
            previewImage?.setImageURI(Uri.parse(product.defaultImage))
            previewTitle?.text = product.productName
            previewPrice?.text = "${getCurrency(product.currency)} ${product.price.toStringWithoutZeros()}"
            preview?.previewAnimation()
        }
    }

    override fun showMessage(message: String?, event: RxEvent?) {
        showDialog(message = message, event = event)
    }

    override fun displayErrorMessage(message : String?, code: Int?, event: RxEvent?){
        showErrorDialog(message, code)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.gallery_img -> getPhotoFromGalleryIntent(this, GALLERY_RESULT)
            R.id.checkout_btn -> {

                val auth = MainClass.getAuth()
                val token = auth.access_token

                if (isProductExist && token.isEmpty()) {

                    (activity as MainActivity).showMainTabbar(true)
                    showFragment(PageProfileFragment(), true,
                            R.anim.left_center, R.anim.center_right, R.anim.right_center, R.anim.center_left, PageProfileFragment::class.java.simpleName)

                } else if (isProductExist) {

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

                } else {
                    showDialog(getString(R.string.please_choose_at_least_one_product))
                }
            }
            R.id.count, R.id.count_arrow -> {
                Log.d("Test", "==== click count arrow")
                val parent = parentFragment

                if (parent != null && parent is GoodsPagerFragment) {
                    parent.slideNext()
                }
            }
            R.id.turn_camera_btn -> ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.CAMERA), PERMISSION_CAMERA_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_RESULT) {

            val imageUri = data?.data;
            if (imageUri != null)
                presenter?.onDecodeImage(imageUri)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            presenter?.onResumeScanning()
            count?.bouncingAnimation()
        } else
            presenter?.onStopScanning()
    }

    override fun onResume() {
        super.onResume()

        if (isFirstRun) {
            MainClass.getRxBus()?.send(RxEvent(Events.COST_REQUEST, 0))
            isFirstRun = false
        }

        sensorManager?.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onStart() {
        super.onStart()

        _subscriptions = CompositeSubscription();
        _subscriptions?.add(_rxBus?.toObserverable()
                ?.subscribe({

                    Log.d("Test", "Scan fragment get event" + it.key)
                    when (it.key) {

                        Events.QR_NOT_FOUND_DIALOG_CLOSE -> presenter?.onResumeScanning()

                        Events.CHANGE_COST -> changeTotalState(it.sentObject as Triple<Int, Double, String>)

                        Events.CHANGE_COUNT -> changeTotalStateFromCountAPI(it.sentObject as Triple<Int, Double, Int>)

                        Events.CAMERA_PERMISSION -> {

                            if (it.sentObject is Int && it.sentObject == 0) {
                                presenter?.setPermission(true)
                                turnCameraLayout?.setVisibility(View.INVISIBLE)
                            } else {
                                presenter?.setPermission(false)
                                showMessage(getString(R.string.access_to_camera))

                                //visible turn on camera button
                                turnCameraLayout?.setVisibility(View.VISIBLE)
                                Log.d(TAG, "========== camera permission false===========")
                            }

                            //call location permission after camera permission
                            getUserCountry()
                        }
                    }
                }));

        //update user language
        val current_language = getDeviceLanguage()
        if (MainClass.getSavedLanguage() != current_language)
            presenter?.updateUserLanguage(current_language)
    }

    override fun onStop() {
        super.onStop()
        _subscriptions?.clear();
    }

    override fun onPause() {
        super.onPause()

        sensorManager?.unregisterListener(this)
    }

    fun getUserCountry(){

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_LOCATION_REQUEST)
        } else {
            Log.d("Test", " == getting location from Scan fragment")
            getLocation(context.applicationContext, this)
        }
    }

    override fun onReceiveLocation(location: Pair<Float, Float>) {

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

    override fun onCheckShippingCardBeforeCheckout(result: Array<String?>) {

        if (result[0] == "") {
            showMessage(MainClass.context.getString(R.string.please_add_shipping_address))
            (activity as MainActivity).changeProfileTabState(MainActivity.ProfileTabStates.PROFILE_PAGE)
            (activity as MainActivity).showMainTabbar(true)
            showFragment(PageProfileFragment(), true,
                    R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageProfileFragment::class.java.simpleName)
        }
        else if (result[1] == "") {
            showMessage(MainClass.context.getString(R.string.please_add_credit_card))
            (activity as MainActivity).changeProfileTabState(MainActivity.ProfileTabStates.PROFILE_PAGE)
            (activity as MainActivity).showMainTabbar(true)
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

    private fun orderPlace() {
        presenter?.checkShippingCardBeforeCheckout()
    }
}