package com.dudencovgmaill.tagsonimage.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.TextViewCompat
import java.util.*

fun Context.getDisplayWidthDp() = resources.displayMetrics.widthPixels / resources.displayMetrics.density

fun Context.getDisplayWidthDpInt() = (resources.displayMetrics.widthPixels / resources.displayMetrics.density).toInt()

fun Context.getDisplayHeightDp() = resources.displayMetrics.heightPixels / resources.displayMetrics.density

fun Context.getDisplayHeightDpInt() = (resources.displayMetrics.heightPixels / resources.displayMetrics.density).toInt()

fun ViewGroup?.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false) =
    inflater().inflate(layoutRes, this, attachToRoot)

fun ViewGroup?.inflater() = LayoutInflater.from(this?.context)

fun View?.setBackgroundColorExt(colorResId: Int) {
    this?.setBackgroundColor(this.getColor(colorResId)!!)
}

fun View?.setBackgroundColorExt(colorHex: String) {
    this?.setBackgroundColorExt(Color.parseColor(colorHex))
}

fun View?.getColor(colorResId: Int) = this?.resources?.getColor(colorResId)

fun View?.setGone() {
    if (this == null || this.visibility == View.GONE) {
        return
    }
    this.visibility = View.GONE
}

fun View?.setInvisible() {
    if (this == null || this.visibility == View.INVISIBLE) {
        return
    }
    this.visibility = View.INVISIBLE
}

fun View?.setVisible() {
    if (this == null || this.visibility == View.VISIBLE) {
        return
    }
    this.visibility = View.VISIBLE
}

fun View?.setVisibility(state: Boolean) {
    if (state) {
        setVisible()
    } else {
        setInvisible()
    }
}

fun View?.setGone(state: Boolean) {
    if (state) {
        setVisible()
    } else {
        setGone()
    }
}

fun View?.loadBitmapFromView(): Bitmap {
    val b =
        Bitmap.createBitmap(this?.layoutParams?.width ?: 0, this?.layoutParams?.height ?: 0, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    this?.layout(0, 0, this.layoutParams.width, this.layoutParams.height)
    this?.draw(c)
    return b
}
