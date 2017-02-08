/**
 * Changes:
 *
 * - Displayed more specific error message
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.utils

import android.app.Activity
import android.app.FragmentManager
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.primo.R
import com.primo.main.MainClass
import com.primo.utils.consts.*
import com.primo.utils.other.RxEvent

fun AppCompatActivity.showFragment(fragment: Fragment, isAddToBackStack: Boolean = true,
                 enter: Int = 0, exit: Int = 0,
                 popEnter: Int = 0, popExit: Int = 0, tag: String = "") {

    val transaction = supportFragmentManager.beginTransaction()
    transaction.setCustomAnimations(enter, exit, popEnter, popExit)
    transaction.replace(R.id.container, fragment, tag)

    if (isAddToBackStack)
        transaction.addToBackStack(tag)

    transaction.commitAllowingStateLoss()
}

fun AppCompatActivity.showDialogFragment(dialog: DialogFragment, targetFragment: Fragment? = null) {

    val fragmentTransaction = supportFragmentManager.beginTransaction()
    if (targetFragment != null) dialog.setTargetFragment(targetFragment, 0)
    fragmentTransaction.add(dialog, null)
    fragmentTransaction.commitAllowingStateLoss()
}

fun AppCompatActivity.showAlert(title: String = getString(R.string.infromation), message: String?, code: Int?, event: RxEvent? = null) {

    var messageTitle = title
    var msg_content: String? = null
    Log.d("Test", "==== ERROR code ===" + code)
    when (code) {
        -2 -> msg_content = message

        // Product Error
        OUTOF_STOCK_ADD_TO_WISHLIST -> msg_content = MainClass.context.getString(R.string.product_is_out_of_stock_add_to_wishlist)

        //Payment Error
        STRIPE_NOT_FOUND              ->  msg_content = MainClass.context.getString(R.string.stripe_not_found)
        STRIPE_APPROVE_WITH_ID        ->  msg_content = MainClass.context.getString(R.string.stripe_approve_with_id)
        STRIPE_CARD_NOT_SUPPORTED     ->  msg_content = MainClass.context.getString(R.string.stripe_card_not_supported)
        STRIPE_CARD_VELOCITY_EXCEEDED ->  msg_content = MainClass.context.getString(R.string.stripe_card_velocity_exceeded)
        STRIPE_CURRENCY_NOT_SUPPORT   ->  msg_content = MainClass.context.getString(R.string.stripe_currency_not_supported)
        STRIPE_FRAUDULENT             ->  msg_content = MainClass.context.getString(R.string.stripe_fraudulent)
        STRIPE_INSUFFICIENT_FUNDS     ->  msg_content = MainClass.context.getString(R.string.stripe_insufficient_funds)
        STRIPE_INVALID_ACCOUNT        ->  msg_content = MainClass.context.getString(R.string.stripe_invalid_account)
        STRIPE_INVALID_AMOUNT         ->  msg_content = MainClass.context.getString(R.string.stripe_invalid_amount)
        STRIPE_INVALID_PIN            ->  msg_content = MainClass.context.getString(R.string.stripe_invalid_pin)
        STRIPE_ISSUER_NOT_AVAILABLE   ->  msg_content = MainClass.context.getString(R.string.stripe_issuer_not_available)
        STRIPE_LOST_CARD              ->  msg_content = MainClass.context.getString(R.string.stripe_lost_card)
        STRIPE_NEW_ACCOUNT_INFO_AVAILABLE      ->  msg_content = MainClass.context.getString(R.string.stripe_new_account_info_available)
        STRIPE_NOT_PERMITTED                   ->  msg_content = MainClass.context.getString(R.string.stripe_not_permitted)
        STRIPE_REENTER_TRANSACTION             ->  msg_content = MainClass.context.getString(R.string.stripe_reenter_transaction)
        STRIPE_TESTMODE_DECLINE                ->  msg_content = MainClass.context.getString(R.string.stripe_testmode_decline)
        STRIPE_TRY_AGAIN                       ->  msg_content = MainClass.context.getString(R.string.stripe_try_again_later)
        STRIPE_WITHDRAWAL_COUNT_LIMIT_EXCEEDED ->  msg_content = MainClass.context.getString(R.string.stripe_withdrawal_count_limit_exceeded)
        STRIPE_INVALID_NUMBER                  ->  msg_content = MainClass.context.getString(R.string.stripe_invalid_number)
        STRIPE_INVALID_EXPIRY_MONTH            ->  msg_content = MainClass.context.getString(R.string.stripe_invalid_expiry_month)
        STRIPE_INVALID_EXPIRY_YEAR             ->  msg_content = MainClass.context.getString(R.string.stripe_invalid_expiry_year)
        STRIPE_INVALID_CVC                     ->  msg_content = MainClass.context.getString(R.string.stripe_invalid_cvc)
        STRIPE_INVALID_SWIPE_DATA              ->  msg_content = MainClass.context.getString(R.string.stripe_invalid_swipe_data)
        STRIPE_INCORRECT_NUMBER                ->  msg_content = MainClass.context.getString(R.string.stripe_incorrect_number)
        STRIPE_EXPIRED_CARD                    ->  msg_content = MainClass.context.getString(R.string.stripe_expired_card)
        STRIPE_INCORRECT_CVC                   ->  msg_content = MainClass.context.getString(R.string.stripe_incorrect_cvc)
        STRIPE_INCORRECT_ZIP                   ->  msg_content = MainClass.context.getString(R.string.stripe_incorrect_zip)
        STRIPE_MISSING                         ->  msg_content = MainClass.context.getString(R.string.stripe_missing)
        STRIPE_PROCESSING_ERROR                ->  msg_content = MainClass.context.getString(R.string.stripe_processing_error)

        STRIPE_CALL_ISSUER, STRIPE_DO_NOT_HONOR, STRIPE_DO_NOT_TRY_AGAIN, STRIPE_GENERIC_DECLINE, STRIPE_NO_ACTION_TOKEN, STRIPE_PICKUP_CARD,
        STRIPE_RESTRICTED_CARD, STRIPE_REVOCATION_ALL_AUTHORIZATIONS, STRIPE_REVOCATION_AUTHORICATIONS, STRIPE_SECURITY_VIOLATION,
        STRIPE_SERVICE_NOT_ALLOWED, STRIPE_STOLEN_CARD, STRIPE_STOP_PAYMENT_ORDER, STRIPE_TRANSACTION_NOT_ALLOWED, STRIPE_CARD_DECLINED->
            msg_content = MainClass.context.getString(R.string.your_card_declined_contact_bank)

        //Payment Gateway Error
        STRIPE_KEY_NOT_EXIST, NOT_CONNECT_STRIPE, INVALID_REQUEST_STRIPE, AUTH_STRIPE_KEY_ERROR, OTHER_STRIPE_ERROR ->
            msg_content = MainClass.context.getString(R.string.something_went_wrong_fixing_error)
        CHECK_CARD_ERROR, CREATE_CUSTOMER_ERROR, UPDATE_CARD_CUSTOMER_ERROR, DELETE_CARD_CUSTOMER_ERROR, HIT_RATE_LIMIT_STRIPE_REQUEST ->
            msg_content = MainClass.context.getString(R.string.something_went_wrong_try_again)

        //General
        100001 -> msg_content = MainClass.context.getString(R.string.something_went_wrong_try_again)
        100002 -> msg_content = MainClass.context.getString(R.string.something_went_wrong_try_again)

        // API Client Error
        200001 -> msg_content = MainClass.context.getString(R.string.something_went_wrong_try_again)
        200002 -> msg_content = MainClass.context.getString(R.string.access_not_granted)
        200009 -> msg_content = MainClass.context.getString(R.string.hit_api_limit)
        300009 -> msg_content = MainClass.context.getString(R.string.try_another_email)
        ACCESS_NOT_GRANTED_DATA_IS_NOT_VALID -> msg_content = MainClass.context.getString(R.string.invalid_user_credentials)
        SERVICE_UNAVAILABLE_ERROR            -> msg_content = MainClass.context.getString(R.string.performing_system_maintenance)

        // User / Authentication
        201001 -> msg_content = MainClass.context.getString(R.string.user_not_found)

        // Shipping Address
        203001 -> msg_content = MainClass.context.getString(R.string.no_shipping_address)
        203005 -> msg_content = MainClass.context.getString(R.string.register_max_5_shipping_address)

        // Credit card
        204001 -> msg_content = MainClass.context.getString(R.string.no_cart_in_profile)

        // Wish list
        208001 -> msg_content = MainClass.context.getString(R.string.wishlist_not_found)

        // Merchant
        MERCHANT_NOT_FOUND,
        MERCHANT_NOT_SELL,
        MERCHANT_NOT_ACTIVE,
        MERCHANT_STRIPE_ACCOUNT_ERROR ->
            msg_content = MainClass.context.getString(R.string.product_not_available)

        // Database
        102001, 102002, 102003, 102004 ->
            msg_content = MainClass.context.getString(R.string.something_went_wrong_fixing_error)
    }

    if (TextUtils.isEmpty(msg_content))
        return

    if (code == SERVICE_UNAVAILABLE_ERROR){
        messageTitle = MainClass.context.getString(R.string.maintenance)
    }

    val builder = AlertDialog.Builder(this, R.style.DialogTheme)
    builder.setTitle(messageTitle)
    builder.setMessage(msg_content)

    builder.setPositiveButton(android.R.string.ok, { dialogInterface, i ->
        dialogInterface.dismiss()
        if (event != null)
            MainClass.getRxBus()?.send(event)
    });

    val dialog = builder.create()
    dialog.show()
}

fun AppCompatActivity.clearBackStack() {
    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}

fun Activity.hideKeyboard() {

    if (this.currentFocus != null) {
        val view = this.currentFocus
        val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}