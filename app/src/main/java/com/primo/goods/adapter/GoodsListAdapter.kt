/**
 * Changes:
 *
 * - Remove (-) button for 0 stock item
 * - Show 'no stock' note
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
import com.primo.goods.view.GoodsFooterView
import com.primo.network.models.ShippingQuote
import com.primo.network.new_models.CartItem
import com.primo.utils.base.BaseItem
import com.primo.utils.consts.ADD_TO_WISHLIST
import com.primo.utils.getCurrency
import com.primo.utils.interfaces.OnItemClickListener
import com.primo.utils.round
import com.primo.utils.setOnClickListener
import com.primo.utils.toStringWithoutZeros
import com.primo.utils.views.SquareImageView


class GoodsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    constructor(hideMinus: Boolean = false) : super() {
        this.hideMinus = hideMinus
    }

    private val DEFAULT_VIEW_TYPE = Integer.MIN_VALUE
    private val FOOTER_VIEW_TYPE = Integer.MIN_VALUE + 1

    var list: MutableList<BaseItem>  = mutableListOf()
    private var hideMinus: Boolean = false

    var shippings: MutableList<ShippingQuote>? = null

    var goodsFooterViewListener: GoodsFooterView.FooterViewListener? = null
        set(value) {
            field = value
        }

    var itemClickListener: OnItemClickListener? = null
        set(value) {
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {

        val layout = if (viewType == DEFAULT_VIEW_TYPE)
            R.layout.product_list_item
        else
            R.layout.product_list_footer

        val view = LayoutInflater.from(parent?.context).inflate(layout, parent, false)
        return if (viewType == DEFAULT_VIEW_TYPE) ProductHolder(view) else FooterHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

        if (holder is ProductHolder) {

            val item = list[position]

            with(item) {
                Log.d("Test", "list adapter item" + item)
                holder.image?.setImageURI(Uri.parse(getBaseImage()))
                holder.title?.text = getBaseName()
                holder.description?.text = Html.fromHtml(getBaseDescription()).toString()
                holder.price?.text = "${getCurrency(getBaseCurrency())} ${(getBasePrice()
                        /** quantity*/
                        ).round(2).toStringWithoutZeros() }"

                //if status is 4, invisible - button
                if (getBaseStatus() == ADD_TO_WISHLIST){
                    holder.plusBtn?.visibility = View.GONE
                    holder.noStockText?.visibility = View.VISIBLE
                    holder.quantity?.visibility = View.INVISIBLE
                } else{
                    holder.plusBtn?.visibility = View.VISIBLE
                    holder.noStockText?.visibility = View.GONE
                    holder.quantity?.visibility = View.VISIBLE
                }
                //end

                if (!hideMinus) {
                    holder.quantity?.text = getBaseQuantity().toString()
                } else {
                    holder.minusBtn?.visibility = View.GONE
                    holder.quantity?.text = ""
                }

                if (!getStockSize().isEmpty()) {
                    holder.firstStockParam?.text = getStockSize()
                    holder.firstStockParam?.visibility = View.VISIBLE
                } else {
                    holder.firstStockParam?.visibility = View.GONE
                }

                if (!getStockColor().isEmpty()) {
                    holder.secondStockParam?.text = getStockColor()
                    holder.secondStockParam?.visibility = View.VISIBLE
                } else {
                    holder.secondStockParam?.visibility = View.GONE
                }

            }
        } else if (holder is FooterHolder) {
            holder.goodsFooterView?.footerViewListener = goodsFooterViewListener
            holder.goodsFooterView?.recalculate(list as MutableList<CartItem>)
            holder.goodsFooterView?.shippings = shippings
            holder.goodsFooterView?.updateShipping()
        }
    }

    override fun getItemViewType(position: Int): Int {

        if (hideMinus) {
            return DEFAULT_VIEW_TYPE
        } else if (position >= list.size) {
            return FOOTER_VIEW_TYPE
        } else {
            return DEFAULT_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int {
        return list.size + if (hideMinus) 0 else 1
    }

    inner class ProductHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var image: SimpleDraweeView? = null
        var title: TextView? = null
        var description: TextView? = null
        var price: TextView? = null

        var plusBtn: View? = null
        var minusBtn: SquareImageView? = null
        var quantity: TextView? = null
        var noStockText: TextView? = null

        var firstStockParam: TextView? = null
        var secondStockParam: TextView? = null

        init {
            image = itemView?.findViewById(R.id.image) as SimpleDraweeView
            title = itemView?.findViewById(R.id.title) as TextView
            description = itemView?.findViewById(R.id.description) as TextView
            price = itemView?.findViewById(R.id.price) as TextView

            plusBtn = itemView?.findViewById(R.id.plus_btn)
            minusBtn = itemView?.findViewById(R.id.minus_btn) as SquareImageView
            quantity = itemView?.findViewById(R.id.quantity) as TextView
            noStockText = itemView?.findViewById(R.id.txtNoStock) as TextView

            firstStockParam = itemView?.findViewById(R.id.first_stock_param) as TextView
            secondStockParam = itemView?.findViewById(R.id.second_stock_param) as TextView

            setOnClickListener(this, plusBtn, minusBtn, itemView)
        }

        override fun onClick(v: View?) {

            if (adapterPosition == -1)
                return
            else
                itemClickListener?.onItemClick(v, adapterPosition)
        }
    }

    class FooterHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var goodsFooterView: GoodsFooterView? = null

        init {
            goodsFooterView = itemView as GoodsFooterView
        }
    }

}