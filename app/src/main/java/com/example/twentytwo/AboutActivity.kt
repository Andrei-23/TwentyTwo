package com.example.twentytwo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class AboutActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var pref : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        pref = getSharedPreferences("TABLE",  Context.MODE_PRIVATE)
    }

    fun onClick(view: View) {

        if(mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.button_click)
            mediaPlayer!!.isLooping = false
            val volume = if(pref!!.getBoolean("sound", true)) 0.2F else 0F
            mediaPlayer!!.setVolume(volume, volume)
        }
        if(mediaPlayer != null && mediaPlayer!!.isPlaying){
            mediaPlayer!!.pause()
        }
        mediaPlayer!!.seekTo(0)
        mediaPlayer!!.start()

        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(this, R.raw.btn_click)
            mediaPlayer!!.isLooping = false
            mediaPlayer!!.setVolume(0.2F, 0.2F)
        }
        mediaPlayer!!.start()
        if(view.id == R.id.about_btn_back){
            val intent = Intent(this, SetActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, SetActivity::class.java)
        startActivity(intent)
        finish()
    }

}