package com.primo.profile.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.primo.R
import com.primo.network.models.Country
import com.primo.network.models.State
import com.primo.utils.interfaces.OnItemClickListener


class PlaceListAdapter : RecyclerView.Adapter<PlaceListAdapter.BottomItemHolder> {

    constructor(list : List<Any>) : super() {
        this.list = list
    }

    private lateinit var list : List<Any>

    var itemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlaceListAdapter.BottomItemHolder? {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.bottom_list_item, parent, false)
        return BottomItemHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceListAdapter.BottomItemHolder?, position: Int) {

        val item = list[position]
        var name = ""

        if (item is Country) {
            name = item.name
        } else if (item is State) {
            name = item.name
        }

        (holder?.itemView as? TextView)?.text = name
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class BottomItemHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView?.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (adapterPosition == -1)
                return
            else
                itemClickListener?.onItemClick(v, adapterPosition)
        }

    }

}