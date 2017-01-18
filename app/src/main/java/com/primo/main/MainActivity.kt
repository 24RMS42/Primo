package com.primo.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.primo.R
import com.primo.auth.fragment.AuthFragment
import com.primo.goods.fragments.GoodsPagerFragment
import com.primo.network.new_models.Auth
import com.primo.utils.clearBackStack
import com.primo.utils.consts.PERMISSION_CAMERA_REQUEST
import com.primo.utils.consts.PERMISSION_LOCATION_REQUEST
import com.primo.utils.other.Events
import com.primo.utils.other.RxEvent
import com.primo.utils.setOnClickListener
import com.primo.utils.showFragment
import com.primo.utils.slideAnimation

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {

    object ToolbarStates {
        val DEFAULT = 0
        val BACK_BTN_ONLY = 1
        val BACK_BTN_WITH_LOGIN = 2
        val BACK_BTN_WITH_LOGOUT = 3
        val BACK_BTN_ROTATED_ONLY = 4
    }

    private var backBtn: ImageView? = null
    private var toolbarLogin: View? = null
    private var toolbarLogout: View? = null
    private var toolbarShadow: View? = null
    private var thankMessage: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        backBtn = findViewById(R.id.back_btn) as ImageView
        toolbarLogin = findViewById(R.id.toolbar_login)
        toolbarLogout = findViewById(R.id.toolbar_logout)
        toolbarShadow = findViewById(R.id.toolbar_shadow)
        thankMessage = findViewById(R.id.thank_message_txt) as TextView

        setOnClickListener(this, backBtn, toolbarLogin, toolbarLogout)

        showFragment(fragment = GoodsPagerFragment(), isAddToBackStack = false)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val handler = Handler()
        handler.postDelayed(
                {
                    this.clearBackStack()
                    Log.d("Test", "onNewIntent")
                }, 100)

        handler.postDelayed(
                {
                    MainClass.getRxBus()?.send(RxEvent(Events.CONFIRMED))
                }, 150)
    }

    fun changeToolbarState(state: Int) {

        when (state) {

            ToolbarStates.DEFAULT -> {
                backBtn?.visibility = View.INVISIBLE
                toolbarLogin?.visibility = View.GONE
                toolbarLogout?.visibility = View.GONE
            }
            ToolbarStates.BACK_BTN_ONLY,
            ToolbarStates.BACK_BTN_ROTATED_ONLY -> {
                backBtn?.visibility = View.VISIBLE
                val res =
                        if (state == ToolbarStates.BACK_BTN_ONLY) R.drawable.ic_chevron_left_black
                        else R.drawable.ic_expand_less_black
                backBtn?.setImageResource(res)
            }
            ToolbarStates.BACK_BTN_WITH_LOGIN -> {
                backBtn?.visibility = View.VISIBLE
                toolbarLogin?.visibility = View.VISIBLE
                toolbarLogout?.visibility = View.GONE
                backBtn?.setImageResource(R.drawable.ic_chevron_left_black)
            }

            ToolbarStates.BACK_BTN_WITH_LOGOUT -> {
                backBtn?.visibility = View.VISIBLE
                toolbarLogin?.visibility = View.GONE
                toolbarLogout?.visibility = View.VISIBLE
                backBtn?.setImageResource(R.drawable.ic_chevron_left_black)
            }
        }
    }

    fun showThankLayout(isSuccess: Boolean) {

        if (isSuccess)
            thankMessage?.text = getString(R.string.thank_you)
        else
            thankMessage?.text = getString(R.string.problem_with_order)

        thankMessage?.slideAnimation()
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.back_btn -> onBackPressed()

            R.id.toolbar_login -> {
                showFragment(AuthFragment(), true, R.anim.right_center, R.anim.center_left, R.anim.left_center, R.anim.center_right)
            }

            R.id.toolbar_logout -> {
                MainClass.saveAuth(Auth())
                onBackPressed()
                MainClass.getRxBus()?.send(RxEvent(Events.UPDATE_PRODUCTS))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.d("TEST", requestCode.toString() + "<<")

        if (requestCode == PERMISSION_CAMERA_REQUEST && grantResults.size > 0)
            MainClass.getRxBus()?.send(RxEvent(Events.CAMERA_PERMISSION, grantResults[0]))
        else if (requestCode == PERMISSION_LOCATION_REQUEST && grantResults.size > 0)
            MainClass.getRxBus()?.send(RxEvent(Events.LOCATION_PERMISSION, grantResults[0]))
    }
}
