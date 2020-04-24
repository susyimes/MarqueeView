package com.sunfusheng.marqueeview.kt

import android.content.Context
import android.graphics.Typeface
import android.support.annotation.AnimRes
import android.support.annotation.FontRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.text.PrecomputedTextCompat
import android.support.v4.widget.TextViewCompat
import android.support.v7.widget.AppCompatTextView
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.ViewFlipper
import com.sunfusheng.marqueeview.R
import com.sunfusheng.marqueeview.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MarqueeView (context: Context,attributeSet: AttributeSet):ViewFlipper(context,attributeSet){
    private var interval = 3000
    private var hasSetAnimDuration = false
    private var animDuration = 1000
    private var textSize = 14
    private var textColor = -0x1000000
    private var singleLine = false

    private var gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
    private val GRAVITY_LEFT = 0
    private val GRAVITY_CENTER = 1
    private val GRAVITY_RIGHT = 2


    private val DIRECTION_BOTTOM_TO_TOP = 0
    private val DIRECTION_TOP_TO_BOTTOM = 1
    private val DIRECTION_RIGHT_TO_LEFT = 2
    private val DIRECTION_LEFT_TO_RIGHT = 3

    private var direction = DIRECTION_BOTTOM_TO_TOP

    private var typeface: Typeface? = null

    @AnimRes
    private var inAnimResId: Int = R.anim.anim_bottom_in
    @AnimRes
    private var outAnimResId: Int = R.anim.anim_top_out

    private var position = 0
    private var messages: List<Any> = ArrayList<Any>()
    private var onItemClickListener: MarqueeView.OnItemClickListener? = null

    private fun init(context: Context, attrs: AttributeSet, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeViewStyle, defStyleAttr, 0)
        interval = typedArray.getInteger(R.styleable.MarqueeViewStyle_mvInterval, interval)
        hasSetAnimDuration = typedArray.hasValue(R.styleable.MarqueeViewStyle_mvAnimDuration)
        animDuration = typedArray.getInteger(R.styleable.MarqueeViewStyle_mvAnimDuration, animDuration)
        singleLine = typedArray.getBoolean(R.styleable.MarqueeViewStyle_mvSingleLine, false)
        if (typedArray.hasValue(R.styleable.MarqueeViewStyle_mvTextSize)) {
            textSize = typedArray.getDimension(R.styleable.MarqueeViewStyle_mvTextSize, textSize.toFloat()).toInt()
            textSize = Utils.px2sp(context, textSize.toFloat())
        }
        textColor = typedArray.getColor(R.styleable.MarqueeViewStyle_mvTextColor, textColor)
        @FontRes val fontRes = typedArray.getResourceId(R.styleable.MarqueeViewStyle_mvFont, 0)
        if (fontRes != 0) {
            typeface = ResourcesCompat.getFont(context, fontRes)
        }
        val gravityType = typedArray.getInt(R.styleable.MarqueeViewStyle_mvGravity, GRAVITY_LEFT)
        when (gravityType) {
            GRAVITY_LEFT -> gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
            GRAVITY_CENTER -> gravity = Gravity.CENTER
            GRAVITY_RIGHT -> gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
        }
        if (typedArray.hasValue(R.styleable.MarqueeViewStyle_mvDirection)) {
            direction = typedArray.getInt(R.styleable.MarqueeViewStyle_mvDirection, direction)
            when (direction) {
                DIRECTION_BOTTOM_TO_TOP -> {
                    inAnimResId = R.anim.anim_bottom_in
                    outAnimResId = R.anim.anim_top_out
                }
                DIRECTION_TOP_TO_BOTTOM -> {
                    inAnimResId = R.anim.anim_top_in
                    outAnimResId = R.anim.anim_bottom_out
                }
                DIRECTION_RIGHT_TO_LEFT -> {
                    inAnimResId = R.anim.anim_right_in
                    outAnimResId = R.anim.anim_left_out
                }
                DIRECTION_LEFT_TO_RIGHT -> {
                    inAnimResId = R.anim.anim_left_in
                    outAnimResId = R.anim.anim_right_out
                }
            }
        } else {
            inAnimResId = R.anim.anim_bottom_in
            outAnimResId = R.anim.anim_top_out
        }
        typedArray.recycle()
        setFlipInterval(interval)
    }

    fun startWithList(messages: List<Any>?) {
        startWithList(messages, inAnimResId, outAnimResId)
    }

    fun startWithList(messages: List<Any>?, @AnimRes inAnimResId: Int, @AnimRes outAnimResID: Int) {
        if (Utils.isEmpty(messages)) return
        setMessages(messages)
        postStart(inAnimResId, outAnimResID)
    }

    private fun postStart(@AnimRes inAnimResId: Int, @AnimRes outAnimResID: Int) {
        post { start(inAnimResId, outAnimResID) }
    }

    private val isAnimStart = false

    private fun start(@AnimRes inAnimResId: Int, @AnimRes outAnimResID: Int) {
        removeAllViews()
        clearAnimation()
        createTextView()
        // 检测数据源
        if (messages.isEmpty()) {
            throw RuntimeException("The messages cannot be empty!")
        }
        position = 0
        if (messages.size > 1) {
            setInAndOutAnimation(inAnimResId, outAnimResID)
            showNext()
            startFlipping()
        }
    }

    private fun createTextView() {
        for (message in messages) {
            val textView = AppCompatTextView(context)
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.maxLines = 2
            textView.ellipsize = TextUtils.TruncateAt.END
            textView.textSize = textSize.toFloat()
            textView.includeFontPadding = true
            textView.setTextColor(textColor)
//            if (message is CharSequence) {
//                textView.setTextFuture(
//                        PrecomputedTextCompat.getTextFuture(
//                                message,  //文本
//                                textView.textMetricsParamsCompat,  //PrecomputedTextCompat.Params
//                                null) //线程池,
//                )
//            } else if (message is IMarqueeItem) {
//                textView.setTextFuture(
//                        PrecomputedTextCompat.getTextFuture(
//                                message.marqueeMessage()!!,  //文本
//                                textView.textMetricsParamsCompat,  //PrecomputedTextCompat.Params
//                                null) //线程池,
//                )
//            }
            textView.setOnClickListener { v ->
                onItemClickListener?.onItemClick(getPosition(), v as TextView)
            }
            textView.tag = position
            GlobalScope.launch(Dispatchers.Main) {
                val text = withContext(Dispatchers.IO)
                {
                    val params = TextViewCompat.getTextMetricsParams(textView)
                    PrecomputedTextCompat.create(message as CharSequence, params)
                }
                TextViewCompat.setPrecomputedText(textView, text)
            }
            addView(textView)
        }
    }

    fun getPosition(): Int {
        return currentView.tag as Int
    }

    fun getMessages(): List<Any>? {
        return messages
    }

    fun setMessages(messages: List<Any>?) {
        this.messages = messages!!
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, textView: TextView?)
    }

    private fun setInAndOutAnimation(@AnimRes inAnimResId: Int, @AnimRes outAnimResID: Int) {
        val inAnim = AnimationUtils.loadAnimation(context, inAnimResId)
        if (hasSetAnimDuration) inAnim.duration = animDuration.toLong()
        inAnimation = inAnim
        val outAnim = AnimationUtils.loadAnimation(context, outAnimResID)
        if (hasSetAnimDuration) outAnim.duration = animDuration.toLong()
        outAnimation = outAnim
    }


}