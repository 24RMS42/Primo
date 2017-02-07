package com.primo.utils.other

import com.primo.network.models.Country
import com.primo.network.models.State
import com.primo.utils.consts.*
import com.primo.utils.getInt
import com.primo.utils.getString
import org.json.JSONObject


object AssetsParser {

    fun parseCountry(str: String, str_ja: String, str_ch: String, str_cht: String) : Country? {

        val country : Country

        val obj = JSONObject(str)
        val obj_ja = JSONObject(str_ja)
        val obj_ch = JSONObject(str_ch)
        val obj_cht = JSONObject(str_cht)

        val name = obj.getString(NAME, "")
        val code = obj.getString(CODE, "")
        val value = obj.getInt(VALUE, 0)
        val continent = obj.getString(CONTINENT, "")
        val fileName = obj.getString(FILENAME, "")

        val name_ja = obj_ja.getString(NAME, "")
        val name_ch = obj_ch.getString(NAME, "")
        val name_cht = obj_cht.getString(NAME, "")

        country = Country(name, value, code, continent, fileName, name_ja, name_ch, name_cht)

        return country
    }

    fun parseState(str: String, key: String) : State? {

        val state : State?

        val obj = JSONObject(str)

        val name = obj.getString(NAME, "")
        val code = obj.getString(CODE, "")
        val name_ja = obj.getString(NAME_JA, "")

        state = State(key, name, code, name_ja)

        return state
    }
}