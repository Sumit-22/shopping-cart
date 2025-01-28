package com.example.blinkshop

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
//
//class WavyPathView @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyleAttr: Int = 0
//) : View(context, attrs, defStyleAttr) {
//
//    private val wavyData = "M 0 2000 0 500 Q 62.5 280 125 500 t 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 v1000 z"
//
//    private var waveColor: Int = 0xFF000000.toInt() // Default color: black
//    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        style = Paint.Style.STROKE
//        strokeWidth = 5f
//    }
//    private val path: Path = Path()
//
//    init {
//        // Parse custom attributes if needed (optional)
//        context.theme.obtainStyledAttributes(attrs, R.styleable.WaveView, 0, 0).apply {
//            try {
//                waveColor = getColor(R.styleable.WaveView_waveColor, waveColor)
//                paint.color = waveColor
//            } finally {
//                recycle()
//            }
//        }
//
//        // Parse path data
//        parsePathData(wavyData)
//    }
//
//    private fun parsePathData(data: String) {
//        val commands = data.split(" ")
//        path.reset()
//
//        var currentX = 0f
//        var currentY = 0f
//
//        for (command in commands) {
//            when {
//                command.startsWith("M") -> {
//                    val coords = command.substring(1).split(",")
//                    currentX = coords[0].toFloat()
//                    currentY = coords[1].toFloat()
//                    path.moveTo(currentX, currentY)
//                }
//                command.startsWith("Q") -> {
//                    val coords = command.substring(1).split(",")
//                    val cx = coords[0].toFloat()
//                    val cy = coords[1].toFloat()
//                    val x = coords[2].toFloat()
//                    val y = coords[3].toFloat()
//                    path.quadTo(cx, cy, x, y)
//                    currentX = x
//                    currentY = y
//                }
//                command.startsWith("t") -> {
//                    val coords = command.substring(1).split(",")
//                    val x = coords[0].toFloat()
//                    val y = coords[1].toFloat()
//                    path.rQuadTo(0f, 0f, x, y)
//                    currentX += x
//                    currentY += y
//                }
//                command.startsWith("v") -> {
//                    val vertical = command.substring(1).toFloat()
//                    path.rLineTo(0f, vertical)
//                    currentY += vertical
//                }
//                command.startsWith("z") -> {
//                    path.close()
//                }
//            }
//        }
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        canvas.drawPath(path, paint)
//    }
//}
class WavyPathView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val wavyData = "M 0 500 Q 62.5 280 125 500 t 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 125 0 v1000 z"

    private var waveColor: Int = 0xFF000000.toInt() // Default: Black
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = waveColor
    }
    private val path: Path = Path()

    init {
        try {
            context.theme.obtainStyledAttributes(attrs, R.styleable.WaveView, 0, 0).apply {
                try {
                    waveColor = getColor(R.styleable.WaveView_waveColor, waveColor)
                    paint.color = waveColor
                    Log.d("WavyPathView", "Initializing WavyPathView with wavyData: $wavyData")
                } finally {
                    recycle()
                }
            }
            parsePathData(wavyData)
        } catch (e: Exception) {
            Log.e("WavyPathView", "Error initializing WavyPathView", e)
            throw RuntimeException("WavyPathView initialization failed", e)
        }
    }
/*
    private fun parsePathData(data: String) {
        val commands = data.split(" ")
        path.reset()

        var currentX = 0f
        var currentY = 0f

        for (command in commands) {
            when {
                command.startsWith("M") -> {
                    val coords = command.substring(1).split(",")
                    currentX = coords[0].toFloat()
                    currentY = coords[1].toFloat()
                    path.moveTo(currentX, currentY)
                }
                command.startsWith("Q") -> {
                    val coords = command.substring(1).split(",")
                    val cx = coords[0].toFloat()
                    val cy = coords[1].toFloat()
                    val x = coords[2].toFloat()
                    val y = coords[3].toFloat()
                    path.quadTo(cx, cy, x, y)
                    currentX = x
                    currentY = y
                }
                command.startsWith("t") -> {
                    val coords = command.substring(1).split(",")
                    val x = coords[0].toFloat()
                    val y = coords[1].toFloat()
                    path.rQuadTo(0f, 0f, x, y)
                    currentX += x
                    currentY += y
                }
                command.startsWith("v") -> {
                    val vertical = command.substring(1).toFloat()
                    path.rLineTo(0f, vertical)
                    currentY += vertical
                }
                command.startsWith("z") -> {
                    path.close()
                }
            }
        }
    }
*/
private fun parsePathData(data: String) {
    path.reset()
    try {
        val commands = data.split(" ")
        var currentX = 0f
        var currentY = 0f

        commands.forEach { command ->
            if (command.isEmpty()) return@forEach // Skip empty strings

            when {
                command.startsWith("M") -> { // MoveTo command
                    val coords = command.substring(1).split(",")
                    if (coords.size == 2) {
                        currentX = coords[0].toFloatOrNull() ?: 0f
                        currentY = coords[1].toFloatOrNull() ?: 0f
                        path.moveTo(currentX, currentY)
                    }
                }
                command.startsWith("Q") -> { // QuadTo command
                    val coords = command.substring(1).split(",")
                    if (coords.size == 4) {
                        val x1 = coords[0].toFloatOrNull() ?: 0f
                        val y1 = coords[1].toFloatOrNull() ?: 0f
                        val x2 = coords[2].toFloatOrNull() ?: 0f
                        val y2 = coords[3].toFloatOrNull() ?: 0f
                        path.quadTo(x1, y1, x2, y2)
                    }
                }
                command.startsWith("t") -> { // Shorthand curve command
                    val coords = command.substring(1).split(",")
                    if (coords.size == 2) {
                        val dx = coords[0].toFloatOrNull() ?: 0f
                        val dy = coords[1].toFloatOrNull() ?: 0f
                        currentX += dx
                        currentY += dy
                        path.lineTo(currentX, currentY)
                    }
                }
                command.startsWith("v") -> { // Vertical line
                    val dy = command.substring(1).toFloatOrNull() ?: 0f
                    currentY += dy
                    path.lineTo(currentX, currentY)
                }
                command.startsWith("z") -> { // Close path
                    path.close()
                }
                else -> {
                    // Handle unknown commands gracefully
                    Log.w("WavyPathView", "Unknown command: $command")
                }
            }
        }
    } catch (e: Exception) {
        Log.e("WavyPathView", "Error parsing path data: $data", e)
    }
}
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Rebuild the path using the width and height of the view
        parsePathData(wavyData)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.d("WavyPathView", "onDraw called. Canvas size: ${canvas.width}x${canvas.height}")
        Log.d("WavyPathView", "Path: $path")
        canvas.drawPath(path, paint)
    }
}