package com.example.stopwatch

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.max
import kotlin.math.min

class CircularArc(context: Context, attr: AttributeSet) : View(context, attr) {
    private val MAX_SWEEP = 360f
    private val paint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    private var padding : Float = 2f
    private var strokeWidth: Float = 15f
    private var sweepAngle = 0f
    private var startAngle = -90f

    init {
        val ta = context.obtainStyledAttributes(attr, R.styleable.CircularArc)
        padding = ta.getDimension(R.styleable.CircularArc_padding, 2f)
        paint.strokeWidth = ta.getDimension(R.styleable.CircularArc_strokeWidth, 15f)
        paint.color = ta.getColor(R.styleable.CircularArc_arcColor, Color.CYAN)
        setArcPercent(ta.getFloat(R.styleable.CircularArc_arcPercent, 0.0f))
        ta.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val len = min(width, height)

        val rect = RectF(
            width / 2f - len / 2f + padding,
            height / 2f - len / 2f + padding,
            width / 2f + len / 2f - padding,
            height / 2f + len / 2f - padding
        )
        canvas.drawArc(rect, startAngle, sweepAngle, false, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        setMeasuredDimension(size, size)
    }

    fun setArcPercent(percent: Float) {
        sweepAngle = min(360f, max(0f, percent * MAX_SWEEP))
        invalidate()
    }
}