package com.dudencovgmaill.tagsonimage.base

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.DrawableRes

abstract class AMarkerView : LinearLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    abstract fun setText1(text: String)
    abstract fun setText2(text: String)
    abstract fun getViewAsBitmap(bmpLis: (bmp: Bitmap) -> Unit)
    abstract fun setBackground(@DrawableRes resId: Int)

    fun setSize(width: Int, height: Int) {
        layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                this.width = width
                this.height = height
            }
    }

}