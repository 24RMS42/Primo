package com.primo.network.new_models

import android.os.Parcel
import android.os.Parcelable


data class Option(var name: String = "", var description: String = "", var stockId: String = "") : Parcelable {
    constructor(source: Parcel): this(source.readString(), source.readString(), source.readString())

    override fun equals(other: Any?): Boolean {

        if (other != null && other is Option && other.name.equals(name)) {
            return true
        }

        return false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(name)
        dest?.writeString(description)
        dest?.writeString(stockId)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<Option> = object : Parcelable.Creator<Option> {
            override fun createFromParcel(source: Parcel): Option {
                return Option(source)
            }

            override fun newArray(size: Int): Array<Option?> {
                return arrayOfNulls(size)
            }
        }
    }
}