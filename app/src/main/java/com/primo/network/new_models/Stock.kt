package com.primo.network.new_models

import android.os.Parcel
import android.os.Parcelable


data class Stock(var variant_type: Int = 0,
                 var quantity: Int = 0,
                 var stock_id: String = "",
                 var size: Option = Option(),
                 var color: Option = Option(),
                 var custom: Option = Option()) : Parcelable {

    constructor(source: Parcel) : this(source.readInt(), source.readInt(), source.readString(), source.readParcelable<Option>(Option::class.java.classLoader), source.readParcelable<Option>(Option::class.java.classLoader), source.readParcelable<Option>(Option::class.java.classLoader))

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(variant_type)
        dest?.writeInt(quantity)
        dest?.writeString(stock_id)
        dest?.writeParcelable(size, 0)
        dest?.writeParcelable(color, 0)
        dest?.writeParcelable(custom, 0)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<Stock> = object : Parcelable.Creator<Stock> {
            override fun createFromParcel(source: Parcel): Stock {
                return Stock(source)
            }

            override fun newArray(size: Int): Array<Stock?> {
                return arrayOfNulls(size)
            }
        }
    }
}