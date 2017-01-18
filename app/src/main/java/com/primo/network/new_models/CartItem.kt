package com.primo.network.new_models

import android.os.Parcel
import android.os.Parcelable
import com.primo.utils.base.BaseItem

data class CartItem(var productId: String = "",
                    var cartItemId: String = "",
                    var quantity: Int = 1,
                    var productName: String = "",
                    var imageUrl: String = "",
                    var thumbnailUrl: String = "",
                    var stock: Stock = Stock(),
                    var price: Double = 0.0,
                    var currency: Int = -1,
                    var description: String = "",
                    var shippingDomesticOption: Int = 0,
                    var shippingDomesticAmount: Double = 0.0,
                    var shippingInternationalOption: Int = 0,
                    var shippingInternationalAmount: Double = 0.0) : BaseItem, Parcelable {

    constructor(source: Parcel): this(source.readString(), source.readString(), source.readInt(), source.readString(), source.readString(), source.readString(), source.readParcelable<Stock>(Stock::class.java.classLoader), source.readDouble(), source.readInt(), source.readString(), source.readInt(), source.readDouble(), source.readInt(), source.readDouble())

    override fun getBaseImage() = imageUrl

    override fun getBaseName() = productName

    override fun getBaseDescription() = description

    override fun getBasePrice() = price

    override fun getBaseCurrency() = currency

    override fun getBaseQuantity() = quantity

    override fun getStockColor(): String = stock.color.name

    override fun getStockSize(): String = stock.size.name

    override fun getStockCustom(): String = stock.custom.name

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {

        if (other != null && other is CartItem)
            return other.cartItemId == cartItemId

        return false
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(productId)
        dest?.writeString(cartItemId)
        dest?.writeInt(quantity)
        dest?.writeString(productName)
        dest?.writeString(imageUrl)
        dest?.writeString(thumbnailUrl)
        dest?.writeParcelable(stock, 0)
        dest?.writeDouble(price)
        dest?.writeInt(currency)
        dest?.writeString(description)
        dest?.writeInt(shippingDomesticOption)
        dest?.writeDouble(shippingDomesticAmount)
        dest?.writeInt(shippingInternationalOption)
        dest?.writeDouble(shippingInternationalAmount)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<CartItem> = object : Parcelable.Creator<CartItem> {
            override fun createFromParcel(source: Parcel): CartItem {
                return CartItem(source)
            }

            override fun newArray(size: Int): Array<CartItem?> {
                return arrayOfNulls(size)
            }
        }
    }
}