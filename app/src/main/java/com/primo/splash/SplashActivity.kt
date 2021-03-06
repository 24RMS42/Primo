/**
 * Changes:
 *
 * - Parse Japanese country name
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.primo.R
import com.primo.main.MainActivity
import com.primo.network.models.Country
import com.primo.network.models.State
import com.primo.utils.consts.*
import com.primo.utils.loadJsonFromAssets
import com.primo.utils.other.AssetsParser
import io.realm.Realm
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.properties.Delegates

class SplashActivity : AppCompatActivity() {

    private var realm: Realm by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        realm = Realm.getDefaultInstance()

/*        if (!MainClass.getSharedPreferences().getBoolean(COUNTRIES_EXIST, false)) {

        }*/

        clearDB()
        parseCountries()

        val handler = Handler()
        handler.postDelayed({ startMainActivity() }, 3 * 1000)
    }

    private fun clearDB() {

        realm.beginTransaction()
        realm.clear(Country::class.java)
        realm.clear(State::class.java)
        realm.commitTransaction()
    }

    private fun parseCountries() {

        val obj = JSONObject(loadJsonFromAssets(this, COUNTRIES_EN))
        val obj_ja = JSONObject(loadJsonFromAssets(this, COUNTRIES_JA))
        val obj_ch = JSONObject(loadJsonFromAssets(this, COUNTRIES_CH))
        val obj_cht = JSONObject(loadJsonFromAssets(this, COUNTRIES_CHT))

        val array = obj.getJSONArray("others")
        val array_ja = obj_ja.getJSONArray("others")
        val array_ch = obj_ch.getJSONArray("others")
        val array_cht = obj_cht.getJSONArray("others")

        val size = array.length()

        val countries = mutableListOf<Country>()
        val states = mutableListOf<State>()

        for (i in 0..size - 1) {

            val country: Country? = AssetsParser.parseCountry(array.getString(i), array_ja.getString(i), array_ch.getString(i), array_cht.getString(i))

            if (country != null && !country.name.isEmpty() && !country.name_ja.isEmpty() && !country.name_ch.isEmpty() && !country.name_cht.isEmpty()) {
                countries.add(country)

                if (!country.fileName.isEmpty()) {
                    val stateArray = JSONArray(loadJsonFromAssets(this, "states/${country.fileName}.json"))
                    val stateSize = stateArray.length()

                    for (j in 0..stateSize - 1) {
                        val state = AssetsParser.parseState(stateArray.getString(j), country.fileName)

                        if (state != null && !state.name.isEmpty()) {
                            states.add(state)
                        }
                    }
                }
            }
        }

        //Crashlytics.logException(Throwable("countries size (parsing) = ${countries.size}"))
        //Crashlytics.logException(Throwable("states size (parsing) = ${states.size}"))

        if (countries.size > 0) {

            // sort by alphabetically //
            Collections.sort(countries, object : Comparator<Country> {
                override fun compare(country2: Country, country1: Country): Int {

                    return country2.name.compareTo(country1.name)
                }
            })

            realm.beginTransaction()
            realm.copyToRealm(countries)
            realm.copyToRealm(states)
            realm.commitTransaction()
            //MainClass.getSharedPreferences().edit().putBoolean(COUNTRIES_EXIST, true).apply()
        }
    }

    private fun startMainActivity() {

        val mainIntent = Intent(this, MainActivity::class.java);
        startActivity(mainIntent)
        this.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}