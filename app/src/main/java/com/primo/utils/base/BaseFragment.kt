package com.primo.utils.base

import android.app.Activity
import android.content.res.Resources
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.primo.R
import com.primo.main.MainActivity
import com.primo.utils.hideKeyboard
import com.primo.utils.other.RxEvent
import com.primo.utils.showAlert
import com.primo.utils.showDialogFragment
import com.primo.utils.showFragment

abstract class BaseFragment : Fragment() {

    protected var rootView: View? = null
    protected var swipe: SwipeRefreshLayout? = null

    protected val DEFAULT_CHILD_ANIMATION_DURATION = 250L

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {

        val parent = parentFragment;

        if (!enter && parent != null && parent.isRemoving) {
            // This is a workaround for the bug where child fragments disappear when
            // the parent is removed (as all children are first removed from the parent)
            // See https://code.google.com/p/android/issues/detail?id=55228
            val doNothingAnim = AlphaAnimation(1f, 1f)
            doNothingAnim.duration = getNextAnimationDuration(parent, DEFAULT_CHILD_ANIMATION_DURATION)
            return doNothingAnim;
        } else {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }

    }

    private fun getNextAnimationDuration(fragment: Fragment, defValue: Long): Long {

        try {
            // Attempt to get the resource ID of the next animation that
            // will be applied to the given fragment.
            val nextAnimField = Fragment::class.java.getDeclaredField("mNextAnim")
            nextAnimField.isAccessible = true
            val nextAnimResource = nextAnimField.getInt(fragment)
            val nextAnim = AnimationUtils.loadAnimation(fragment.activity, nextAnimResource)

            // ...and if it can be loaded, return that animation's duration
            return if (nextAnim == null) defValue else nextAnim.getDuration()
        } catch (ex: NoSuchFieldException) {
            return defValue
        } catch (ex: IllegalAccessException) {
            return defValue
        } catch (ex: Resources.NotFoundException) {
            return defValue
        }
    }

    //SWIPE DISABLED BY DEFAULT
    protected fun initSwipe() {

        swipe = rootView?.findViewById(R.id.swipe) as SwipeRefreshLayout
        swipe?.setProgressBackgroundColorSchemeResource(android.R.color.white)
        swipe?.setColorSchemeResources(R.color.color_red)
        swipe?.isEnabled = false
    }

    protected fun changeToolbarState(state: Int) {

        val activity = activity
        if (activity is MainActivity)
            activity.changeToolbarState(state)
    }

    protected fun showFragment(fragment: Fragment, isAddToBackStack: Boolean = true,
                               enter: Int = 0, exit: Int = 0,
                               popEnter: Int = 0, popExit: Int = 0, tag: String = "") {

        val activity = activity
        if (activity is MainActivity)
            activity.showFragment(fragment, isAddToBackStack,
                    enter, exit, popEnter, popExit, tag)
    }

    protected fun showDialogFragment(dialog: DialogFragment, targetFragment: Fragment? = null) {

        val activity = activity
        if (activity is MainActivity)
            activity.showDialogFragment(dialog, targetFragment)
    }

    protected fun showDialog(message: String?, event: RxEvent? = null) {

        val activity: Activity? = activity
        if (activity is MainActivity)
            activity.showAlert(message = message, code = -2, event = event)
    }

    protected fun showErrorDialog(message: String?, code: Int?, event: RxEvent? = null) {

        Log.d("Test", "base fragment error dialgo" + code)
        val activity: Activity? = activity
        if (activity is MainActivity)
            activity.showAlert(message = message, code = code, event = event)
    }

    override fun onPause() {
        super.onPause()
        activity.hideKeyboard()
    }

}