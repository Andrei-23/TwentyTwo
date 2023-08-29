package com.example.twentytwo

import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.*
import android.graphics.Paint.Style
import android.util.Log

class Piece(pos:Int, number:Int, private val gameView: GameView){

    private var leftMargin: Float
    private var topMargin: Float
    private var width: Float
    private var height: Float

    private val pieceHW = 0.9F // Proportion of one piece, H/W

    private val resources: Resources = gameView.resources
//    private var bitmap: Bitmap
    private var x: Float = 0F
    private var y: Float = 0F

    // animation
    private var old_x: Float = 0F
    private var old_y: Float = 0F
    var isMoving: Boolean = false

    private val xPadding = 8F
    private val yPadding = 3F

    //integer coordinates
    var xPos = 0
    var yPos = 0

    var faceUp: Boolean = true
    var num = number // written number in game
    var pos = 0 // index in array

    fun isFasedUp(pos:Int) : Boolean {
        if(pos < 0) return pos == -1
        return (pos % 2 == 0) xor (pos in 5..18)
    }

    init {

        val viewWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        val viewHeight = Resources.getSystem().displayMetrics.heightPixels.toFloat()
        val fieldWidth = maxOf(minOf(viewWidth - 120, (viewHeight - 1000) / pieceHW), 100F)
        val fieldHeight = fieldWidth * pieceHW

        width = fieldWidth * 0.25F
        height = fieldHeight * 0.25F
        leftMargin = (viewWidth - fieldWidth + width) * 0.5F
        topMargin = (viewHeight - fieldHeight) * 0.5F

        this.move(pos, false)

        faceUp = isFasedUp(pos)

        Log.w("piece", "init complete")
    }

    fun draw(canvas: Canvas, dMove: Float) {
        if(num < 0) return

        // dMove: 0F - begin, 1F - end
        val xDraw = old_x + (x - old_x) * dMove
        val yDraw = old_y + (y - old_y) * dMove

        val p = Paint()
        p.style = Paint.Style.FILL_AND_STROKE
        p.isAntiAlias = true

        val path = Path()
        if(faceUp) {
            path.moveTo(xDraw + xPadding, yDraw + height - yPadding)
            path.lineTo(xDraw + width - xPadding, yDraw + height - yPadding)
            path.lineTo(xDraw + width / 2F, yDraw+ yPadding)
            path.lineTo(xDraw + xPadding, yDraw + height - yPadding)
            p.color = resources.getColor(R.color.piece_up_bg)
        }
        else{
            path.moveTo(xDraw + xPadding, yDraw + yPadding)
            path.lineTo(xDraw + width - xPadding, yDraw + yPadding)
            path.lineTo(xDraw + width / 2F, yDraw + height - yPadding)
            path.lineTo(xDraw + xPadding, yDraw + yPadding)
            p.color = resources.getColor(R.color.piece_down_bg)
        }
        path.close()
        canvas.drawPath(path, p)

//        val img_rect = Rect(0, 0, bitmap.width, bitmap.height)
//        val pos_rect = RectF(xDraw, yDraw, xDraw+width, yDraw+height)
//        canvas.drawBitmap(bitmap, img_rect, pos_rect, p)
//        canvas.drawBitmap(bitmap, xDraw, yDraw, p)

        val p1 = Paint()
        p1.color = resources.getColor(R.color.piece_text_color)
        p1.textSize = 75F
        p1.typeface = Typeface.createFromAsset(resources.assets, "fonts/arial_round_bold.ttf")
        val textWidth: Float = p1.measureText(num.toString())
        val dy = if(faceUp) 50F else 0F // idk how to measure text height
        canvas.drawText(num.toString(), xDraw + (width-textWidth) / 2F, yDraw + height / 2F + dy, p1)
    }

    fun intersect(x_touch: Float, y_touch: Float): Boolean {
        val k = 2 * height / width
        var lx = x_touch - x
        var ly = y_touch - y
        if(lx in 0F..width && ly in 0F..height) {
            if (!faceUp) ly = height - ly
            return(ly >= kotlin.math.abs(-k * lx + height))
        }
        return false
    }

    fun move(new_pos: Int, animate: Boolean){
        pos = new_pos
        xPos = new_pos
        yPos = 0
        if(xPos in 5..11){
            xPos -= 5
            yPos = 1
        }
        else if(xPos in 12..18){
            xPos -= 11
            yPos = 2
        }
        else if(xPos >= 19){
            xPos -= 16
            yPos = 3
        }

        old_x = x
        old_y = y
        x = leftMargin + (xPos - yPos) * (width * 0.5F)
        y = topMargin + yPos * (height)
        if(animate) {
            // anime
        }
        else{
            old_x = x
            old_y = y
        }
    }

    fun stopAnimation(){
        old_x = x
        old_y = y
        isMoving = false
    }
}
