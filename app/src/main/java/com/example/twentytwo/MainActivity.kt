package com.example.twentytwo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    var pref : SharedPreferences? = null
    private var mediaPlayer: MediaPlayer? = null

    fun timeToString(solveTime: Long): String {
        val ms = (solveTime / 100) % 10
        val sec = (solveTime / 1000) % 60
        val min = (solveTime / 60000)
        return "$min:${if(sec < 10) "0" else ""}$sec.$ms"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        // no darkmode
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pref = getSharedPreferences("TABLE",  Context.MODE_PRIVATE)
        val bestTime = pref?.getLong("bestTime", -1)!!
        val bestMove = pref?.getLong("bestMove", -1)!!

        if(bestTime != -1L) {
            val textBestTime = findViewById<TextView>(R.id.main_text_best_time)
            textBestTime.text = "Best time: " + timeToString(bestTime)
        }
        if(bestMove != -1L) {
            val textBestMove = findViewById<TextView>(R.id.main_text_best_move)
            textBestMove.text = "Min moves: " + bestMove
        }
    }

    fun onClick(view: View) {
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(this, R.raw.button_click)
            mediaPlayer!!.isLooping = false
            val volume = if(pref!!.getBoolean("sound", true)) 0.2F else 0F
            mediaPlayer!!.setVolume(volume, volume)
        }
        if(mediaPlayer != null && mediaPlayer!!.isPlaying){
            mediaPlayer!!.pause()
        }
        mediaPlayer!!.start()

        when(view.id){
            R.id.main_btn_start -> {
                val intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.main_btn_set -> {
                val intent = Intent(this, SetActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.main_btn_exit -> finish()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
//        Toast.makeText(this, "Back to freedom", Toast.LENGTH_SHORT).show()
        finish()
    }
}