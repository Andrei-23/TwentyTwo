package com.example.twentytwo

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context, attributeSet: AttributeSet) : SurfaceView(context, attributeSet), SurfaceHolder.Callback {

    val drawThread: DrawThread

    init {
        holder.addCallback(this)
        drawThread = DrawThread(holder, this)
    }


    override fun surfaceCreated(p0: SurfaceHolder) {
        drawThread.start()
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        drawThread.requestStop()
        var retry = true
        while (retry) {
            try {
                drawThread.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            retry = false

        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        drawThread.setXY(event!!.x, event.y)
        return false
    }
}