package com.dudencovgmaill.tagsonimage

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.*
import android.graphics.*
import com.dudencovgmaill.tagsonimage.base.AMarkerView

@SuppressLint("ViewConstructor")
class MarkerView : AMarkerView, LifecycleObserver {

    private lateinit var text1Live: MediatorLiveData<String>
    private lateinit var text2Live: MediatorLiveData<String>
    private var bmpListener: ((bmp: Bitmap) -> Unit)? = null
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private var bgBmp: Bitmap? = null
    private var lo: LifecycleOwner? = null
    private var drawableLis: ((resId: Int) -> Unit)? = null

    constructor(context: Context, owner: LifecycleOwner) : super(context) {
        init(owner)
    }

    constructor(context: Context, attrs: AttributeSet, owner: LifecycleOwner) : super(context, attrs) {
        init(owner)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int, owner: LifecycleOwner) : super(
        context,
        attrs,
        defStyle
    ) {
        init(owner)
    }

    override fun setBackground(resId: Int) {
        drawableLis?.invoke(resId)
    }

    override fun setText1(text: String) {
        text1Live.addSource(MutableLiveData<String>().apply { value = text }) {
            text1Live.value = it
        }
    }

    override fun setText2(text: String) {
        text2Live.addSource(MutableLiveData<String>().apply { value = text }) { text2Live.value = it }
    }

    override fun getViewAsBitmap(bmpLis: (bmp: Bitmap) -> Unit) {
        bmpListener = bmpLis
        bmpListener?.invoke(createBitmapFromView((this@MarkerView)))
    }

    private fun init(owner: LifecycleOwner) {
        lo = owner
        lo?.lifecycle?.addObserver(this)

        text1Live = MediatorLiveData()
        text2Live = MediatorLiveData()

        createView()
    }

    private fun createView() {
        layoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                orientation = VERTICAL
                background = resources.getDrawable(R.drawable.ic_label_black)
                drawableLis = { background = resources.getDrawable(it) }
                setPadding(
                    resources.getDimension(R.dimen.padding_left_marker_view).toInt(),
                    resources.getDimension(R.dimen.padding_top_marker_view).toInt(),
                    resources.getDimension(R.dimen.padding_right_marker_view).toInt(),
                    resources.getDimension(R.dimen.padding_bottom_marker_view).toInt()
                )
            }
        val tv1 = AppCompatTextView(context).apply { tag = "marker_tv1" }
        val tv2 = AppCompatTextView(context).apply { tag = "marker_tv2" }

        addView(tv1.apply {
            observe(text1Live) {
                text = it
                bmpListener?.invoke(createBitmapFromView(this@MarkerView))
            }

            textSize = resources.getDimension(R.dimen.text_size_marker_view)

            gravity = Gravity.CENTER_VERTICAL
            setTextColor(resources.getColor(R.color.text_color_marker_view))
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f)
        })

        addView(tv2.apply {
            observe(text2Live) {
                text = it
                observe(text2Live) { bmpListener?.invoke(createBitmapFromView(this@MarkerView)) }
            }
            textSize = resources.getDimension(R.dimen.text_size_marker_view)
            gravity = Gravity.CENTER_VERTICAL
            setTextColor(resources.getColor(R.color.text_color_marker_view))
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f)
        })
    }

    private fun observe(liveData: LiveData<String>, l: (it: String) -> Unit) {
        if (lo == null && liveData.value == null) {
            return
        }
        liveData.observe(lo!!, Observer { l.invoke(it) })
    }

    private fun createBitmapFromView(v: View): Bitmap {
        v.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        v.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
        bitmap = Bitmap.createBitmap(
            v.measuredWidth,
            v.measuredHeight,
            Bitmap.Config.ARGB_8888
        )

        canvas = Canvas(bitmap!!)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(canvas)
        return bitmap!!
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun clean() {
        bgBmp?.recycle()
        bitmap?.recycle()
        canvas = null
    }
}


