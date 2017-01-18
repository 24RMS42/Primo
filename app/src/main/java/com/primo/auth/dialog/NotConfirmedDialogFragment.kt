package com.primo.auth.dialog

import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.primo.R
import kotlinx.android.synthetic.main.not_confirmed_dialog_fragment.*


class NotConfirmedDialogFragment: AppCompatDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.not_confirmed_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.setTitle(getString(R.string.sign_up_is_not_confirmed))

        cancel.setOnClickListener { dismiss() }

        confirmedLink.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}