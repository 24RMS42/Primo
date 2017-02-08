/**
 * Changes:
 *
 * - Define getBaseStatus(), but not using
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.network.new_models

import android.os.Parcel
import android.os.Parcelable
import com.primo.utils.base.BaseItem


data class WishItem(var productId: String = "",
                    var wishlistId: String = "",
                    var quantity: Int = 1,
                    var productName: String = "",
                    var category: Int = 0,
                    var variantType: Int = 0,
                    var weightTypeUnit: Int = 0,
                    var weightAmount: Double = 0.0,
                    var zeroPriceAction: Int = 0,
                    var minimumOrderQty: Int = 0,
                    var maximumOrderQty: Int = 0,
                    var taxType: Int = 0,
                    var taxAmount: Double = 0.0,
                    var creationDate: String = "",
                    var availSinceDate: String = "",
                    var outOfStockAction: Int = 0,
                    var imageUrl: String = "",
                    var thumbnailUrl: String = "",
                    var stock: Stock = Stock(),
                    var price: Double = 0.0,
                    var currency: Int = -1,
                    var description: String = "",
                    var merchant_name: String = "",
                    var merchant_country: String = "",
                    var merchant_url: String = "") : BaseItem, Parcelable {

    constructor(source: Parcel): this(source.readString(), source.readString(), source.readInt(), source.readString(), source.readInt(), source.readInt(), source.readInt(), source.readDouble(), source.readInt(), source.readInt(), source.readInt(), source.readInt(), source.readDouble(), source.readString(), source.readString(), source.readInt(), source.readString(), source.readString(), source.readParcelable<Stock>(Stock::class.java.classLoader), source.readDouble(), source.readInt(), source.readString(), source.readString(), source.readString(), source.readString())

    override fun getBaseImage() = imageUrl

    override fun getBaseName() = productName

    override fun getBaseDescription() = description

    override fun getBasePrice() = price

    override fun getBaseCurrency() = currency

    override fun getBaseStatus() = -1

    override fun getBaseQuantity() = quantity

    override fun getStockColor(): String = stock.color.name

    override fun getStockSize(): String = stock.size.name

    override fun getStockCustom(): String = stock.custom.name

    override fun getMerchantName(): String = merchant_name

    override fun getMerchantCountry(): String = merchant_country

    override fun getMerchantUrl(): String = merchant_url

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(productId)
        dest?.writeString(wishlistId)
        dest?.writeInt(quantity)
        dest?.writeString(productName)
        dest?.writeInt(category)
        dest?.writeInt(variantType)
        dest?.writeInt(weightTypeUnit)
        dest?.writeDouble(weightAmount)
        dest?.writeInt(zeroPriceAction)
        dest?.writeInt(minimumOrderQty)
        dest?.writeInt(maximumOrderQty)
        dest?.writeInt(taxType)
        dest?.writeDouble(taxAmount)
        dest?.writeString(creationDate)
        dest?.writeString(availSinceDate)
        dest?.writeInt(outOfStockAction)
        dest?.writeString(imageUrl)
        dest?.writeString(thumbnailUrl)
        dest?.writeParcelable(stock, 0)
        dest?.writeDouble(price)
        dest?.writeInt(currency)
        dest?.writeString(description)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<WishItem> = object : Parcelable.Creator<WishItem> {
            override fun createFromParcel(source: Parcel): WishItem {
                return WishItem(source)
            }

            override fun newArray(size: Int): Array<WishItem?> {
                return arrayOfNulls(size)
            }
        }
    }
}
