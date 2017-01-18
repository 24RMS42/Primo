package com.primo.utils.other

import com.primo.network.models.Country
import com.primo.network.models.State
import com.primo.utils.consts.*
import com.primo.utils.getInt
import com.primo.utils.getString
import org.json.JSONObject


object AssetsParser {

    fun parseCountry(str: String) : Country? {

        val country : Country

        val obj = JSONObject(str)

        val name = obj.getString(NAME, "")
        val code = obj.getString(CODE, "")
        val value = obj.getInt(VALUE, 0)
        val continent = obj.getString(CONTINENT, "")
        val fileName = obj.getString(FILENAME, "")

        country = Country(name, value, code, continent, fileName)

        return country
    }

    fun parseState(str: String, key: String) : State? {

        val state : State?

        val obj = JSONObject(str)

        val name = obj.getString(NAME, "")
        val code = obj.getString(CODE, "")

        state = State(key, name, code)

        return state
    }
}