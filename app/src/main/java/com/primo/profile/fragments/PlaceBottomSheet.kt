package com.primo.profile.fragments

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.primo.R
import com.primo.network.models.Country
import com.primo.network.models.State
import com.primo.profile.adapter.PlaceListAdapter
import com.primo.utils.getDeviceLanguage
import com.primo.utils.interfaces.OnItemClickListener


class PlaceBottomSheet : BottomSheetDialogFragment(), OnItemClickListener{

    companion object {

        private val IS_COUNTRIES = "is_countries"
        private val COUNTRY_CODE = "country_code"

        fun newInstance(country: String?): PlaceBottomSheet {
            val dialog = PlaceBottomSheet()
            val bundle = Bundle()
            bundle.putBoolean(IS_COUNTRIES, country == null)
            bundle.putString(COUNTRY_CODE, country)
            dialog.arguments = bundle
            return dialog
        }
    }

    private var isCountry: Boolean = false
    private val data: MutableList<Any> = mutableListOf()
    private var language = "en"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        isCountry = arguments.getBoolean(IS_COUNTRIES, false)
        val countryCode = arguments.getString(COUNTRY_CODE)

        val rootView = inflater?.inflate(R.layout.place_bottom_sheet, null)
        val list = rootView?.findViewById(R.id.list) as? RecyclerView
        language = getDeviceLanguage()

        dialog.setTitle(if (isCountry) getString(R.string.country) else getString(R.string.state_province))

        val realm = io.realm.Realm.getDefaultInstance()
        if (isCountry) {
            val countryList: MutableList<Country> = realm.copyFromRealm(realm.allObjects(Country::class.java)).orEmpty().toMutableList()
            data.addAll(getNewCountryList(countryList))
        } else if (countryCode != null){
            val stateList: MutableList<State> = realm.copyFromRealm(realm.allObjects(State::class.java).where().equalTo("key", countryCode).findAll())
            data.addAll(getNewStateList(stateList))
        }

        //Crashlytics.logException(Throwable("isCountry = $isCountry"))
        //Crashlytics.logException(Throwable("data size (dialog) = ${data.size}"))

        (rootView?.findViewById(R.id.title) as? TextView)?.text = if (isCountry) getString(R.string.country) else getText(R.string.state_province)

        rootView?.findViewById(R.id.empty)?.visibility = if (data.size > 0) View.GONE else View.VISIBLE

        list?.layoutManager = LinearLayoutManager(context)
        val adapter = PlaceListAdapter(data)
        adapter.itemClickListener = this
        list?.adapter = adapter
        list?.setHasFixedSize(true)

        return rootView
    }

    fun getNewCountryList(countryList: List<Country>): MutableList<Country>{

        val newCountryList = mutableListOf<Country>()
        val size = countryList.size

        for (i in 0..size - 1) {

            val country: Country
            var name = ""

            if (language == "ja")
                name = countryList[i].name_ja
            else if (language == "en")
                name = countryList[i].name
            else if (language == "ch")
                name = countryList[i].name_ch
            else if (language == "cht")
                name = countryList[i].name_cht

            val code = countryList[i].code
            val value = countryList[i].value
            val continent = countryList[i].continent
            val fileName = countryList[i].fileName

            country = Country(name, value, code, continent, fileName, name)
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

            if (language == "ja")
                name = stateList[i].name_ja
            else if (language == "en")
                name = stateList[i].name

            val code = stateList[i].code
            val key = stateList[i].key

            state = State(key, name, code, name)
            newStateList.add(state)
        }

        return newStateList
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    override fun onItemClick(view: View?, position: Int) {

        val fragment = targetFragment

        if (data.size > position && fragment != null && fragment is ListDialogResult) {

            val item = data[position]
            if (item is Country)
                fragment.onCountrySelected(item)
            else if (item is State)
                fragment.onStateSelected(item)

            dismiss()
        }
    }

    interface ListDialogResult {

        fun onCountrySelected(country: Country)

        fun onStateSelected(state: State)
    }
}