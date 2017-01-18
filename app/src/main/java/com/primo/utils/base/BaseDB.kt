package com.primo.utils.base

import io.realm.Realm
import kotlin.properties.Delegates


abstract class  BaseDB {

    protected var realm: Realm by Delegates.notNull()

    init {
        realm = io.realm.Realm.getDefaultInstance()
    }

    fun onDestroy() {
        realm.close()
    }

}