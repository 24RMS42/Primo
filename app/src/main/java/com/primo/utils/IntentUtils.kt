package com.primo.utils

import android.content.Intent
import android.provider.MediaStore
import android.support.v4.app.Fragment

fun getPhotoFromGalleryIntent(fragment : Fragment, result : Int) {
    val gallery_intent_photo = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    fragment.startActivityForResult(gallery_intent_photo, result)
}
