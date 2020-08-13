package com.jf.sproject

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.Klaxon
import java.io.BufferedReader
import java.io.File
import java.io.StringReader

class JCharacter(val Hiragana:String, val Katakana:String, val Romaji:String)

fun createJWord(chars: List<JCharacter>?, length: Int): JCharacter{
    var hira = ""
    var kata = ""
    var roma = ""

    for (i in 0..length){
        val jc = chars?.shuffled()?.take(1)?.get(0)
        if (jc != null) {
            hira = hira + jc.Hiragana
            kata = kata + jc.Katakana
            roma = roma + jc.Romaji
        }
    }

    return JCharacter(hira,kata,roma)
}


fun getRandomCharacter(chars: List<JCharacter>?):JCharacter?{
    return chars?.shuffled()?.take(1)?.get(0)
}

fun parseCharacterJson(context: Context): List<JCharacter>? {
    val x = context.assets.open("kana.json").bufferedReader().use(BufferedReader::readText)
    //Log.e("hi", "val: ${x}")

    return Klaxon().parseArray<JCharacter>(StringReader(x))
}

class MainActivity : AppCompatActivity() {
    var correct_answer: String = ""

    private lateinit var tv_answer: EditText
    private lateinit var tv_challengeWord: TextView
    var chars: List<JCharacter>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chars = parseCharacterJson(this.applicationContext)
        tv_answer = findViewById(R.id.textView_answer)
        tv_challengeWord = findViewById(R.id.textView_challengeWord)

        correct_answer = newWord()


        tv_answer.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                if (s.toString() == correct_answer){
                    correct_answer = newWord()
                    tv_answer.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })


    }


    fun newWord(): String{



        val jc_challengeWord = createJWord(chars, (2..4).random())

        tv_challengeWord.text = jc_challengeWord.Hiragana
        Log.e("hi:", "answer: ${ jc_challengeWord.Romaji}")
        return jc_challengeWord.Romaji


    }
}