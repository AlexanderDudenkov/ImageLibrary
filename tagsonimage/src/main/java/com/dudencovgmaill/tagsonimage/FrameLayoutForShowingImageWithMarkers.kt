package com.dudencovgmaill.tagsonimage

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.*
import com.dudencovgmaill.tagsonimage.base.AFrameLayoutForShowingImageWithMarkers

@Suppress("UNCHECKED_CAST")
open class FrameLayoutForShowingImageWithMarkers : AFrameLayoutForShowingImageWithMarkers, LifecycleObserver {

    protected open lateinit var pictureLive: MediatorLiveData<Bitmap>
    protected open lateinit var viewsLive: MediatorLiveData<ArrayList<ViewWithCoord>>
    protected open var numLis: ((views: List<View>, number: Int) -> Unit)? = null
    protected open var lo: LifecycleOwner? = null
    protected open var ivBig: View? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun setPicture(pic: Bitmap?) {
        if (pic != null) {
            pictureLive.value = pic
        }
    }

    override fun setLifecycle(owner: LifecycleOwner) {
        lo = owner
        lo?.lifecycle?.addObserver(this)
        init()
    }

    override fun setListViews(list: List<ViewWithCoord>?) {
        if (!list.isNullOrEmpty()) {
            viewsLive.value?.clear()
            viewsLive.value = list as ArrayList<ViewWithCoord>
        }
    }

    override fun getPosPressedView(listener: (views: List<View>, number: Int) -> Unit) {
        numLis = listener
    }

    protected fun init() {
        layoutParams =
            ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        ivBig = ImageView(context).apply {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        addView(ivBig)

        pictureLive = MediatorLiveData()
        pictureLive.addSource(MutableLiveData<Bitmap>()) { pictureLive.value = it }

        viewsLive = MediatorLiveData()
        viewsLive.addSource(MutableLiveData<ArrayList<ViewWithCoord>>()) { viewsLive.value = it }

        pictureLive {
            (ivBig as ImageView).apply {
                scaleType = ImageView.ScaleType.FIT_XY
                setImageBitmap(it)
            }
        }
        viewsLive { addViewsOnItself(it) }
    }

    protected open fun pictureLive(l: (it: Bitmap) -> Unit) {
        if (lo == null && pictureLive.value == null) {
            return
        }
        pictureLive.observe(lo!!, Observer { l.invoke(it) })
    }

    protected open fun viewsLive(l: (it: ArrayList<ViewWithCoord>) -> Unit) {
        if (lo == null && viewsLive.value == null) {
            return
        }
        viewsLive.observe(lo!!, Observer { l.invoke(it) })
    }

    protected open fun addViewsOnItself(list: ArrayList<ViewWithCoord>) {
        var clickedView: View? = null

        list.forEach {
            addView(it.view.apply {
                this?.setOnClickListener { v ->
                    if (v != clickedView && v.tag is Int) {
                        clickedView = v
                        numLis?.invoke(list as @ParameterName(name = "views") List<View>, v.tag as Int)
                    }
                }

                this?.animate()
                    ?.x(it.x)
                    ?.y(it.y)
                    ?.setDuration(0)
                    ?.start()
            })
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected open fun clean() {
        pictureLive.value?.recycle()
    }
}