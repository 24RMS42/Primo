/**
 * Add:
 * - List shipping address Fragment
 * - Integrated shipping address api (get, add, update, update default)
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
import android.widget.TextView
import com.primo.R
import com.primo.goods.adapter.AddressListAdapter
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
import kotlinx.android.synthetic.main.page_address_fragment.*
import rx.subscriptions.CompositeSubscription
import java.util.*

class AddAddressFragment : BasePresenterFragment<OrderView, OrderPresenter>(), OrderView, View.OnClickListener, OnItemClickListener {

    private var addressList: RecyclerView? = null
    private var addressAdapter: AddressListAdapter? = null
    private var plusButton: ImageButton? = null
    private var shippingAddressList: MutableList<UserProfile>? = null
    private var defaultText: TextView?= null

    private var _subscriptions: CompositeSubscription? = null
    private var _rxBus: RxBus? = null

    override fun onStart() {
        super.onStart()

        _subscriptions = CompositeSubscription();
        _subscriptions?.add(_rxBus?.toObserverable()
                ?.subscribe({
                    Log.d("Test", "AddAddress fragment get event:" + it.key)
                    when (it.key) {

//                        Events.TAB_CARD -> {
//                            onNextButtonClick()
//                        }
                    }
                }));
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (rootView == null) {

            rootView = inflater?.inflate(R.layout.address_list_fragment, container, false)

            initSwipe()
            init()
            initPresenter()

        }

        (activity as MainActivity).showToolbar(true)
        (activity as MainActivity).setProfilePageState(MainActivity.ProfileTabStates.ADDRESS_PAGE)

        return rootView
    }

    private fun init() {

        addressList = rootView?.findViewById(R.id.recycler_view) as RecyclerView
        plusButton = rootView?.findViewById(R.id.plusBtn) as ImageButton
        defaultText = rootView?.findViewById(R.id.default_text) as TextView

        val layoutManager = LinearLayoutManager(context)
        addressList?.layoutManager = layoutManager
        addressAdapter = AddressListAdapter()
        addressAdapter?.itemClickListener = this
        addressList?.adapter = addressAdapter
        shippingAddressList = addressAdapter?.list as MutableList<UserProfile>

        setOnClickListener(this, plusButton)

        _rxBus = MainClass.getRxBus()
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.plusBtn -> {

                val bundle = Bundle()
                bundle.putString("kind", SHIPPING_ADDRESS_ADD)
                val fragObj = PageAddressFragment()
                fragObj.arguments = bundle

                showFragment(fragObj, true,
                        R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageAddressFragment::class.java.simpleName)
            }
        }
    }

    override fun onItemClick(view: View?, position: Int) {

        val item = shippingAddressList?.get(position)

        when (view?.id) {

            R.id.shipping_address -> {

                val bundle = Bundle()
                bundle.putString("kind", SHIPPING_ADDRESS_UPDATE)
                bundle.putString(SHIPPING_ID, item?.shipping_id)
                bundle.putInt(IS_DEFAULT, item!!.is_default)
                bundle.putInt(SHIPPING_ADDRESS_COUNT, shippingAddressList!!.size)

                val fragObj = PageAddressFragment()
                fragObj.arguments = bundle

                (activity as MainActivity).user?.phone               = item!!.phone
                (activity as MainActivity).user?.firstname           = item!!.firstname
                (activity as MainActivity).user?.lastname            = item!!.lastname
                (activity as MainActivity).user?.postcode            = item!!.postcode
                (activity as MainActivity).user?.state               = item!!.state
                (activity as MainActivity).user?.country             = item!!.country
                (activity as MainActivity).user?.city                = item!!.city
                (activity as MainActivity).user?.address             = item!!.address
                (activity as MainActivity).user?.delivery_preference = item!!.delivery_preference

                showFragment(fragObj, true,
                        R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, PageAddressFragment::class.java.simpleName)
            }

            R.id.delete_btn -> {

                if (item?.is_default == 1)
                    showMessage(MainClass.context.getString(R.string.cannot_delete_default_address))
                else
                    confirmDialogShow(1, item)
            }

            R.id.default_image -> {

                if (item?.is_default == 0)
                    confirmDialogShow(2, item)
            }
        }
    }

    fun confirmDialogShow(dialogID: Int, item: UserProfile?) {

        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setTitle(MainClass.context.getString(R.string.infromation))
        alertDialogBuilder.setCancelable(false)

        if (dialogID == 1)
            alertDialogBuilder.setMessage(MainClass.context.getString(R.string.are_you_sure_delete_shipping))
        else if (dialogID == 2)
            alertDialogBuilder.setMessage(MainClass.context.getString(R.string.are_you_sure_change_default_shipping))

        alertDialogBuilder.setPositiveButton("Yes") { dialog, id ->

            if (dialogID == 1)
                presenter?.deleteShippingAddress(item!!.shipping_id)

            else if (dialogID == 2)
                presenter?.updateDefaultShippingAddress(item!!.shipping_id)

        }

        alertDialogBuilder.setNegativeButton("No") { dialog, id ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun onNextButtonClick(){

        showFragment(AddCardFragment(), true,
                R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right, AddCardFragment::class.java.simpleName)
        (activity as MainActivity).changeProfileTabState(MainActivity.ProfileTabStates.CARD_PAGE)
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

        addressAdapter?.list?.clear()
        addressAdapter?.list?.addAll(listShippingAddress)
        addressAdapter?.notifyDataSetChanged()

        if (listShippingAddress.size == 0)
            defaultText?.visibility = View.INVISIBLE
        else
            defaultText?.visibility = View.VISIBLE
    }

    override fun updateShippingAddress(){

    }

    override fun updateListCreditCard(listCreditCard: ArrayList<CreditCardData>) {

    }

    override fun updateUserData(userData: UserProfile?) {

    }

    override fun updateCardData(card: CreditCard?) {

    }

    override fun onSigned() {

    }

    override fun onSignUped() {

    }

    override fun onCountrySelected() {

    }

    override fun onStateSelected() {

    }

    override fun onResume() {
        super.onResume()

        presenter?.retrieveListShippingAddress()

        if (MainClass.getAuth().access_token.isEmpty())
            changeToolbarState(MainActivity.ToolbarStates.DEFAULT)
        else
            changeToolbarState(MainActivity.ToolbarStates.DEFAULT)
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