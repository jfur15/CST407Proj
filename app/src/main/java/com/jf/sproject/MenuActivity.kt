package com.jf.sproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

private fun getID(spinner: Spinner, value: Int): Int{
    if(value != null){
        for (i in 0 until spinner.getCount()) {
            if (spinner.getItemAtPosition(i) == value) {
                return i
            }
        }
    }
    return -1
}

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val sp_numwords: Spinner = findViewById(R.id.spinner_wordcount)
        if (sp_numwords != null) {
            val adapter = ArrayAdapter(this,
                R.layout.spinner_layout_wordcount, (1..3).toList())
            sp_numwords.adapter = adapter
        }

        val sp_speed: Spinner = findViewById(R.id.spinner_speed)
        if (sp_speed != null) {
            val adapter = ArrayAdapter(this,
                R.layout.spinner_layout_wordcount, arrayOf("No timer", 7,5,3))
            sp_speed.adapter = adapter
        }

        var pref: SharedPreferences =
            applicationContext.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE)

        val prefSpeed  = pref.getInt("SPEED", -1)
        var speedID = getID(sp_speed, prefSpeed)

        if (speedID != -1){
            sp_speed.setSelection(speedID)
        }

        val prefWords  = pref.getInt("WORD_COUNT", -1)
        var wordID = getID(sp_numwords, prefWords)

        if (wordID != -1){
            sp_numwords.setSelection(wordID)
        }


        val btn_go = findViewById(R.id.button_go) as Button
        btn_go.setOnClickListener {
            if (sp_speed.selectedItem is Int){
            pref.edit().putInt("SPEED", sp_speed.selectedItem as Int)}

            if (sp_numwords.selectedItem is Int){
            pref.edit().putInt("WORD_COUNT", sp_numwords.selectedItem as Int)}

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        //val intent = Intent(this, MainActivity::class.java)
        //startActivity(intent)
    }
}