/**
 * Created:
 *
 * - custom dialog class
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.utils

import android.content.Context
import com.kaopiz.kprogresshud.KProgressHUD

class DialogUtils {

    var mkProgressHUD: KProgressHUD? = null

    fun showLoadingWithoutLabel(context: Context) {

        mkProgressHUD = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show()
    }

    fun hideLoading() {
        mkProgressHUD?.dismiss()
    }
}