package com.primo.utils

import android.app.Activity
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

fun JSONObject.getString(name: String, defValue: String) : String {
    return if (this.isNull(name)) defValue else this.getString(name)
}

fun JSONObject.getInt(name: String, defValue: Int) : Int {
    return if (this.isNull(name)) defValue else this.getInt(name)
}

fun JSONObject.getDouble(name: String, defValue: Double) : Double {
    return if (this.isNull(name)) defValue else this.getDouble(name)
}

fun JSONObject.getLong(name: String, defValue: Long) : Long {
    return if (this.isNull(name)) defValue else this.getLong(name)
}

fun JSONObject.getJSONObject(name: String, defValue: JSONObject) : JSONObject {
    return if (this.isNull(name)) defValue else this.getJSONObject(name)
}

fun JSONObject.getBoolean(name: String, defValue: Boolean) : Boolean {
    return if (this.isNull(name)) defValue else this.getBoolean(name)
}

fun JSONObject.getJSONArrayExt(name: String) : JSONArray {
    return if (this.isNull(name)) JSONArray() else getJSONArray(name)
}

fun loadJsonFromAssets(activity: Activity, fileName: String) : String {

    val json : String;

    try {
        val input = activity.assets.open(fileName)
        val size = input.available()
        val buffer = ByteArray(size)
        input.read(buffer)
        input.close()
        json = String(buffer)
    } catch (ex : IOException) {
        ex.printStackTrace();
        return "";
    }

    return json;
}