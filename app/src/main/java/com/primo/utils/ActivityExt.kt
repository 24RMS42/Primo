package com.primo.utils

import android.app.Activity
import android.app.FragmentManager
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import com.primo.R
import com.primo.main.MainClass
import com.primo.utils.other.RxEvent

fun AppCompatActivity.showFragment(fragment: Fragment, isAddToBackStack: Boolean = true,
                 enter: Int = 0, exit: Int = 0,
                 popEnter: Int = 0, popExit: Int = 0, tag: String = "") {

    val transaction = supportFragmentManager.beginTransaction()
    transaction.setCustomAnimations(enter, exit, popEnter, popExit)
    transaction.replace(R.id.container, fragment, tag)

    if (isAddToBackStack)
        transaction.addToBackStack(tag)

    transaction.commitAllowingStateLoss()
}

fun AppCompatActivity.showDialogFragment(dialog: DialogFragment, targetFragment: Fragment? = null) {

    val fragmentTransaction = supportFragmentManager.beginTransaction()
    if (targetFragment != null) dialog.setTargetFragment(targetFragment, 0)
    fragmentTransaction.add(dialog, null)
    fragmentTransaction.commitAllowingStateLoss()
}

fun AppCompatActivity.showAlert(title: String = getString(R.string.infromation), message: String?, event: RxEvent? = null) {

    if (TextUtils.isEmpty(message))
        return

    val builder = AlertDialog.Builder(this, R.style.DialogTheme)
    builder.setTitle(title)
    builder.setMessage(message)

    builder.setPositiveButton(android.R.string.ok, { dialogInterface, i ->
        dialogInterface.dismiss()
        if (event != null)
            MainClass.getRxBus()?.send(event)
    });

    val dialog = builder.create()
    dialog.show()
}

fun AppCompatActivity.clearBackStack() {
    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}

fun Activity.hideKeyboard() {

    if (this.currentFocus != null) {
        val view = this.currentFocus
        val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}