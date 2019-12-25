package com.dudencovgmaill.tagsonimage

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.doOnLayout
import androidx.lifecycle.*
import com.dudencovgmaill.tagsonimage.base.AFrameLayoutForGettingMarkerCoord

open class FrameLayoutForGettingMarkerCoord :
    AFrameLayoutForGettingMarkerCoord {

    private lateinit var text1Live: MediatorLiveData<String>
    private lateinit var text2Live: MediatorLiveData<String>
    protected open var bigPicLive: MediatorLiveData<Bitmap> = MediatorLiveData()
    protected open var lo: LifecycleOwner? = null
    protected open var ivBig: View? = null
    protected open var markerView: LinearLayout? = null
    private var cxt: Context
    private var lisXtag: ((x: Float) -> Unit)? = null
    private var lisYtag: ((y: Float) -> Unit)? = null
    private var lisXYtag: ((x: Float, y: Float) -> Unit)? = null

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
        pic?.let { bmp ->
            bigPicLive.addSource(MutableLiveData<Bitmap>().apply { value = bmp }) {
                bigPicLive.value = it
            }
        }
    }

    override fun setLifecycle(owner: LifecycleOwner) {
        lo = owner
        init()
    }

    override fun getMarkerCoord(lis: (x: Float, y: Float) -> Unit) {
        lisXYtag = lis
    }

    override fun setText1(text: String) {
        text1Live.addSource(MutableLiveData<String>().apply { value = text }) {
            text1Live.value = it
        }
    }

    override fun setText2(text: String) {
        text2Live.addSource(MutableLiveData<String>().apply { value = text }) { text2Live.value = it }
    }

    protected fun init() {
        text1Live = MediatorLiveData()
        text2Live = MediatorLiveData()
        var coordX = 0f
        var coordY = 0f
        var dX = 0f
        var dY = 0f
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

            this.layoutParams =
                LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    .apply {
                        gravity = Gravity.START
                    }

            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {

                        coordX = event.rawX + dX
                        coordY = event.rawY + dY

                        //d { "coordX=$coordX; coordY=$coordY" }

                        lisXtag?.invoke(event.rawX)
                        lisYtag?.invoke(event.rawY)

                        lisXYtag?.invoke(
                            coordX / if (this@FrameLayoutForGettingMarkerCoord.width == 0) 1 else this@FrameLayoutForGettingMarkerCoord.width,
                            coordY / if (this@FrameLayoutForGettingMarkerCoord.height == 0) 1 else (this@FrameLayoutForGettingMarkerCoord.height)
                        )

                        v.animate()
                            .x(coordX)
                            .y(coordY)
                            .setDuration(0)
                            .start()
                        true
                    }
                    MotionEvent.ACTION_DOWN -> {
                        dX = v.x - event.rawX
                        dY = v.y - event.rawY

                        // d { "v.x=${v.x}; v.y=${v.y}; event.rawX=${event.rawX}; event.rawY=${event.rawY}; dX=$dX; dY=$dY" }
                        true
                    }
                    else -> {
                        //d { "?" }
                        false
                    }
                }
            }

        }

        doOnLayout {
            markerView?.let {
                lisXYtag?.invoke(
                    it.x / if (this@FrameLayoutForGettingMarkerCoord.width == 0) 1 else this@FrameLayoutForGettingMarkerCoord.width,
                    it.y / if (this@FrameLayoutForGettingMarkerCoord.height == 0) 1 else (this@FrameLayoutForGettingMarkerCoord.height)
                )
            }
        }

        addView(markerView)

        observeBigPicLive {
            (ivBig as ImageView).apply {
                layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                scaleType = ImageView.ScaleType.FIT_XY
                setImageBitmap(it)
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