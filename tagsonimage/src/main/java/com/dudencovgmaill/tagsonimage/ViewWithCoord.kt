package com.dudencovgmaill.tagsonimage

import android.view.View

data class ViewWithCoord(
    var view: View? = null,
    var x: Float = 0f,
    var y: Float = 0f,
    var xScale: Float = 0f,
    var yScale: Float = 0f
)