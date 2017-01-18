package com.primo.utils.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.NumberPicker

import com.primo.R
import com.primo.utils.setDividerColor

import java.util.Calendar

@SuppressLint("InflateParams")
class MonthYearPicker
/**
 * Instantiates a new month year picker.

 * @param activity
 * *            the activity
 */
(private val activity: Activity) {

    private val view: View
    private var builder: AlertDialog.Builder? = null
    private var pickerDialog: AlertDialog? = null
    private var build = false
    private var monthNumberPicker: NumberPicker? = null
    private var yearNumberPicker: NumberPicker? = null

    init {
        this.view = activity.layoutInflater.inflate(R.layout.month_year_picker_view, null)
    }

    /**
     * Builds the month year alert dialog.

     * @param positiveButtonListener
     * *            the positive listener
     * *
     * @param negativeButtonListener
     * *            the negative listener
     */
    fun build(positiveButtonListener: DialogInterface.OnClickListener, negativeButtonListener: DialogInterface.OnClickListener?) {
        this.build(-1, -1, positiveButtonListener, negativeButtonListener)
    }

    /**
     * Gets the current year.

     * @return the current year
     */
    var currentYear: Int = 0
        private set

    /**
     * Gets the current month.

     * @return the current month
     */
    var currentMonth: Int = 0
        private set

    /**
     * Builds the month year alert dialog.

     * @param selectedMonth
     * *            the selected month 0 to 11 (sets current moth if invalid
     * *            value)
     * *
     * @param selectedYear
     * *            the selected year 1970 to 2099 (sets current year if invalid
     * *            value)
     * *
     * @param positiveButtonListener
     * *            the positive listener
     * *
     * @param negativeButtonListener
     * *            the negative listener
     */
    fun build(selectedMonth: Int, selectedYear: Int, positiveButtonListener: DialogInterface.OnClickListener,
              negativeButtonListener: DialogInterface.OnClickListener?) {
        var selectedMonth = selectedMonth
        var selectedYear = selectedYear

        val instance = Calendar.getInstance()
        currentMonth = instance.get(Calendar.MONTH)
        currentYear = instance.get(Calendar.YEAR)

        if (selectedMonth > 11 || selectedMonth < -1) {
            selectedMonth = currentMonth
        }

        if (selectedYear < MIN_YEAR || selectedYear > MAX_YEAR) {
            selectedYear = currentYear
        }

        if (selectedMonth == -1) {
            selectedMonth = currentMonth
        }

        if (selectedYear == -1) {
            selectedYear = currentYear
        }

        builder = AlertDialog.Builder(activity, R.style.DialogTheme)
        builder?.setView(view)

        monthNumberPicker = view.findViewById(R.id.monthNumberPicker) as NumberPicker
        monthNumberPicker?.displayedValues = PICKER_DISPLAY_MONTHS_NAMES

        monthNumberPicker?.minValue = 0
        monthNumberPicker?.maxValue = MONTHS.size - 1

        yearNumberPicker = view.findViewById(R.id.yearNumberPicker) as NumberPicker
        yearNumberPicker?.minValue = MIN_YEAR
        yearNumberPicker?.maxValue = MAX_YEAR

        monthNumberPicker?.value = selectedMonth
        yearNumberPicker?.value = selectedYear

        monthNumberPicker?.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        yearNumberPicker?.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        builder?.setTitle((activity.resources.getString(R.string.card_exp)).toUpperCase())
        builder?.setPositiveButton(activity.resources.getString(R.string.ok), positiveButtonListener)
        builder?.setNegativeButton(activity.resources.getString(R.string.cancel), negativeButtonListener)
        build = true
        pickerDialog = builder?.create()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            monthNumberPicker?.setDividerColor(ContextCompat.getColor(activity, R.color.color_red))
            yearNumberPicker?.setDividerColor(ContextCompat.getColor(activity, R.color.color_red))
        }
    }

    /**
     * Show month year picker dialog.
     */
    fun show() {
        if (build) {
            pickerDialog!!.show()
        } else {
            throw IllegalStateException("Build picker before use")
        }
    }

    /**
     * Gets the selected month.

     * @return the selected month
     */
    val selectedMonth: Int
        get() = monthNumberPicker!!.value

    /**
     * Gets the selected month name.

     * @return the selected month name
     */
    val selectedMonthName: String
        get() = MONTHS[monthNumberPicker!!.value]

    /**
     * Gets the selected month name.

     * @return the selected month short name i.e Jan, Feb ...
     */
    val selectedMonthShortName: String
        get() = PICKER_DISPLAY_MONTHS_NAMES[monthNumberPicker!!.value]

    /**
     * Gets the selected year.

     * @return the selected year
     */
    val selectedYear: Int
        get() = yearNumberPicker!!.value

    /**
     * Sets the month value changed listener.

     * @param valueChangeListener
     * *            the new month value changed listener
     */
    fun setMonthValueChangedListener(valueChangeListener: NumberPicker.OnValueChangeListener) {
        monthNumberPicker!!.setOnValueChangedListener(valueChangeListener)
    }

    /**
     * Sets the year value changed listener.

     * @param valueChangeListener
     * *            the new year value changed listener
     */
    fun setYearValueChangedListener(valueChangeListener: NumberPicker.OnValueChangeListener) {
        yearNumberPicker!!.setOnValueChangedListener(valueChangeListener)
    }

    /**
     * Sets the month wrap selector wheel.

     * @param wrapSelectorWheel
     * *            the new month wrap selector wheel
     */
    fun setMonthWrapSelectorWheel(wrapSelectorWheel: Boolean) {
        monthNumberPicker!!.wrapSelectorWheel = wrapSelectorWheel
    }

    /**
     * Sets the year wrap selector wheel.

     * @param wrapSelectorWheel
     * *            the new year wrap selector wheel
     */
    fun setYearWrapSelectorWheel(wrapSelectorWheel: Boolean) {
        yearNumberPicker!!.wrapSelectorWheel = wrapSelectorWheel
    }

    companion object {

        private val MIN_YEAR = 1970

        private val MAX_YEAR = 2099

        private val PICKER_DISPLAY_MONTHS_NAMES = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

        private val MONTHS = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    }

}