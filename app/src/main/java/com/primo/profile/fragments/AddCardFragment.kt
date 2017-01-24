/**
 * Add:
 * - List credit card Fragment
 * - Integrated credit card api (get, add, update, update default)
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.profile.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.primo.R
import com.primo.goods.adapter.CardListAdapter
import com.primo.main.MainActivity
import com.primo.main.MainClass
import com.primo.network.new_models.Address
import com.primo.network.new_models.CreditCard
import com.primo.network.new_models.CreditCardData
import com.primo.network.new_models.UserProfile
import com.primo.profile.mvp.OrderPresenter
import com.primo.profile.mvp.OrderView
import com.primo.profile.mvp.ProfilePresenterImpl
import com.primo.utils.base.BasePresenterFragment
import com.primo.utils.consts.*
import com.primo.utils.interfaces.OnItemClickListener
import com.primo.utils.other.Events
import com.primo.utils.other.RxBus
import com.primo.utils.other.RxEvent
import com.primo.utils.setOnClickListener
import com.primo.utils.views.GestureRelativeLayout
import kotlinx.android.synthetic.main.page_address_fragment.*
import rx.subscriptions.CompositeSubscription
import java.util.*

class AddCardFragment : BasePresenterFragment<OrderView, OrderPresenter>(), OrderView, View.OnClickListener, OnItemClickListener, GestureRelativeLayout.OnSwipeListener {

    private var cardList: RecyclerView? = null
    private var cardAdapter: CardListAdapter? = null
    private var plusButton: ImageButton? = null
    private var creditCardList: MutableList<CreditCardData>? = null

    private var _subscriptions: CompositeSubscription? = null
    private var _rxBus: RxBus? = null

    override fun onStart() {
        super.onStart()

        _subscriptions = CompositeSubscription();
        _subscriptions?.add(_rxBus?.toObserverable()
                ?.subscribe({
                    Log.d("Test", "AddCard fragment get event:" + it.key)
                    when (it.key) {

                        //Fixed inactive address submenu clicking
//                        Events.TAB_ADDRESS_FROM_CARD -> {
//                            onPreviousButtonClick()
//                        }
                    }
                }));
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (rootView == null) {

            rootView = inflater?.inflate(R.layout.card_list_fragment, container, false)

            initSwipe()
            init()
            initPresenter()

        }

        (activity as MainActivity).setProfilePageState(MainActivity.ProfileTabStates.CARD_PAGE)

        return rootView
    }

    private fun init() {

        cardList = rootView?.findViewById(R.id.recycler_view) as RecyclerView
        plusButton = rootView?.findViewById(R.id.plusBtn) as ImageButton

        val layoutManager = LinearLayoutManager(context)
        cardList?.layoutManager = layoutManager
        cardAdapter = CardListAdapter()
        cardAdapter?.itemClickListener = this
        cardList?.adapter = cardAdapter
        creditCardList = cardAdapter?.list as MutableList<CreditCardData>

        setOnClickListener(this, plusButton)

        _rxBus = MainClass.getRxBus()
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.plusBtn -> {

                val bundle = Bundle()
                bundle.putString("kind", CREDIT_CARD_ADD)
                val fragObj = PageCardFragment()
                fragObj.arguments = bundle

                showFragment(fragObj, true,
                        R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageCardFragment::class.java.simpleName)
            }
        }
    }

    override fun onItemClick(view: View?, position: Int) {

        val item = creditCardList?.get(position)

        when (view?.id) {

            R.id.credit_card -> {

                val bundle = Bundle()
                bundle.putString("kind", CREDIT_CARD_UPDATE)
                bundle.putString(CREDITCARD_ID, item?.cardId)
                bundle.putInt(IS_DEFAULT, item!!.is_default)

                val fragObj = PageCardFragment()
                fragObj.arguments = bundle

                (activity as MainActivity).user?.cardyear        = item!!.cardyear.toString()
                (activity as MainActivity).user?.cardmonth       = item!!.cardmonth.toString()
                (activity as MainActivity).user?.lastFour        = item!!.lastFour
                (activity as MainActivity).user?.cardname        = item!!.cardname

                showFragment(fragObj, true,
                        R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageCardFragment::class.java.simpleName)
            }

            R.id.delete_btn -> {

                if (item?.is_default == 1)
                    showMessage(MainClass.context.getString(R.string.cannot_delete_default_card))
                else
                    confirmDialogShow(1, item)
            }

            R.id.default_image -> {

                if (item?.is_default == 0)
                    confirmDialogShow(2, item)
            }
        }
    }

    fun confirmDialogShow(dialogID: Int, item: CreditCardData?) {

        Log.d("Test", "card id:" + item!!.cardId)
        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setTitle(MainClass.context.getString(R.string.infromation))
        alertDialogBuilder.setCancelable(false)

        if (dialogID == 1)
            alertDialogBuilder.setMessage(MainClass.context.getString(R.string.are_you_sure_delete_card))
        else if (dialogID == 2)
            alertDialogBuilder.setMessage(MainClass.context.getString(R.string.are_you_sure_change_default_card))

        alertDialogBuilder.setPositiveButton("Yes") { dialog, id ->

            if (dialogID == 1)
                presenter?.deleteCreditCard(item!!.cardId)

            else if (dialogID == 2)
                presenter?.updateDefaultCreditCard(item!!.cardId)
        }

        alertDialogBuilder.setNegativeButton("No") { dialog, id ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun onPreviousButtonClick(){

        (activity as MainActivity).changeProfileTabState(MainActivity.ProfileTabStates.ADDRESS_PAGE)

        showFragment(AddAddressFragment(), true,
                R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, AddAddressFragment::class.java.simpleName)
    }

    override fun backToPreviousScreen() {

    }

    override fun initPresenter() {
        presenter = ProfilePresenterImpl(this)
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

    override fun getAddressData(addressData: Address?){

    }

    override fun updateListShippingAddress(listShippingAddress: ArrayList<UserProfile>){

    }

    override fun updateShippingAddress(){

    }

    override fun updateListCreditCard(listCreditCard: ArrayList<CreditCardData>) {

        cardAdapter?.list?.clear()
        cardAdapter?.list?.addAll(listCreditCard)
        cardAdapter?.notifyDataSetChanged()
    }

    override fun updateUserData(userData: UserProfile?) {

    }

    override fun updateCardData(card: CreditCard?) {

    }

    override fun onSigned() {

    }

    override fun onCountrySelected() {

    }

    override fun onStateSelected() {

    }

    override fun onResume() {
        super.onResume()

        presenter?.retrieveListCardData()

        if (MainClass.getAuth().access_token.isEmpty())
            changeToolbarState(MainActivity.ToolbarStates.BACK_BTN_WITH_LOGIN)
        else
            changeToolbarState(MainActivity.ToolbarStates.BACK_BTN_WITH_LOGOUT)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDetach() {
        super.onDetach()
    }
}