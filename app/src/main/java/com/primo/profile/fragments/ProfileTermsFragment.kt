package com.primo.profile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joanzapata.pdfview.PDFView
import com.joanzapata.pdfview.exception.FileNotFoundException
import com.primo.R
import com.primo.main.MainActivity
import com.primo.utils.base.BaseFragment
import com.primo.utils.consts.TERMS_DEF
import com.primo.utils.consts.TERMS_JAP
import java.util.*

class ProfileTermsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (rootView == null) {
            rootView = inflater?.inflate(R.layout.order_terms_fragment, container, false)

            val pdf_container = rootView?.findViewById(R.id.pdf_container) as PDFView

            try {

                var filename = TERMS_DEF

                if (Locale.getDefault().language == Locale.JAPANESE.language)
                    filename = TERMS_JAP

                pdf_container.fromAsset(filename).defaultPage(1).showMinimap(false).swipeVertical(true).enableSwipe(true).load()
            } catch (ex: FileNotFoundException) {
                ex.printStackTrace()
            }

        }

        return rootView
    }

    override fun onResume() {
        super.onResume()
        changeToolbarState(MainActivity.ToolbarStates.BACK_BTN_ROTATED_ONLY)
    }

    override fun onPause() {
        super.onPause()
        changeToolbarState(MainActivity.ToolbarStates.DEFAULT)
    }

}