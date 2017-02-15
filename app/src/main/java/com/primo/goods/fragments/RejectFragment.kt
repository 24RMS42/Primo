/**
 * Created:
 *
 * - Reject Fragment
 *
 * 2015 © Primo . All rights reserved.
 */


package com.primo.goods.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.primo.R
import com.primo.goods.adapter.RejectListAdapter
import com.primo.goods.mvp.GoodsTotalPresenter
import com.primo.goods.mvp.GoodsTotalView
import com.primo.main.MainActivity
import com.primo.main.MainClass
import com.primo.network.new_models.RejectItem
import com.primo.network.parsers.PrimoParsers
import com.primo.utils.base.BasePresenterFragment
import com.primo.utils.other.Events
import com.primo.utils.other.RxEvent
import com.primo.utils.setOnClickListener
import java.util.*

class RejectFragment : BasePresenterFragment<GoodsTotalView, GoodsTotalPresenter>(),  View.OnClickListener {

    private var rejectList: RecyclerView? = null
    private var rejectAdapter: RejectListAdapter? = null
    private var okButton: Button? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (rootView == null) {

            rootView = inflater?.inflate(R.layout.reject_fragment, container, false)

            init()

            changeToolbarState(MainActivity.ToolbarStates.OK_BTN)
            (activity as MainActivity).showToolbar(true)
        }

        return rootView
    }

    private fun init() {

        val body_data = arguments.getString("body_data")
        val kind = arguments.getString("kind")

        rejectList = rootView?.findViewById(R.id.reject_recycler) as RecyclerView
        okButton = rootView?.findViewById(R.id.okBtn) as Button

        val layoutManager = LinearLayoutManager(context)
        rejectList?.layoutManager = layoutManager
        rejectAdapter = RejectListAdapter(true)
        rejectList?.adapter = rejectAdapter

        var rejects = ArrayList<RejectItem>()
        if (kind == "order_reject"){
            rejects = PrimoParsers.orderRejectParser(body_data)
        }
        else if (kind == "reject"){
            rejects = PrimoParsers.rejectParser(body_data)
        }

        rejectAdapter?.list?.addAll(rejects)
        rejectAdapter?.notifyDataSetChanged()

        setOnClickListener(this, okButton)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.okBtn -> {
                val activity = activity
                if (activity is MainActivity)
                    activity.showThankLayout(true)

                MainClass.getRxBus()?.send(RxEvent(Events.UPDATE_PRODUCTS))// not working
                activity?.let {
                    rootView?.postDelayed({ activity?.onBackPressed() }, 100)
                }
            }
        }
    }

    override fun initPresenter() {

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDetach() {
        super.onDetach()
    }
}