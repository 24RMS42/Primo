/**
 * Changes:
 *
 * - 503 HTTP status handling
 * - Implement Count api integration
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.goods.mvp

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.util.Log
import com.google.zxing.*
import com.google.zxing.common.GlobalHistogramBinarizer
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.primo.R
import com.primo.main.MainClass
import com.primo.network.api_new.*
import com.primo.network.new_models.Count
import com.primo.network.new_models.Product
import com.primo.utils.LONG_DURATION
import com.primo.utils.NORMAL_DURATION
import com.primo.utils.consts.*
import com.primo.utils.getInt
import com.primo.utils.other.Events
import com.primo.utils.other.RxEvent
import org.json.JSONObject
import java.io.FileNotFoundException


class GoodsScannerPresenterImpl(view: GoodsScannerView, barcodeView: CompoundBarcodeView?) : GoodsScannerPresenter(view), BarcodeCallback {

    private var barcodeView: CompoundBarcodeView? = barcodeView
    private var isLoading: Boolean = false
    private var isCameraPermissionGranted = false

    init {
        barcodeView?.decodeContinuous(this)
    }

    override fun setPermission(isGranted: Boolean) {
        isCameraPermissionGranted = isGranted
            barcodeView?.resume()
    }

    override fun barcodeResult(result: BarcodeResult?) {

        if (!isLoading) {
            isLoading = true
            getProductByResult(result?.text)
        }
    }

    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
    }

    override fun onDecodeImage(uri: Uri) {

        try {

            val inputStream = barcodeView?.context?.contentResolver?.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            if (bitmap == null)
            {
                print("uri is not a bitmap," + uri.toString())
            }
            val width = bitmap.width
            val height = bitmap.height

            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            bitmap.recycle()
            val source = RGBLuminanceSource(width, height, pixels)
            val bBitmap = BinaryBitmap(GlobalHistogramBinarizer(source))

            val reader = MultiFormatReader()

            try {
                val result = reader.decode(bBitmap)
                getProductByResult(result?.text)
            }
            catch (e : NotFoundException)
            {
                e.printStackTrace()
            }
            catch (e: OutOfMemoryError){
                e.printStackTrace()
            }
        } catch (e : FileNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onResumeScanning() {
        isLoading = false
    }

    override fun onStopScanning() {
        isLoading = true
    }

    private fun getProductByResult(resultStr: String?) {

        val code : String?

        if (resultStr?.contains(QR_PREFIX) == true) {
            view?.showProgress()
            val tempStr = resultStr?.removePrefix(QR_PREFIX)
            code = tempStr
        } else {
            view?.showMessage(MainClass.context.getString(R.string.there_is_no_such_product),
                    RxEvent(key = Events.QR_NOT_FOUND_DIALOG_CLOSE))
            return
        }

        println(code)

        val searchCall: SearchProductByQr = SearchProductByQrImpl(object: ApiResult<Product?> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: Product?) {
                if (!(result?.productId ?: "").isEmpty()) {
                    view?.addProduct(result)
                    val delay = Handler()
                    Log.d("Test", result.toString())
                    delay.postDelayed({ isLoading = false }, NORMAL_DURATION + LONG_DURATION + LONG_DURATION + NORMAL_DURATION)
                } else {
                    view?.showMessage(MainClass.context.getString(R.string.there_is_no_such_product),
                            RxEvent(key = Events.QR_NOT_FOUND_DIALOG_CLOSE))
                }
            }

            override fun onError(message: String, code: Int) {
                Log.d("Test", "search product error:" + message)
                Log.d("Test", "http code:" + code.toString())
                view?.hideProgress()
                view?.showErrorMessage(message)
                when(code) {

                    NOT_FOUND_ERROR -> view?.showMessage(MainClass.context.getString(R.string.there_is_no_such_product),
                            RxEvent(key = Events.QR_NOT_FOUND_DIALOG_CLOSE))

                    CONNECTION_ERROR -> view?.showMessage(MainClass.context.getString(R.string.please_check_your_internet_connection),
                            RxEvent(key = Events.QR_NOT_FOUND_DIALOG_CLOSE))

                    UNAVAILABLE_ERROR -> view?.showMessage(MainClass.context.getString(R.string.performing_system_maintenance),
                            RxEvent(key = Events.QR_NOT_FOUND_DIALOG_CLOSE))

                    INTERNAL_ERROR -> view?.showMessage(MainClass.context.getString(R.string.something_went_wrong_try_again),
                            RxEvent(key = Events.QR_NOT_FOUND_DIALOG_CLOSE))

                    else -> onResumeScanning()
                }
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        if (code != null)
            searchCall.searchProductByQr(code)
    }

    override fun updateUserLanguage(language: String) {

        val auth = MainClass.getAuth()
        val token = auth.access_token

        if (!token.isEmpty()) {

            val updateUserLanguageCall: UpdateUserLanguage = UpdateUserLanguageImpl(object : ApiResult<String> {

                override fun onStart() {

                }

                override fun onResult(result: String) {
                    Log.d("Test", "language update success:" + result)
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("Test", "language update error:" + message)
                }

                override fun onComplete() {

                }
            })

            updateUserLanguageCall.updateUserLanguage(language, token)
        }
    }

    override fun checkShippingCardBeforeCheckout() {

        val auth = MainClass.getAuth()
        val token = auth.access_token

        val checkShippingCardCall: CheckShippingCard = CheckShippingCardImpl(object : ApiResult<Array<String?>> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: Array<String?>) {

                view?.onCheckShippingCardBeforeCheckout(result)
            }

            override fun onError(message: String, code: Int) {
                view?.showErrorMessage(message)

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        checkShippingCardCall.checkShippingCard(token)
    }

    override fun getPublicCount() {

        val auth = MainClass.getAuth()
        val token = auth.access_token

        val getPublicCountCall: GetCount = GetPublicCountImpl(object : ApiResult<Count> {

            override fun onStart() {}

            override fun onResult(result: Count) {

                view?.getCountResult(result)
            }

            override fun onError(message: String, code: Int) {
                Log.d("Test", "get public count error:" + message)
                displayMessage(message, code)
            }

            override fun onComplete() {}
        })

        getPublicCountCall.getCount(token)
    }

    override fun getLiveCount() {

        val auth = MainClass.getAuth()
        val token = auth.access_token

        val getLiveCountCall: GetCount = GetLiveCountImpl(object : ApiResult<Count> {

            override fun onStart() {}

            override fun onResult(result: Count) {

                view?.getCountResult(result)
            }

            override fun onError(message: String, code: Int) {
                Log.d("Test", "get live count error:" + message)
                displayMessage(message, code)
            }

            override fun onComplete() {}
        })

        getLiveCountCall.getCount(token)
    }

    override fun onResume() {
        super.onResume()
        println("onResume")
        if (isCameraPermissionGranted)
            barcodeView?.resume()
    }

    override fun onPause() {
        super.onPause()
        println("onPause")
        if (isCameraPermissionGranted)
            barcodeView?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        barcodeView = null
    }

    fun displayMessage(message: String, code: Int){

        var codeError = -1
        val jsonObject = JSONObject(message)
        codeError = jsonObject.getInt("error_code", -1)

        view?.displayErrorMessage("", codeError)
    }

}
