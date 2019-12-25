package com.dudencovgmaill.tagsonimage

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.*
import com.dudencovgmaill.tagsonimage.base.AFrameLayoutForSavingPhotoWithMarker
import com.dudencovgmaill.tagsonimage.util.getDisplayWidthDp
import com.dudencovgmaill.tagsonimage.util.loadBitmapFromView

open class FrameLayoutForSavingPhotoWithMarker :
    AFrameLayoutForSavingPhotoWithMarker {

    private lateinit var text1Live: MediatorLiveData<String>
    private lateinit var text2Live: MediatorLiveData<String>
    protected open var bigPicLive: MediatorLiveData<Bitmap> = MediatorLiveData()
    protected open var lo: LifecycleOwner? = null
    protected open var ivBig: View? = null
    protected open var markerView: LinearLayout? = null
    private var cxt: Context
    private var lisCoord: ((x: Float, y: Float) -> Unit)? = null

    constructor(context: Context) : super(context) {
        cxt = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        cxt = context
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        cxt = context
    }

    override fun setPicture(pic: Bitmap?) {
        bigPicLive.addSource(MutableLiveData<Bitmap>().apply { value = pic }) { bigPicLive.value = it }
    }

    override fun getResultPicture(): Bitmap? {
        markerView?.background = resources.getDrawable(R.drawable.ic_label)

        return loadBitmapFromView()
    }

    override fun setLifecycle(owner: LifecycleOwner) {
        lo = owner
        init()
    }

    override fun setText1(text: String) {
        text1Live.addSource(MutableLiveData<String>().apply { value = text }) {
            text1Live.value = it
        }
    }

    override fun setText2(text: String) {
        text2Live.addSource(MutableLiveData<String>().apply { value = text }) { text2Live.value = it }
    }

    override fun setMarkerCoord(x: Float, y: Float) {
        lisCoord?.invoke(x, y)
    }

    protected fun init() {
        text1Live = MediatorLiveData()
        text2Live = MediatorLiveData()

        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        ivBig = ImageView(context).apply {
            this.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .apply {
                    gravity = Gravity.CENTER
                }
        }

        addView(ivBig)

        markerView = MarkerView(context, lo!!).apply {

            observe(text1Live) {
                setText1(it)
            }
            observe(text2Live) {
                setText2(it)
            }

            this.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .apply {
                    gravity = Gravity.CENTER
                }
        }
        addView(markerView)

        observeBigPicLive {
            (ivBig as ImageView).setImageBitmap(it)
            val w = it.width
            val h = it.height
            layoutParams = LayoutParams(w, h)

            markerView?.apply {
                layoutParams = LayoutParams((w * 0.10).toInt(), (h * 0.10).toInt())

                findViewWithTag<AppCompatTextView>("marker_tv1").textSize = context.getDisplayWidthDp() * 0.043f
                findViewWithTag<AppCompatTextView>("marker_tv2").textSize = context.getDisplayWidthDp() * 0.043f

                setPadding(
                    ((w * 0.1).toInt() * 0.2f).toInt(),
                    ((h * 0.1).toInt() * 0.2f).toInt(),
                    ((w * 0.1).toInt() * 0.2f).toInt(),
                    ((h * 0.1).toInt() * 0.2f).toInt()
                )
            }
            lisCoord = { x, y ->
                markerView?.animate()
                    ?.x(x * w)
                    ?.y(y * h)
                    ?.setDuration(0)
                    ?.start()
            }
        }
    }


    protected fun observeBigPicLive(l: (it: Bitmap) -> Unit) {
        if (lo == null && bigPicLive.value == null) {
            return
        }
        bigPicLive.observe(lo!!, Observer { l.invoke(it) })
    }

    private fun observe(liveData: LiveData<String>, l: (it: String) -> Unit) {
        if (lo == null && liveData.value == null) {
            return
        }
        liveData.observe(lo!!, Observer { l.invoke(it) })
    }
}