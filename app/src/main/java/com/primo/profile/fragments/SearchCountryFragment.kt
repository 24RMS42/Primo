package com.primo.profile.fragments

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.primo.R
import com.primo.main.MainClass
import com.primo.network.models.Country
import com.primo.network.models.State
import com.primo.utils.getDeviceLanguage
import com.primo.utils.hideKeyboard
import search.reel.android.RAMReel
import java.util.*

class SearchCountryFragment : BottomSheetDialogFragment(){

    companion object {

        private val IS_COUNTRIES = "is_countries"
        private val COUNTRY_CODE = "country_code"

        fun newInstance(country: String?): SearchCountryFragment {
            val dialog = SearchCountryFragment()
            val bundle = Bundle()
            bundle.putBoolean(IS_COUNTRIES, country == null)
            bundle.putString(COUNTRY_CODE, country)
            dialog.arguments = bundle
            return dialog
        }
    }

    private var language = "en"
    private var name_key = "name"
    private var isCountry: Boolean = false

    override fun onStart() {
        super.onStart()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater?.inflate(R.layout.search_country_fragment, null)

        val picker = rootView?.findViewById(R.id.picker) as RAMReel
        if (arguments != null)
            isCountry = arguments.getBoolean(IS_COUNTRIES, false)

        language = getDeviceLanguage()

        if (language == "en")
            name_key = "name"
        else if (language == "ja")
            name_key = "name_ja"
        else if (language == "ch")
            name_key = "name_ch"
        else if (language == "cht")
            name_key = "name_cht"

        val realm = io.realm.Realm.getDefaultInstance()
        val fragment = targetFragment
        if (isCountry) {
            val countryList: MutableList<Country> = realm.copyFromRealm(realm.allObjects(Country::class.java)).orEmpty().toMutableList()
            val newCountryList = getNewCountryList(countryList)
            val size = newCountryList.size
            val array = arrayOfNulls<String>(size)
            Arrays.fill(array, "")

            for (i in 0..size - 1) {
                array[i] = newCountryList[i].name
            }

            picker.setValues(array)
            picker.setHint(MainClass.context.getString(R.string.usa))
            picker.setOnItemClickListener { text ->

                val country = realm.allObjects(Country::class.java).where().equalTo(name_key, text).findFirst()

                if (country != null) {

                    //get localization
                    var name_main = ""
                    if (language == "en")
                        name_main = country.name
                    else if (language == "ja")
                        name_main = country.name_ja
                    else if (language == "ch")
                        name_main = country.name_ch
                    else if (language == "cht")
                        name_main = country.name_cht

                    val newCountry = Country(name_main, country.value, country.code, country.continent, country.fileName, country.name, country.name, country.name)

                    Log.d("Test", "selected country :" + newCountry + fragment)
                    if (fragment != null && fragment is ListDialogResult) {
                        activity.hideKeyboard()
                        fragment.countrySelected(newCountry)
                        dismiss()
                    }
                }
            }
        }else{
            val stateList: MutableList<State> = realm.copyFromRealm(realm.allObjects(State::class.java).where().equalTo("key", arguments.getString("country_code")).findAll())
            val newStateList = getNewStateList(stateList)
            val size = newStateList.size
            val array = arrayOfNulls<String>(size)
            Arrays.fill(array, "")

            for (i in 0..size - 1) {
                array[i] = newStateList[i].name
            }

            picker.setValues(array)
            picker.setHint(MainClass.context.getString(R.string.pa))
            picker.setOnItemClickListener { text ->

                if (language == "ja" && arguments.getString("country_code") == "japan")
                    name_key = "name_ja"
                else
                    name_key = "name"

                val state = realm.allObjects(State::class.java).where().equalTo(name_key, text).findFirst()

                //get localization
                var name_main = ""
                if (language == "ja" && state.key == "japan")
                    name_main = state.name_ja
                else
                    name_main = state.name

                val newState = State(state.key, name_main, state.code, state.name)

                Log.d("Test", "selected state :" + newState + fragment)
                if (fragment != null && fragment is ListDialogResult) {
                    activity.hideKeyboard()
                    fragment.stateSelected(newState)
                    dismiss()
                }
            }
        }

        return rootView
    }

    fun getNewCountryList(countryList: List<Country>): MutableList<Country>{

        val newCountryList = mutableListOf<Country>()
        val size = countryList.size

        for (i in 0..size - 1) {

            val country: Country
            var name = ""
            val name_en = countryList[i].name // it is used to sort as it is English name

            if (language == "ja") {
                name = countryList[i].name_ja
            }
            else if (language == "en") {
                name = countryList[i].name
            }
            else if (language == "ch") {
                name = countryList[i].name_ch
            }
            else if (language == "cht")
                name = countryList[i].name_cht

            val code = countryList[i].code
            val value = countryList[i].value
            val continent = countryList[i].continent
            val fileName = countryList[i].fileName

            country = Country(name, value, code, continent, fileName, name_en, name, name)
            newCountryList.add(country)
        }

        return newCountryList
    }

    fun getNewStateList(stateList: List<State>) : MutableList<State>{

        val newStateList = mutableListOf<State>()
        val size = stateList.size

        for (i in 0..size - 1) {

            val state: State
            var name = ""
            val name_en = stateList[i].name
            val key = stateList[i].key
            val code = stateList[i].code

            if (language == "ja" && key == "japan")
                name = stateList[i].name_ja
            else
                name = stateList[i].name

            state = State(key, name, code, name_en)
            newStateList.add(state)
        }

        // sort by alphabetically //
        Collections.sort(newStateList, object : Comparator<State> {
            override fun compare(state2: State, state1: State): Int {

                return state2.name_ja.compareTo(state1.name_ja) //name_ja is same as name_en
            }
        })

        return newStateList
    }

    interface ListDialogResult {

        fun countrySelected(country: Country)
        fun stateSelected(state: State)
    }
}