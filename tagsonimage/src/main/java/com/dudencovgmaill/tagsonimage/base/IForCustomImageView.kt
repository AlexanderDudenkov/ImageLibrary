package com.dudencovgmaill.tagsonimage.base

import android.graphics.Bitmap
import androidx.lifecycle.LifecycleOwner

interface IForCustomImageView {
    fun setLifecycle(owner: LifecycleOwner)
    fun setPicture(pic: Bitmap?)
}