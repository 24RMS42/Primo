package com.primo.network.models

import io.realm.RealmObject

open class Country(open var name: String = "",
                   open var value: Int = -1,
                   open var code: String = "",
                   open var continent: String = "",
                   open var fileName: String = "") : RealmObject() {

    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {

        if (other is Country && other.value.equals(value))
            return true

        return false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}