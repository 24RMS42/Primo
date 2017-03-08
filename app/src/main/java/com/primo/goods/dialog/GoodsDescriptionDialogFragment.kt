/**
 * Changes:
 *
 * - Show merchant info
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.goods.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.primo.R
import com.primo.database.OrderDB
import com.primo.database.OrderDBImpl
import com.primo.goods.adapter.GoodsDescriptionAdapter
import com.primo.main.MainClass
import com.primo.network.models.Country
import com.primo.network.new_models.CartItem
import com.primo.network.new_models.Option
import com.primo.network.new_models.Stock
import com.primo.network.new_models.WishItem
import com.primo.utils.base.BaseItem
import com.primo.utils.consts.ADD
import com.primo.utils.consts.DELETE
import com.primo.utils.consts.UPDATE
import com.primo.utils.getCurrency
import com.primo.utils.interfaces.OnDialogClickListener
import com.primo.utils.setOnClickListener
import com.primo.utils.toStringWithoutZeros
import kotlinx.android.synthetic.main.goods_product_dialog.*
import java.util.*

class GoodsDescriptionDialogFragment : AppCompatDialogFragment() {

    private var cartItem: CartItem? = null

    private val colorList = arrayListOf<Option>()
    private val sizeList = arrayListOf<Option>()
    private val custom = arrayListOf<Option>()

    private val tempColorList = arrayListOf<Option>()
    private val tempSizeList = arrayListOf<Option>()

    private var stockList: ArrayList<Stock> = arrayListOf()

    private var colorAdapter: GoodsDescriptionAdapter? = null
    private var sizeAdapter: GoodsDescriptionAdapter? = null

    private var defaultSize = ""
    private var defaultColor = ""

    private var selectedSize = ""
    private var selectedColor = ""

    private var orderDB: OrderDB? = null

    companion object {

        private val CART_ITEM = "cart_item"
        private val WISH_ITEM = "wish_item"
        private val STOCK_LIST = "stock_list"

        fun newInstance(cartItem: CartItem, stocks: ArrayList<Stock>): GoodsDescriptionDialogFragment {
            val dialog = GoodsDescriptionDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(CART_ITEM, cartItem)
            bundle.putParcelableArrayList(STOCK_LIST, stocks)
            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0)
            dialog.arguments = bundle
            return dialog
        }

        fun newInstance(wishItem: WishItem): GoodsDescriptionDialogFragment {
            val dialog = GoodsDescriptionDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(WISH_ITEM, wishItem)
            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0)
            dialog.arguments = bundle
            return dialog
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.goods_product_dialog, null)
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderDB = OrderDBImpl()
        val countryList: MutableList<Country> = getCountries().orEmpty().toMutableList()

        cartItem = arguments.getParcelable<CartItem>(CART_ITEM)
        val wishItem = arguments.getParcelable<WishItem>(WISH_ITEM)
        stockList.addAll(arguments.getParcelableArrayList<Stock>(STOCK_LIST).orEmpty())

        stockList.forEach { item ->
            item.let {

                if (!item.color.name.isEmpty()) {
                    colorList.add(item.color)
                }

                if (!item.size.name.isEmpty()) {
                    sizeList.add(item.size)
                }
            }
        }

        val item: BaseItem = if (cartItem == null && wishItem != null) wishItem else cartItem as BaseItem

        if (!sizeList.isEmpty()/* && !colorList.isEmpty()*/) {

            sizeList.forEach { size ->
                if (!tempSizeList.contains(size)) {
                    tempSizeList.add(size)
                }
            }

            filterColor(sizeList[0].name)

        } else if (!colorList.isEmpty()) {

            colorList.forEach { color ->
                if (!tempColorList.contains(color)) {
                    tempColorList.add(color)
                }
            }

            firstSpinner.visibility = View.GONE
        } else {
            spinnerContainer.visibility = View.GONE
        }

        val currentSizeIndex = tempSizeList.indexOf(Option(item.getStockSize()))
        val currentColorIndex = tempColorList.indexOf(Option(item.getStockColor()))

        colorAdapter = GoodsDescriptionAdapter(context, R.id.color_name, tempColorList, GoodsDescriptionAdapter.COLOR_TYPE)
        sizeAdapter = GoodsDescriptionAdapter(context, R.id.name, tempSizeList, GoodsDescriptionAdapter.SIZE_TYPE)

        firstSpinner.adapter = sizeAdapter
        secondSpinner.adapter = colorAdapter

        firstSpinner.setSelection(if (currentSizeIndex >= 0) currentSizeIndex else 0)
        secondSpinner.setSelection(if (currentColorIndex >= 0) currentColorIndex else 0)

        firstSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val size = tempSizeList[p3.toInt()]
                filterColor(size.name)

                selectedSize = size.name

                if (defaultSize.isEmpty())
                    defaultSize = size.name
                else if (!size.name.equals(defaultSize))
                    changeButtonState()
            }
        }

        secondSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val color = tempColorList[p3.toInt()]

                selectedColor = color.name

                if (defaultColor.isEmpty())
                    defaultColor = color.name
                else if (!color.name.equals(defaultColor))
                    changeButtonState()
            }
        }

        productTitle.text = (item.getBaseName())
        productDescription.text = item.getBaseDescription()

        productPrice.text = "${getCurrency(item.getBaseCurrency())} ${item.getBasePrice().toStringWithoutZeros()}"

        productImage.setImageURI(Uri.parse(item.getBaseImage()))

        //Show merchant info
        val countryIndex = countryList.indexOf(Country(value = Integer.parseInt(item.getMerchantCountry())))
        if (countryIndex > -1) {

            val countryModel = countryList[countryIndex]

            merchantName.text = item.getMerchantName()
            merchantCountry.text = countryModel.name
            merchantUrl.text = item.getMerchantUrl()
            merchantUrl.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.getMerchantUrl()))
                startActivity(browserIntent)
            }
        }

        if (item is WishItem) add.text = getString(R.string.add_to_cart)

        val target = targetFragment

        setOnClickListener(View.OnClickListener {
            v ->
            if (target is OnDialogClickListener) {

                if (v.id == R.id.delete && v.tag == null)
                    target.onDialogClick(DELETE, item)
                else if (v.id == R.id.delete1 && v.tag == null)
                    target.onDialogClick(DELETE, item)
                else if (v.id == R.id.delete)
                    target.onDialogClick(UPDATE, Pair(cartItem, getNewItem()))
                else if (v.id == R.id.delete1)
                    target.onDialogClick(UPDATE, Pair(cartItem, getNewItem()))
                else
                    target.onDialogClick(ADD, item)

                dismiss()
            }

        }, delete, add, delete1)

        //remove 'add to wishlist' button before login
        if (MainClass.getAuth().access_token.isEmpty()) {
            delete1.visibility = View.VISIBLE
            delete.visibility = View.INVISIBLE
            add.visibility = View.INVISIBLE
        }
    }

    fun getCountries(): List<Country>? {
        return orderDB?.getAllCountries()?.orEmpty()
    }

    private fun changeButtonState() {
        delete.text = getString(R.string.update)
        delete1.text = getString(R.string.update)
        delete.tag = ""
        delete1.tag = ""
    }

    private fun filterColor(sizeName: String) {

        tempColorList.clear()

        for (item in stockList) {

            if (item.size.name.equals(sizeName) && !item.color.name.isEmpty() && !tempColorList.contains(item.color))
                tempColorList.add(item.color)

        }

        if (tempColorList.isEmpty()) {
            secondSpinner.visibility = View.GONE
        } else {
            secondSpinner.visibility = View.VISIBLE
            colorAdapter?.notifyDataSetChanged()
        }
    }

    private fun getNewItem(): String {

        var stockId = ""

        if (!tempColorList.isEmpty()) {
            val colorIndex = tempColorList.indexOf(Option(selectedColor))
            if (colorIndex > -1) {
                stockId = tempColorList[colorIndex].stockId
            }
        } else if (!tempSizeList.isEmpty()) {
            val sizeIndex = tempSizeList.indexOf(Option(selectedSize))
            if (sizeIndex > -1) {
                stockId = tempSizeList[sizeIndex].stockId
            }
        }

        return stockId
    }

    override fun onResume() {
        super.onResume()
        val height = resources.getDimensionPixelSize(R.dimen.dialog_product_height);
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);
    }
}