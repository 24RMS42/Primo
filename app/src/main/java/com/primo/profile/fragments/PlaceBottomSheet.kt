package com.primo.profile.fragments

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        isCountry = arguments.getBoolean(IS_COUNTRIES, false)
        val countryCode = arguments.getString(COUNTRY_CODE)

        val rootView = inflater?.inflate(R.layout.place_bottom_sheet, null)
        val list = rootView?.findViewById(R.id.list) as? RecyclerView

        dialog.setTitle(if (isCountry) getString(R.string.country) else getString(R.string.state_province))

        val realm = io.realm.Realm.getDefaultInstance()
        if (isCountry) {
            data.addAll(realm.copyFromRealm(realm.allObjects(Country::class.java)))
        } else if (countryCode != null){
            data.addAll(realm.copyFromRealm(realm.allObjects(State::class.java).where().equalTo("key", countryCode).findAll()))
        }

        Crashlytics.logException(Throwable("isCountry = $isCountry"))
        Crashlytics.logException(Throwable("data size (dialog) = ${data.size}"))

        (rootView?.findViewById(R.id.title) as? TextView)?.text = if (isCountry) getString(R.string.country) else getText(R.string.state_province)

        rootView?.findViewById(R.id.empty)?.visibility = if (data.size > 0) View.GONE else View.VISIBLE

        list?.layoutManager = LinearLayoutManager(context)
        val adapter = PlaceListAdapter(data)
        adapter.itemClickListener = this
        list?.adapter = adapter
        list?.setHasFixedSize(true)

        return rootView
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