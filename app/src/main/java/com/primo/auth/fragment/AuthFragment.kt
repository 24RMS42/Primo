package com.primo.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewSwitcher
import com.primo.R
import com.primo.auth.mvp.AuthPresenter
import com.primo.auth.mvp.AuthPresenterImpl
import com.primo.auth.mvp.AuthView
import com.primo.main.MainActivity
import com.primo.main.MainClass
import com.primo.network.new_models.Auth
import com.primo.utils.base.BasePresenterFragment
import com.primo.utils.clearBackStack
import com.primo.utils.other.RxEvent
import com.primo.utils.setOnClickListener
import com.primo.utils.showSnack
import com.primo.utils.views.PrefixedEditText

class AuthFragment : BasePresenterFragment<AuthView, AuthPresenter>(), View.OnClickListener, AuthView {

    private var switcher: ViewSwitcher? = null

    private var email: PrefixedEditText? = null
    private var password: PrefixedEditText? = null
    private var forgot: View? = null
    private var confirmedText: View? = null
    private var confirmedLink: View? = null
    private var cancelBtn: View? = null
    private var signInBtn: View? = null

    private var restoreEmail: PrefixedEditText? = null
    private var cancelRestoreBtn: View? = null
    private var restoreBtn: View? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (rootView == null) {

            rootView = inflater?.inflate(R.layout.auth_fragment, container, false)

            initSwipe()
            init()
            initPresenter()
        }

        return rootView
    }

    private fun init() {

        switcher = rootView?.findViewById(R.id.switcher) as ViewSwitcher

        email = rootView?.findViewById(R.id.email) as PrefixedEditText
        password = rootView?.findViewById(R.id.password) as PrefixedEditText
        forgot = rootView?.findViewById(R.id.forgot)
        confirmedText = rootView?.findViewById(R.id.confirmed_text)
        confirmedLink = rootView?.findViewById(R.id.confirmed_link)
        cancelBtn = rootView?.findViewById(R.id.cancel_btn)
        signInBtn = rootView?.findViewById(R.id.sign_in_btn)

        restoreEmail = rootView?.findViewById(R.id.restore_email) as PrefixedEditText
        cancelRestoreBtn = rootView?.findViewById(R.id.cancel_restore_btn)
        restoreBtn = rootView?.findViewById(R.id.restore_btn)

        setOnClickListener(this, forgot, confirmedText, confirmedLink,
                cancelBtn, signInBtn, cancelRestoreBtn, restoreBtn)
    }

    override fun initPresenter() {

        presenter = AuthPresenterImpl(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.forgot -> {
                switcher?.setInAnimation(context, R.anim.right_center)
                switcher?.setOutAnimation(context, R.anim.center_left)
                switcher?.showNext()
            }

            R.id.cancel_btn -> activity?.onBackPressed()

            R.id.sign_in_btn -> {
                val email = email?.text?.toString()?.trim()
                val password = password?.text?.toString()?.trim()

                presenter?.signIn(email ?: "",
                         password ?: "")

                if (email != null && password != null)
                    MainClass.saveLoginData(email, password)
            }

            R.id.cancel_restore_btn -> {
                switcher?.setInAnimation(context, R.anim.left_center)
                switcher?.setOutAnimation(context, R.anim.center_right)
                switcher?.showPrevious()
            }

            R.id.restore_btn -> presenter?.restore(restoreEmail?.text?.toString()?.trim() ?: "")

            R.id.confirmed_text,
            R.id.confirmed_link -> {
                presenter?.resendConfirm(email?.text?.toString()?.trim() ?: "")
                confirmedText?.visibility = View.GONE
                confirmedLink?.visibility = View.GONE
            }
        }
    }

    override fun onSigned(auth: Auth) {

        MainClass.deleteLoginData()

        MainClass.saveAuth(auth)

        val activity = activity
        if (activity != null && activity is MainActivity)
            activity.clearBackStack()
    }

    override fun onEmailSent() {
        showDialog(getString(R.string.check_email))
    }

    override fun onNotConfirmed() {
        confirmedText?.visibility = View.VISIBLE
        confirmedLink?.visibility = View.VISIBLE
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
}