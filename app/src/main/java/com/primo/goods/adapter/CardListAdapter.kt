package com.primo.goods.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.primo.R
import com.primo.network.new_models.CreditCardData
import com.primo.utils.consts.*
import com.primo.utils.interfaces.OnItemClickListener
import com.primo.utils.setOnClickListener
import com.primo.utils.views.SquareImageView

class CardListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    constructor() : super() {

    }

    var list: MutableList<CreditCardData>  = mutableListOf()

    var itemClickListener: OnItemClickListener? = null
        set(value) {
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {

        val layout = R.layout.card_list_item

        val view = LayoutInflater.from(parent?.context).inflate(layout, parent, false)
        return CardHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

        if (holder is CardHolder){

            val item = list[position]
            holder.numberTxt?.text = (position + 1).toString()
            holder.nameTxt?.text = item.lastFour

            if (item.is_default == 1)
                holder.defaultImage?.setBackgroundResource(R.drawable.icon_checked)
            else
                holder.defaultImage?.setBackgroundResource(R.drawable.icon_unchecked)

            when (item.cardtype){

                CARD_UNKNOWN -> holder.cardType?.setBackgroundResource(R.drawable.card_general)
                CARD_VISA -> holder.cardType?.setBackgroundResource(R.drawable.card_visa)
                CARD_MASTER -> holder.cardType?.setBackgroundResource(R.drawable.card_master)
                CARD_JCB -> holder.cardType?.setBackgroundResource(R.drawable.card_jcb)
                CARD_AMEX -> holder.cardType?.setBackgroundResource(R.drawable.card_amex)
                CARD_UNIPAY -> holder.cardType?.setBackgroundResource(R.drawable.card_general)
                CARD_DINERS -> holder.cardType?.setBackgroundResource(R.drawable.card_general)
                CARD_DISCOVER -> holder.cardType?.setBackgroundResource(R.drawable.card_general)
            }
        }
    }

    override fun getItemCount(): Int {

        return list.size
    }

    inner class CardHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var nameTxt: TextView? = null
        var numberTxt: TextView? = null
        var deleteBtn: SquareImageView? = null
        var defaultImage: ImageView? = null
        var cardType: ImageView? = null

        init {
            nameTxt = itemView?.findViewById(R.id.name_txt) as TextView
            numberTxt = itemView?.findViewById(R.id.number_txt) as TextView
            deleteBtn = itemView?.findViewById(R.id.delete_btn) as SquareImageView
            defaultImage = itemView?.findViewById(R.id.default_image) as ImageView
            cardType = itemView?.findViewById(R.id.card_type) as ImageView

            setOnClickListener(this, itemView, deleteBtn, defaultImage)
        }

        override fun onClick(v: View?) {

            if (adapterPosition == -1)
                return
            else {
                itemClickListener?.onItemClick(v, adapterPosition)
            }
        }
    }
}