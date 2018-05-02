package com.muei.apm.runtrack.utils

import android.content.Context
import android.provider.MediaStore
import android.content.Intent


class MediaUtils(val context: Context) {
    fun checkMediaCaptureApps(): Boolean {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        return takePictureIntent.resolveActivity(context.packageManager) != null
    }
}