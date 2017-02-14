/**
 * Changes:
 *
 * - Add login in wishlist while logout
 * - Control toolbar
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.goods.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.primo.R
import com.primo.goods.adapter.GoodsListAdapter
import com.primo.goods.decoration.VerticalSpaceItemDecoration
import com.primo.goods.dialog.GoodsDescriptionDialogFragment
import com.primo.goods.mvp.GoodsWishlistPresenter
import com.primo.goods.mvp.GoodsWishlistPresenterImpl
import com.primo.goods.mvp.GoodsWishlistView
import com.primo.main.MainActivity
import com.primo.main.MainClass
import com.primo.network.new_models.WishItem
import com.primo.utils.base.BasePresenterFragment
import com.primo.utils.consts.ADD
import com.primo.utils.consts.DELETE
import com.primo.utils.interfaces.OnDialogClickListener
import com.primo.utils.interfaces.OnItemClickListener
import com.primo.utils.other.RxEvent
import com.primo.utils.showFragment
import com.primo.utils.views.GestureRelativeLayout
import kotlinx.android.synthetic.main.goods_list_fragment.*

class GoodsWishlistFragment : BasePresenterFragment<GoodsWishlistView, GoodsWishlistPresenter>(),
        GoodsWishlistView, OnItemClickListener, OnDialogClickListener, GestureRelativeLayout.OnSwipeListener {

    private var wishlist: MutableList<WishItem>? = null
    private var wishAdapter: GoodsListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.goods_list_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).changeTabbarState(MainActivity.TabbarStates.WISHLIST)
        val auth = MainClass.getAuth()
        val token = auth.access_token
        Log.d("Test", "token:" + token)
        if (token.isEmpty())
            changeToolbarState(MainActivity.ToolbarStates.BACK_BTN_WITH_LOGIN)
        else
            changeToolbarState(MainActivity.ToolbarStates.BACK_BTN_ONLY)
    }

    override fun onPause() {
        super.onPause()
        changeToolbarState(MainActivity.ToolbarStates.DEFAULT)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootView = view

        initRecycler()
        initPresenter()

        listTitle.text = getString(R.string.wishlist)
        gestureLayout.onSwipeListener = this

        presenter?.getWishes()

        (activity as MainActivity).showToolbar(true)
        Log.d("Test", "=======Wishlist Fragment create")
    }

    private fun initRecycler() {

        initSwipe()
        swipe?.isEnabled = true
        swipe?.setOnRefreshListener { refresh() }

        val layoutManager = LinearLayoutManager(context)
        recycler?.layoutManager = layoutManager
        wishAdapter = GoodsListAdapter(true)
        wishAdapter?.itemClickListener = this
        recycler?.adapter = wishAdapter

        wishlist = wishAdapter?.list as MutableList<WishItem>

        recycler?.addItemDecoration(VerticalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.decoration_space), true))
    }

    override fun initPresenter() {
        presenter = GoodsWishlistPresenterImpl(this)
    }

    fun refresh() {
        presenter?.getWishes()
    }

    override fun onItemClick(view: View?, position: Int) {

        val wish = wishlist?.get(position)
        wish?.let {
            when (view?.id) {

                R.id.product -> showDialogFragment(GoodsDescriptionDialogFragment.newInstance(wish), this)

                R.id.plus_btn -> presenter?.addItemToCart(wish)

                else -> {}
            }
        }
    }

    override fun onSwipeToRight() {
        //activity?.onBackPressed()
        (activity as MainActivity).changeTabbarState(MainActivity.TabbarStates.CART)
        (activity as MainActivity).setPageState(1); // Set Total fragment
        showFragment(GoodsPagerFragment(), true,
                R.anim.left_center, R.anim.center_right, R.anim.right_center, R.anim.center_left, GoodsPagerFragment::class.java.simpleName)
    }

    override fun onDialogClick(code: Int, dataObject: Any?) {

        if (dataObject != null && dataObject is WishItem) {
            when (code) {

                DELETE -> {
                    deleteItem(dataObject)
                }

                ADD -> presenter?.addItemToCart(dataObject)
            }
        }
    }

    override fun updateCartBadge() {

        (activity as MainActivity).increaseBadge()
    }

    override fun showWishes(wishes: MutableList<WishItem>) {
        wishAdapter?.list?.clear()
        wishAdapter?.list?.addAll(wishes)
        wishAdapter?.notifyDataSetChanged()
    }

    override fun deleteItem(wishItem: WishItem) {
        presenter?.removeWishItem(wishItem)
        val index = wishlist?.indexOf(wishItem) ?: -1
        if (index >= 0) {
            wishlist?.removeAt(index)
            wishAdapter?.notifyItemRemoved(index)
        }
    }

    override fun showProgress() {
        swipe?.post { swipe?.isRefreshing = true }
    }

    override fun hideProgress() {
        swipe?.post { swipe?.isRefreshing = false }
    }

    override fun showMessage(message: String?, event: RxEvent?) {
        //throw UnsupportedOperationException()
        showDialog(message = message, event = event)
    }

    override fun displayErrorMessage(message : String?, code: Int?, event: RxEvent?){
        showErrorDialog(message, code)
    }

}

