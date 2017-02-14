package com.primo.goods.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.primo.R
import com.primo.goods.adapter.GoodsListAdapter
import com.primo.goods.decoration.VerticalSpaceItemDecoration
import com.primo.goods.dialog.GoodsDescriptionDialogFragment
import com.primo.goods.mvp.GoodsHistoryPresenter
import com.primo.goods.mvp.GoodsHistoryPresenterImpl
import com.primo.goods.mvp.GoodsHistoryView
import com.primo.main.MainActivity
import com.primo.main.MainClass
import com.primo.network.new_models.CartItem
import com.primo.network.new_models.Product
import com.primo.utils.base.BasePresenterFragment
import com.primo.utils.interfaces.OnItemClickListener
import com.primo.utils.other.EndlessRecyclerViewScrollListener
import com.primo.utils.other.Events
import com.primo.utils.other.RxEvent
import java.util.*

class GoodsHistoryFragment : BasePresenterFragment<GoodsHistoryView, GoodsHistoryPresenter>(),
        GoodsHistoryView, OnItemClickListener {

    private var productList: MutableList<CartItem>? = null
    private var historyList: RecyclerView? = null
    private var historyAdapter: GoodsListAdapter? = null

    private var newProducts: MutableList<CartItem>? = null

    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    private var currentPage = 1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (rootView == null) {
            rootView = inflater?.inflate(R.layout.goods_list_fragment, container, false)

            init()
            initPresenter()

            presenter?.getOrderHistory(1)
        }

        //show toolbar
        (activity as MainActivity).showToolbar(true)

        return rootView
    }

    private fun init() {

        initSwipe()
        swipe?.isEnabled = true
        swipe?.setOnRefreshListener { refresh() }

        productList = mutableListOf()
        newProducts = mutableListOf()

        historyList = rootView?.findViewById(R.id.recycler) as RecyclerView
        val layoutManager = LinearLayoutManager(context)
        historyList?.layoutManager = layoutManager
        historyAdapter = GoodsListAdapter(true)
        historyAdapter?.itemClickListener = this
        historyList?.adapter = historyAdapter

        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                println("page = $page, totalItemsCount = $totalItemsCount")
                currentPage = page + 1
                presenter?.getOrderHistory(currentPage)
            }
        }

        historyList?.addItemDecoration(VerticalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.decoration_space), true))
        historyList?.addOnScrollListener(scrollListener)
    }

    override fun initPresenter() {

        presenter = GoodsHistoryPresenterImpl(this)
    }

    private fun refresh() {
        scrollListener?.reset()
        historyAdapter?.list?.clear()
        currentPage = 1
        presenter?.getOrderHistory(currentPage)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).changeTabbarState(MainActivity.TabbarStates.HISTORY)
        changeToolbarState(MainActivity.ToolbarStates.BACK_BTN_ROTATED_ONLY)
    }

    override fun onPause() {
        super.onPause()
        changeToolbarState(MainActivity.ToolbarStates.DEFAULT)
    }

    override fun onItemClick(view: View?, position: Int) {

        val item = historyAdapter?.list?.get(position) as? CartItem
        item?.let {
            when (view?.id) {

                R.id.plus_btn -> presenter?.addItemToCart(item)

                else -> {}
            }
        }
    }

    override fun showHistoryResult(products: ArrayList<CartItem>?) {

        if (products != null && products.size > 0) {
            historyAdapter?.list?.addAll(products)
            historyAdapter?.notifyDataSetChanged()
        }
    }

    override fun showProgress() {
        swipe?.post({ swipe?.isRefreshing = true })
    }

    override fun hideProgress() {
        swipe?.post({ swipe?.isRefreshing = false })
    }

    override fun showMessage(message: String?, event: RxEvent?) {
        showDialog(message)
    }

    override fun displayErrorMessage(message : String?, code: Int?, event: RxEvent?){
        showErrorDialog(message, code)
    }

    override fun onDetach() {
        super.onDetach()
        if (newProducts != null && (newProducts?.size ?: 0) > 0)
            MainClass.getRxBus()?.send(RxEvent(Events.ADD_PRODUCTS, newProducts))
    }
}