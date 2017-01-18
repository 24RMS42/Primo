package com.primo.network.models

import io.realm.RealmObject

open class State(open var key: String = "", open var name: String = "", open var code: String = "") : RealmObject() {

    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {

        if (other is State && other.name.equals(name))
            return true

        return false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
