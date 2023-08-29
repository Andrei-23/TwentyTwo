package com.example.twentytwo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView

class WinActivity : AppCompatActivity() {

//    private var viewtime : TextView? = null
//    private var viewturns : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_win)
//
//        var time: Int = 0
//        var turns: Int = 0
//        intent.getIntExtra("Time", time)
//        intent.getIntExtra("Turns", turns)
//
//        viewtime = findViewById<TextView>(R.id.win_time_text)
//        viewturns = findViewById<TextView>(R.id.win_turns_text)
//
//        val timestr = "Time: " + timeToString(time)
//        val turnsstr = "Turns: $turns"
//
//        viewtime?.setText(timestr)
//        viewturns?.setText(turnsstr)
    }


    private fun timeToString(solveTime: Int): String {
        val ms = (solveTime % 1000) / 100
        val sec = (solveTime / 1000) % 60
        val min = (solveTime / 60000) % 60
        return "$min:${if(sec < 10) "0" else ""}$sec.$ms"
    }

    fun onClick(view: View) {
        when(view.id){
            R.id.win_btn_restart -> {
                val intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.win_btn_menu -> {
                val intent = Intent(this, MainActivity::class.java)
                finish()
                startActivity(intent)
            }
        }

    }
}