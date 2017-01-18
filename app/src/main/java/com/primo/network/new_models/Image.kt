package com.primo.network.new_models

import io.realm.RealmObject


open class Image(open var is_default: Int = 0,
                 open var caption: String = "",
                 open var display_order: Int = 0,
                 open var image_url: String = "",
                 open var image_thumbnail_url: String = ""): RealmObject() {
}
