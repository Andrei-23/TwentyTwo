package com.example.twentytwo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout

class GameActivity : AppCompatActivity() {

    private var bMediaPlayer: MediaPlayer? = null
    private var pMediaPlayer: MediaPlayer? = null
    private var wMediaPlayer: MediaPlayer? = null

    lateinit var infoLayout: LinearLayout

    var pref : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        pref = getSharedPreferences("TABLE",  Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        infoLayout = findViewById(R.id.info_layout)
    }

//    fun levelComplete(time:Float, turns:Int){
//        val intent = Intent(this, WinActivity::class.java)
//        intent.putExtra("Time", time)
//        intent.putExtra("Turns", turns)
//        startActivity(intent)
//    }

    override fun onBackPressed() {
        super.onBackPressed()
//        Toast.makeText(this, "Back to main", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onClick(view: View) {
        if(bMediaPlayer == null) {
            bMediaPlayer = MediaPlayer.create(this, R.raw.button_click)
            bMediaPlayer!!.isLooping = false
            val volume = if(pref!!.getBoolean("sound", true)) 0.2F else 0F
            bMediaPlayer!!.setVolume(volume, volume)
        }
        if(bMediaPlayer != null && bMediaPlayer!!.isPlaying){
            bMediaPlayer!!.pause()
        }
        bMediaPlayer!!.seekTo(0)
        bMediaPlayer!!.start()

        when(view.id){
            R.id.game_btn_back -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.game_btn_next -> {
                val gameView = findViewById<GameView>(R.id.game_view)
                gameView.drawThread.resetRequest = true
            }
        }
    }

    fun setInfoVisibility(isVisible: Boolean) {
//        findViewById<TextView>(R.id.game_text_timer).setTextColor(if(isVisible) getColor(R.color.gray) else Color.TRANSPARENT)
//        findViewById<TextView>(R.id.game_text_moves).setTextColor(if(isVisible) getColor(R.color.gray) else Color.TRANSPARENT)
        infoLayout.alpha = if(isVisible) 1F else 0F
    }

    fun playSoundPiece() {
        if(pMediaPlayer == null) {
            pMediaPlayer = MediaPlayer.create(this, R.raw.piece_click)
            pMediaPlayer!!.isLooping = false
            val volume = if(pref!!.getBoolean("sound", true)) 0.4F else 0F
            pMediaPlayer!!.setVolume(volume, volume)
        }
        if(pMediaPlayer != null && pMediaPlayer!!.isPlaying){
            pMediaPlayer!!.pause()
        }
        pMediaPlayer!!.seekTo(0)
        pMediaPlayer!!.start()
    }

    fun playSoundWin() {
        wMediaPlayer = MediaPlayer.create(this, R.raw.level_complete)
        val volume = if(pref!!.getBoolean("sound", true)) 0.3F else 0F
        wMediaPlayer!!.setVolume(volume, volume)
        wMediaPlayer!!.start()
    }
}