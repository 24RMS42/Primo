/**
 * Created:
 *
 * - Reject List Adapter
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.goods.adapter

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.primo.R
import com.primo.main.MainClass
import com.primo.utils.base.BaseItem
import com.primo.utils.consts.*


class RejectListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    constructor(hideMinus: Boolean = false) : super() {

    }

    private val DEFAULT_VIEW_TYPE = Integer.MIN_VALUE

    var list: MutableList<BaseItem>  = mutableListOf()
    private var hideMinus: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {

        val layout = R.layout.reject_list_item

        val view = LayoutInflater.from(parent?.context).inflate(layout, parent, false)
        return RejectHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

        if (holder is RejectHolder) {

            val item = list[position]

            with(item) {
                Log.d("Test", "list adapter item" + item)
                holder.image?.setImageURI(Uri.parse(getBaseImage()))
                holder.productNameTxt?.text = Html.fromHtml(getBaseName()).toString()
                val errorCode = getBaseStatus()

                when (errorCode) {

                    //Payment Error
                    STRIPE_NOT_FOUND              ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_not_found)
                    STRIPE_APPROVE_WITH_ID        ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_approve_with_id)
                    STRIPE_CARD_NOT_SUPPORTED     ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_card_not_supported)
                    STRIPE_CARD_VELOCITY_EXCEEDED ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_card_velocity_exceeded)
                    STRIPE_CURRENCY_NOT_SUPPORT   ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_currency_not_supported)
                    STRIPE_FRAUDULENT             ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_fraudulent)
                    STRIPE_INSUFFICIENT_FUNDS     ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_insufficient_funds)
                    STRIPE_INVALID_ACCOUNT        ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_invalid_account)
                    STRIPE_INVALID_AMOUNT         ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_invalid_amount)
                    STRIPE_INVALID_PIN            ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_invalid_pin)
                    STRIPE_ISSUER_NOT_AVAILABLE   ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_issuer_not_available)
                    STRIPE_LOST_CARD              ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_lost_card)
                    STRIPE_NEW_ACCOUNT_INFO_AVAILABLE      ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_new_account_info_available)
                    STRIPE_NOT_PERMITTED                   ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_not_permitted)
                    STRIPE_REENTER_TRANSACTION             ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_reenter_transaction)
                    STRIPE_TESTMODE_DECLINE                ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_testmode_decline)
                    STRIPE_TRY_AGAIN                       ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_try_again_later)
                    STRIPE_WITHDRAWAL_COUNT_LIMIT_EXCEEDED ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_withdrawal_count_limit_exceeded)
                    STRIPE_INVALID_NUMBER                  ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_invalid_number)
                    STRIPE_INVALID_EXPIRY_MONTH            ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_invalid_expiry_month)
                    STRIPE_INVALID_EXPIRY_YEAR             ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_invalid_expiry_year)
                    STRIPE_INVALID_CVC                     ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_invalid_cvc)
                    STRIPE_INVALID_SWIPE_DATA              ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_invalid_swipe_data)
                    STRIPE_INCORRECT_NUMBER                ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_incorrect_number)
                    STRIPE_EXPIRED_CARD                    ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_expired_card)
                    STRIPE_INCORRECT_CVC                   ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_incorrect_cvc)
                    STRIPE_INCORRECT_ZIP                   ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_incorrect_zip)
                    STRIPE_MISSING                         ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_missing)
                    STRIPE_PROCESSING_ERROR                ->  holder.errorTxt?.text = MainClass.context.getString(R.string.stripe_processing_error)

                    STRIPE_CALL_ISSUER, STRIPE_DO_NOT_HONOR, STRIPE_DO_NOT_TRY_AGAIN, STRIPE_GENERIC_DECLINE, STRIPE_NO_ACTION_TOKEN, STRIPE_PICKUP_CARD,
                    STRIPE_RESTRICTED_CARD, STRIPE_REVOCATION_ALL_AUTHORIZATIONS, STRIPE_REVOCATION_AUTHORICATIONS, STRIPE_SECURITY_VIOLATION,
                    STRIPE_SERVICE_NOT_ALLOWED, STRIPE_STOLEN_CARD, STRIPE_STOP_PAYMENT_ORDER, STRIPE_TRANSACTION_NOT_ALLOWED, STRIPE_CARD_DECLINED->
                        holder.errorTxt?.text = MainClass.context.getString(R.string.your_card_declined_contact_bank)

                    //Product Error
                    PRODUCT_NOT_FOUND           -> holder.errorTxt?.text = MainClass.context.getString(R.string.product_not_found)
                    SEARCH_PRODUCT_QR_ERROR     -> holder.errorTxt?.text = MainClass.context.getString(R.string.search_product_qrcode)
                    SEARCH_PRODUCT_KEYWORD      -> holder.errorTxt?.text = MainClass.context.getString(R.string.search_product_keyword)
                    PRODUCT_STOCK_NOT_FOUND     -> holder.errorTxt?.text = MainClass.context.getString(R.string.product_out_of_stock)
                    PRODUCT_OUTOF_STOCK         -> holder.errorTxt?.text = MainClass.context.getString(R.string.product_out_of_stock)
                    PRODUCT_NOT_APPROVED        -> holder.errorTxt?.text = MainClass.context.getString(R.string.product_not_approved)
                    PRODUCT_NOT_ACTIVE          -> holder.errorTxt?.text = MainClass.context.getString(R.string.product_not_active)
                    SIGNUP_NOTIFICATION         -> holder.errorTxt?.text = MainClass.context.getString(R.string.product_out_of_stock)
                    INTERNATIONAL_SHIPPING_NOT  -> holder.errorTxt?.text = MainClass.context.getString(R.string.international_shipping_not_available)
                    FAIL_REDUCE_STOCK           -> holder.errorTxt?.text = MainClass.context.getString(R.string.product_out_of_stock)
                    LESSER_MIN_QUANTITY         -> holder.errorTxt?.text = MainClass.context.getString(R.string.lesser_min_order_quantity)
                    GREATER_MAX_QUANTITY        -> holder.errorTxt?.text = MainClass.context.getString(R.string.greater_max_order_quantity)
                    STOCK_REMOVED               -> holder.errorTxt?.text = MainClass.context.getString(R.string.product_out_of_stock)
                    STOCK_UPDATED               -> holder.errorTxt?.text = MainClass.context.getString(R.string.stock_updated_by_merchant)
                    OUTOF_STOCK_ADD_TO_WISHLIST -> holder.errorTxt?.text = MainClass.context.getString(R.string.product_is_out_of_stock_add_to_wishlist)

                    //Merchant Error
                    MERCHANT_NOT_FOUND,
                    MERCHANT_NOT_SELL,
                    MERCHANT_NOT_ACTIVE,
                    MERCHANT_STRIPE_ACCOUNT_ERROR ->
                        holder.errorTxt?.text = MainClass.context.getString(R.string.product_not_available)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        return DEFAULT_VIEW_TYPE
    }

    override fun getItemCount(): Int {

        return list.size
    }

    class RejectHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var errorTxt: TextView? = null
        var productNameTxt: TextView? = null
        var image: SimpleDraweeView? = null

        init {
            errorTxt = itemView?.findViewById(R.id.errorTxt) as TextView
            productNameTxt = itemView?.findViewById(R.id.productNameTxt) as TextView
            image = itemView?.findViewById(R.id.imageView) as SimpleDraweeView
        }
    }

}