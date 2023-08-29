package com.example.twentytwo

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Collections

class DrawThread(holder: SurfaceHolder, private val gameView: GameView) : Thread() {

    var holder: SurfaceHolder
    var pieceArr: ArrayList<Piece> = ArrayList()
    val resources: Resources

    private val targetFPS = 30
//    var dMove = 0F // for move animation

    private var complete = false
    private var emptyUp = 22 // pos of empty down piece
    private var emptyDown = 23 // pos of empty down piece
    private val moveDuration = 200F // anim time

    private val pieceHW = 0.9F // Proportion of one piece, H/W
    private val viewWidth: Float = Resources.getSystem().displayMetrics.widthPixels.toFloat()
    private val viewHeight: Float = Resources.getSystem().displayMetrics.heightPixels.toFloat()
    private var borderWidth = maxOf(minOf(viewWidth - 120, (viewHeight - 1000) / pieceHW), 100F)
    private var borderHeight = borderWidth * pieceHW

    private var solveTime = 0L
    var startTime = 0L
    var movesCount = 0
    var lastMoveTime = 0L

    var resetRequest = false

    val gameActivity: GameActivity = gameView.context as GameActivity
    private var pref : SharedPreferences? = null

    private val timerOn : Boolean
    private val soundOn : Boolean

    private val borderPath = Path()
    private val borderPathPaint = Paint()

//    private val gameAct: GameActivity = gameView.context as GameActivity
//    private val nextButton: Button = gameAct.findViewById(R.id.game_btn_next)

    private fun isFacedUp(pos:Int) : Boolean {
        return (pos % 2 == 0) xor (pos in 5..18)
    }

    private fun getOppositeDir(dir:Int): Int{
        val arr = intArrayOf(1, 0, 5, 4, 3, 2)
        return arr[dir]
    }

    private fun getDirPos(pos:Int, dir:Int) : Int{
        //  5 7 4
        // 1  #  0  Directions
        //  3 6 2

        if(pos !in 0..23 || dir !in 0..7) return -1

        var newDir = -1
        when(dir){
            0 -> {
                if(pos in intArrayOf(4, 11, 18, 23)) return -1
                return pos + 1
            }
            1 -> {
                if(pos in intArrayOf(0, 5, 12, 19)) return -1
                return pos - 1
            }
            6 -> {
                return when(pos){
                    in 0..4 -> pos + 6
                    in 5..11 -> pos + 7
                    in 13..17 -> pos + 6
                    else -> -1
                }
            }
            7 -> {
                return when(pos){
                    in 6..10 -> pos - 6
                    in 12..18 -> pos - 7
                    in 19..23 -> pos - 6
                    else -> -1
                }
            }
        }

        if(isFacedUp(pos)){
            newDir = when(dir){
                2 -> 6
                3 -> 6
                4 -> 0
                5 -> 1
                else -> -1
            }
        }
        else{
            newDir = when(dir){
                2 -> 0
                3 -> 1
                4 -> 7
                5 -> 7
                else -> -1
            }
        }
//        Log.w("new dir", newDir.toString())
        return getDirPos(pos, newDir)
    }

    private fun genPieceOrder() : ArrayList<Int>{
        var arrup = ArrayList<Int>()
        var arrdown = ArrayList<Int>()
        for(i in 0..21){
            if(isFacedUp(i)) arrup.add(i+1)
            else arrdown.add(i+1)
        }
        arrup.shuffle()
        arrdown.shuffle()
        var numbers = ArrayList<Int>()
        for(i in 0..21){
            if(isFacedUp(i)){
                numbers.add(arrup.first())
                arrup.removeFirst()
            }
            else{
                numbers.add(arrdown.first())
                arrdown.removeFirst()
            }
        }
        return numbers
    }

    private fun easyPieceOrder() : ArrayList<Int>{
        var numbers = ArrayList<Int>()
        for(i in 0..21){
            numbers.add(i+1)
        }
        return numbers
    }

    init {

        pref = gameView.context.getSharedPreferences("TABLE",  Context.MODE_PRIVATE)
        timerOn = pref?.getBoolean("timer", true)!!
        soundOn = pref?.getBoolean("sound", true)!!

        holder.also { this.holder = it }
        resources = gameView.resources

        borderWidth += 25F
        borderHeight += 28F
        borderPath.moveTo(viewWidth * 0.5F - borderWidth * 0.5F, viewHeight * 0.5F)
        borderPath.lineTo(viewWidth * 0.5F - borderWidth * 0.25F, viewHeight * 0.5F - borderHeight * 0.5F)
        borderPath.lineTo(viewWidth * 0.5F + borderWidth * 0.25F, viewHeight * 0.5F - borderHeight * 0.5F)
        borderPath.lineTo(viewWidth * 0.5F + borderWidth * 0.5F, viewHeight * 0.5F)
        borderPath.lineTo(viewWidth * 0.5F + borderWidth * 0.25F, viewHeight * 0.5F + borderHeight * 0.5F)
        borderPath.lineTo(viewWidth * 0.5F - borderWidth * 0.25F, viewHeight * 0.5F + borderHeight * 0.5F)
        borderPath.lineTo(viewWidth * 0.5F - borderWidth * 0.5F, viewHeight * 0.5F)
        borderPath.close()

        borderPathPaint.color = resources.getColor(R.color.darker_blue)
        borderPathPaint.strokeWidth = 15F
        borderPathPaint.style = Paint.Style.STROKE

        resetField()
    }

    fun resetField(){

        movesCount = 0
        complete = false

        emptyUp = 22
        emptyDown = 23

        pieceArr = ArrayList<Piece>()

        val numbers = genPieceOrder()
//        val numbers = easyPieceOrder() // To debug win state
//        Collections.swap(numbers, 16,18)
//
        for(i in 0..21){
            pieceArr.add(Piece(i, numbers[i], gameView))
        }

        // Two empty spaces
        pieceArr.add(Piece(22, -1, gameView))
        pieceArr.add(Piece(23, -2, gameView))

        startTime = System.currentTimeMillis()
    }

    fun stopAllAnim(){
        pieceArr.forEach(){
            it.stopAnimation()
        }
    }

    private fun checkWin() : Boolean {
        for(i in 0..21){
            if(pieceArr[i].num != i + 1){
                return false
            }
        }
        return true
    }

    private fun timeToString(solveTime: Long, showMS: Boolean): String {
        val ms = (solveTime / 100) % 10
        val sec = (solveTime / 1000) % 60
        val min = (solveTime / 60000)
        return "$min:" + (if(sec<10) "0" else "") +  "$sec" + (if(showMS) ".$ms" else "")
    }

    private fun setVictoryViewActive(isActive: Boolean){
        gameActivity.findViewById<Button>(R.id.game_btn_next).isClickable = isActive
        gameActivity.findViewById<TextView>(R.id.level_complete_text_view).alpha = if(isActive) 1F else 0F
        gameActivity.findViewById<LinearLayout>(R.id.victory_layout).alpha = if(isActive) 1F else 0F
//        btn.setBackgroundColor(if(isActive) Color.rgb(133,188,222) else Color.WHITE)
//        btn.visibility = if(isActive) View.VISIBLE else View.INVISIBLE
//        gameAct.findViewById<Button>(R.id.game_btn_next).visibility = if(\View.VISIBLE
    }

    private fun playSound(){
        gameActivity.playSoundPiece()
    }

    private fun swapPieces(pos: Int, posEmpty: Int){

        if(isFacedUp(pos))
            emptyUp = pos
        else
            emptyDown = pos

        movesCount++
        Collections.swap(pieceArr, pos, posEmpty)
        pieceArr[pos].move(pos, true)
        pieceArr[posEmpty].move(posEmpty, true)
    }

    private fun swapPieceArr(arr: ArrayList<Int>){
        if(arr.isEmpty()) return
        var prev = arr.last()
        arr.removeLast()
        while(arr.isNotEmpty()){
            swapPieces(arr.last(), prev)
            prev = arr.last()
            arr.removeLast()
        }
    }

    override fun run() {
//        var pt = startTime
//        var isMoved = true

        // init for views properties
        setVictoryViewActive(false)

        while (running) {

            val frameBeginTime = System.currentTimeMillis()

            if(resetRequest){
                resetRequest = false
                setVictoryViewActive(false)
                resetField()
            }

            if(complete) continue

            for(i in pieceArr.indices){
                if(pieceArr[i].num < 0) continue
                if (pieceArr[i].intersect(xTouch, yTouch)) {
                    xTouch = -100F
                    yTouch = -100F

//                    Log.w("ng", getDirPos(i).toString())

                    var isMoved = false

                    val pos = pieceArr[i].pos
                    for(i in 0..5){
                        val p1 = getDirPos(pos, i)
                        val p2 = getDirPos(p1, i)

                        if(p1 != -1 && p2 != -1) {
                            if((p1 == emptyDown || p1 == emptyUp) && (p2 == emptyDown || p2 == emptyUp)){

                                playSound()
                                stopAllAnim()
                                swapPieces(pos, p2)
                                isMoved = true
                                lastMoveTime = System.currentTimeMillis()
                                break
                            }
                        }
                    }

                    if(!isMoved) { // try multipush
                        for(dir in 0..5){
                            val arr1 = ArrayList<Int>()
                            val arr2 = ArrayList<Int>()
                            arr1.add(pos)

                            var curPos = pos
                            var solved = false
                            var ek = 0

                            for(i in 0..10){
                                val newPos = getDirPos(curPos, dir)
                                if(newPos == -1)
                                    break

                                if(i % 2 == 0)
                                    arr2.add(newPos)
                                else
                                    arr1.add(newPos)

                                if(newPos in intArrayOf(emptyUp, emptyDown))
                                    ek++
                                if(ek == 2){
                                    solved = true
                                    break
                                }
                                curPos = newPos
                            }

                            if(solved) {
                                playSound()
                                stopAllAnim()
                                swapPieceArr(arr1)
                                swapPieceArr(arr2)
                                lastMoveTime = System.currentTimeMillis()
                                break
                            }
                        }
                    }


                }
            }

            complete = checkWin()
            if(complete){
                solveTime = System.currentTimeMillis() - startTime
                gameActivity.playSoundWin()
            }

            val canvas = this.holder.lockCanvas()
            if (canvas != null) {
                try {

//                    val drawStartTime = System.currentTimeMillis()

                    val dMove: Float = kotlin.math.min(1F, (System.currentTimeMillis() - lastMoveTime) / moveDuration)

                    canvas.drawColor(Color.WHITE)
                    canvas.drawPath(borderPath, borderPathPaint)

                    if(complete){
                        stopAllAnim()
                    }
                    for (i in pieceArr.indices) {
                        pieceArr[i].draw(canvas, dMove)
                    }

//                    Log.w("drawPieceTime", (System.currentTimeMillis() - drawStartTime).toString())

                    val curSolveTime =
                        timeToString(System.currentTimeMillis() - startTime, false)

                    gameActivity.findViewById<TextView>(R.id.game_text_timer).text = curSolveTime
                    gameActivity.findViewById<TextView>(R.id.game_text_moves).text = movesCount.toString()

//                    Log.w("drawAllTime", (System.currentTimeMillis() - drawStartTime).toString())

                    gameActivity.setInfoVisibility(timerOn && !complete)
                    setVictoryViewActive(complete)

                    if(complete){

                        var col: Int

                        val vTimer = gameActivity.findViewById<TextView>(R.id.victory_timer)
                        val vMoves = gameActivity.findViewById<TextView>(R.id.victory_move)

                        vTimer.text = timeToString(solveTime, true)
                        vMoves.text = movesCount.toString()

                        Log.w("MOVES", movesCount.toString())

                        col = resources.getColor(if(saveTime(solveTime)) R.color.colorPrimaryDark else R.color.text_color)
                        vTimer.setTextColor(col)

                        col = resources.getColor(if(saveMove(movesCount.toLong())) R.color.colorPrimaryDark else R.color.text_color)
                        vMoves.setTextColor(col)
                        Log.w("MOVES", movesCount.toString())


                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    holder.unlockCanvasAndPost(canvas)
                }
            }

            val dt = (1000F / targetFPS).toInt() - (System.currentTimeMillis() - frameBeginTime)
            if(dt > 0){
                sleep(dt)
            }
            else{
//                Log.w("too slow", (System.currentTimeMillis() - frameBeginTime).toString())
            }
        }
    }

    private fun saveTime(time: Long) : Boolean{
        val editor = pref?.edit()
        val curBest = pref?.getLong("bestTime", -1)!!
        if(curBest == -1L || curBest > time) {
            editor?.putLong("bestTime", time)
            editor?.apply()
            return true
        }
        return false
    }

    private fun saveMove(moves: Long) : Boolean{
        val editor = pref?.edit()
        val curBest = pref?.getLong("bestMove", -1)!!
        if(curBest == -1L || curBest > moves) {
            editor?.putLong("bestMove", moves)
            editor?.apply()
            return true
        }
        return false
    }

    fun requestStop() {
        running = false
    }

    fun setXY(x_touch: Float, y_touch: Float){
        this.xTouch = x_touch
        this.yTouch = y_touch
    }

    @Volatile
    var running: Boolean = true
    @Volatile
    var xTouch: Float = 0F
    @Volatile
    var yTouch: Float = 0F
}