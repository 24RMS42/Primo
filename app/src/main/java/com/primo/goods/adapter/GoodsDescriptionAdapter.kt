package com.primo.goods.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.primo.R
import com.primo.network.new_models.Option

import java.util.ArrayList


class GoodsDescriptionAdapter(private val mContext: Context, resource: Int,
                              private val items: ArrayList<Option>, private val type: Int) : ArrayAdapter<String>(mContext, resource) {

    override fun getCount(): Int {
        return items.size
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return getCustomView(position, convertView, parent)
    }

    fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val item = items[position]

        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var rootView: View? = null

        if (type == COLOR_TYPE) {

            rootView = inflater.inflate(R.layout.goods_color_item, parent, false)

            val colorName = rootView!!.findViewById(R.id.color_name) as TextView
            val colorCode = rootView.findViewById(R.id.color_code)

            colorName.text = item.name
            colorCode.setBackgroundColor(Color.parseColor(item.description))
        } else if (type == SIZE_TYPE) {

            rootView = inflater.inflate(R.layout.goods_custom_item, parent, false)

            val name = rootView!!.findViewById(R.id.name) as TextView
            val description = rootView.findViewById(R.id.description) as TextView

            //name.setText(item.component2());
            description.text = item.name
        }

        return rootView
    }

    companion object {

        val COLOR_TYPE = 0
        val SIZE_TYPE = 1
        val CUSTOM_TYPE = 2
    }
}
