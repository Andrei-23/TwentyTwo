package com.example.twentytwo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat

class SetActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var pref : SharedPreferences? = null
//    private var onCompletionListener = OnCompletionListener {releaseMediaPlayerResources()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set)

        pref = getSharedPreferences("TABLE",  Context.MODE_PRIVATE)
        val editor = pref?.edit()

        val timerOn = pref?.getBoolean("timer", true)!!
        val timerSwitch = findViewById<SwitchCompat>(R.id.timer_switch)
        timerSwitch.isChecked = timerOn
        timerSwitch.setOnCheckedChangeListener{ _, isChecked ->
            playSoundButton()
            editor?.putBoolean("timer", isChecked)
            editor?.apply()
        }

        val soundOn = pref?.getBoolean("sound", true)!!
        val soundSwitch = findViewById<SwitchCompat>(R.id.sound_switch)
        soundSwitch.isChecked = soundOn
        soundSwitch.setOnCheckedChangeListener{ _, isChecked ->
            playSoundButton()
            editor?.putBoolean("sound", isChecked)
            editor?.apply()
        }
    }

//    private fun releaseMediaPlayerResources() {
//        if(mediaPlayer != null){
//            mediaPlayer!!.stop()
//            mediaPlayer!!.release()
//            Log.w("sound","Sound finished")
//        }
//    }

    private fun playSoundButton(){

        if(mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.button_click)
            mediaPlayer!!.isLooping = false
        }
        if(mediaPlayer != null && mediaPlayer!!.isPlaying){
            mediaPlayer!!.pause()
        }
        val volume = if(pref!!.getBoolean("sound", true)) 0.2F else 0F
        mediaPlayer!!.setVolume(volume, volume)
        mediaPlayer!!.seekTo(0)
        mediaPlayer!!.start()
//        mediaPlayer!!.setOnCompletionListener(onCompletionListener)
    }

    fun onClick(view: View) {

        playSoundButton()

        when(view.id){
            R.id.set_btn_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.set_btn_reset -> {
                var pref : SharedPreferences? = null
                pref = getSharedPreferences("TABLE",  Context.MODE_PRIVATE)
                val editor = pref.edit()
                editor?.remove("bestTime")
                editor?.remove("bestMove")
                editor?.apply()
                Toast.makeText(this, "Best time cleared", Toast.LENGTH_SHORT).show()
            }
            R.id.set_btn_back -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
//        Toast.makeText(this, "Back to main", Toast.LENGTH_SHORT).show()
        backToMain()
    }

    private fun backToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}