/**
 * Changes:
 *
 * - distinguish Simplified or Traditional Chinese
 * - get timezone
 * - get country code
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.utils

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import com.primo.R
import com.primo.database.OrderDB
import com.primo.database.OrderDBImpl
import com.primo.main.MainClass
import com.primo.utils.interfaces.OnReceiveLocationListener
import ru.solodovnikov.rxlocationmanager.kotlin.LocationTime
import ru.solodovnikov.rxlocationmanager.kotlin.RxLocationManager
import rx.Subscriber
import java.io.IOException
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.security.MessageDigest
import java.text.DecimalFormat
import java.util.*

fun Double.round(places: Int): Double {
    if (places < 0) throw IllegalArgumentException()
    var bd = BigDecimal(this)
    bd = bd.setScale(places, RoundingMode.HALF_UP)
    return bd.toDouble()
}

fun dateFormat(month: String?, year: String?): String {

    val _month = month ?: "01"
    val _year = year ?: Calendar.getInstance().get(Calendar.YEAR).toString()
    val yearFormatted = if (_year.length > 2) _year.substring(2, _year.length) else _year
    val monthFormatted = if (_month.length == 1) "0$_month" else _month
    return "$monthFormatted/$yearFormatted"
}

fun getDateFromField(str: String): Point {

    val calendar = Calendar.getInstance()

    val date = Point(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR))

    val exp = str.split("/")

    try {

        val month = exp[0].toInt()
        val year = calendar.get(Calendar.YEAR).toString().substring(0, 2).toInt() * 100 + exp[1].toInt()

        date.set(month, year)

    } catch (ex: Exception) {
        ex.printStackTrace()
        return date
    }

    return date
}

fun isValidEmail(value: String): Boolean {
    return !(!value.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches())
}

fun getWindowsSize(context: Context): Point {

    val width = context.resources.displayMetrics.widthPixels
    val height = context.resources.displayMetrics.heightPixels
    val size = Point()
    size.set(width, height)

    return size
}

fun getSizeWithoutToolbar(activity: Activity?, height: Int): Int {

    val tv = TypedValue();

    var newHeight = height

    if (activity?.theme?.resolveAttribute(R.attr.actionBarSize, tv, true) ?: false) {
        newHeight -= TypedValue.complexToDimensionPixelSize(tv.data, activity?.resources?.displayMetrics)
    }

    return height
}

fun isValidCard(ccNumber: String): Boolean {

    if (ccNumber.isEmpty())
        return false

    var sum = 0
    var alternate = false
    for (i in ccNumber.length - 1 downTo 0) {
        var n = Integer.parseInt(ccNumber.substring(i, i + 1))
        if (alternate) {
            n *= 2
            if (n > 9) {
                n = n % 10 + 1
            }
        }
        sum += n
        alternate = !alternate
    }

    return sum % 10 == 0
}

fun isFieldsEmpty(vararg fields: String): Boolean {

    for (i in fields) {
        if (TextUtils.isEmpty(i))
            return true
    }

    return false
}

fun isValidDate(month: String, year: String): Boolean {

    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH) + 1
    val currentYear = calendar.get(Calendar.YEAR)

    try {

        val selectedMonth = month.toInt()
        val selectedYear = currentYear.toString().substring(0, 2).plus(year).toInt()

        if (currentYear > selectedYear)
            return false
        else if (currentMonth >= selectedMonth && currentYear == selectedYear)
            return false
        else
            return true

    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

fun String.toMD5(): String {

    val md5 = MessageDigest.getInstance("MD5")
    md5.update(this.toByteArray())
    val digest = md5.digest();
    val bigInt = BigInteger(1, digest)
    var hashtext = bigInt.toString(16)
    // Now we need to zero pad it if you actually want the full 32 chars.
    while (hashtext.length < 32) {
        hashtext = "0" + hashtext
    }

    return hashtext
}

fun getAndroidId() = Settings.Secure.getString(
        MainClass.context.contentResolver,
        Settings.Secure.ANDROID_ID)

fun List<Any>.toStringList(): List<String> {

    val stringList = mutableListOf<String>()

    for (i in this)
        stringList.add(i.toString())

    return stringList
}

fun getDeviceLanguage(): String {

    var language = Locale.ENGLISH.language
    val deviceLanguage = Locale.getDefault().language
    val str_device_language = Locale.getDefault().toString()

    when (deviceLanguage) {

        Locale.JAPAN.language -> language = deviceLanguage

        //Locale.CHINA.language -> language = "ch"
    }

    when (str_device_language){

        "zh_TW" -> language = "cht"
        "zh_CN" -> language = "ch"
    }

    return language
}

fun getCurrency(value: Int): String {

    when (value) {
        1 -> return "$"
        2 -> return "¥"
        3 -> return "¥"
        4 -> return "£"
        5 -> return "€"
        6 -> return "$"
        7 -> return "$"
        8 -> return "$"
        else -> return ""
    }
}

//fun Double.toStringWithoutZeros() = BigDecimal(this.round(2).toString()).stripTrailingZeros().toPlainString()

fun Double.toStringWithoutZeros() : String {

    val formatter = DecimalFormat("#,###.##")
    return formatter.format(BigDecimal(this.round(2).toString()).stripTrailingZeros()).replace("\\.0^", "")
}


fun getLocation(context: Context, listener: OnReceiveLocationListener) {

    var lat = 0f
    var lng = 0f

    RxLocationManager(context).getLastLocation(LocationManager.NETWORK_PROVIDER, LocationTime.OneHour()).subscribe(object : Subscriber<Location>() {

        override fun onCompleted() {
            listener.onReceiveLocation(Pair(lat, lng))
        }

        override fun onNext(location: Location?) {
            Log.d("Test", " ===== location:" + location)

            if (location != null && location.latitude != 0.0 && location.longitude != 0.0) {

                lat = location.latitude.toFloat()
                lng = location.longitude.toFloat()

                // == get user's country code == //
                var countryName = ""
                val gcd = Geocoder(context, Locale.getDefault())
                val addresses: List<Address>
                try {
                    addresses = gcd.getFromLocation(lat.toDouble(), lng.toDouble(), 1)

                    if (!addresses.isEmpty()) {
                        countryName = addresses[0].countryName
                        countryName = "Japan"

                        val orderDB: OrderDB? = OrderDBImpl()
                        val countryCode = orderDB?.getCountryByName(countryName)?.value ?: -1
                        Log.d("Test", " === country name: " + countryName + "" + countryCode)
                        MainClass.saveUserCountry(countryCode.toString())
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
                // == end == //
            }
        }

        override fun onError(p0: Throwable?) {
            p0?.printStackTrace()
        }
    });

}

fun getTimeZone(){
    val tz = TimeZone.getDefault()
    Log.d("Test", "TimeZone   " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.id)
}
