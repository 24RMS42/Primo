package com.primo.utils.base

import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

import java.util.ArrayList

class BaseViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String = "") {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mFragmentTitleList[position]
    }

}