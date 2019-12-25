package com.dudencovgmaill.tagsonimage.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.dudencovgmaill.tagsonimage.ViewWithCoord

abstract class AFrameLayoutForShowingImageWithMarkers : FrameLayout,
    IForCustomImageView {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    abstract fun setListViews(list: List<ViewWithCoord>?)
    abstract fun getPosPressedView(listener: (views: List<View>, i: Int) -> Unit)

    fun setSize(width: Int, height: Int) {
        layoutParams =
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                this.width = width
                this.height = height
            }
    }
}
