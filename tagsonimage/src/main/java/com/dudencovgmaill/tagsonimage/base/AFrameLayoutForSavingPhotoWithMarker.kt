package com.dudencovgmaill.tagsonimage.base

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.widget.FrameLayout

abstract class AFrameLayoutForSavingPhotoWithMarker : FrameLayout,
    IForCustomImageView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    abstract fun getResultPicture(): Bitmap?
    abstract fun setText1(text: String)
    abstract fun setText2(text: String)
    abstract fun setMarkerCoord(x: Float, y: Float)
}