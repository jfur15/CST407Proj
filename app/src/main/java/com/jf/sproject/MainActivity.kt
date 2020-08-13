package com.jf.sproject

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.*
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.StringReader


@Entity
data class entry(
    @ColumnInfo(name = "reading") val reading: String?,
    @ColumnInfo(name = "gloss") val gloss: String?,
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "kanji") val kanji: String?,
    @ColumnInfo(name = "position") val position: String?

)



@Dao
interface WordDao {
    @Query("SELECT * FROM entry")
    fun getAll(): List<entry>

    @Query("SELECT * FROM entry WHERE position NOT LIKE 'unclassified' ORDER BY RANDOM() LIMIT 1")
    fun getRandomWord(): entry
}


@Database(entities = arrayOf(entry::class), version = 1)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    companion object {
        @Volatile
        private var INSTANCE: WordDatabase? = null

        fun getDatabase(context: Context): WordDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, WordDatabase::class.java,"jdict")
                    .createFromAsset("jdict.db")
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

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
    lateinit var db: WordDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        chars = parseCharacterJson(this.applicationContext)
        tv_answer = findViewById(R.id.textView_answer)
        tv_challengeWord = findViewById(R.id.textView_challengeWord)


        db = WordDatabase.getDatabase(applicationContext)
        AsyncTask.execute {
            correct_answer = newWordDX()
        }




        tv_answer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.toString() == correct_answer){
                    tv_answer.setText("")
                    AsyncTask.execute {
                        correct_answer = newWordDX()
                    }
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

    fun newWordDX(): String{
        val challengeWord = db.wordDao().getRandomWord().split(',').get(0)

        tv_challengeWord.text = challengeWord

        //todo: convert to to romaji
        Log.e("hi:", "answer: ${ challengeWord}")
        var challengeWordEnglish=""
        for(i in challengeWord.indices){
            var character = challengeWord[i]
            var character2 = ""
            if (i < challengeWord.indices.last){
                character2=challengeWord.subSequence(i..i+1).toString()
            }

            Log.e("hi:", "sing: ${ character}")
            Log.e("hi:", "doub: ${ character2}")
            var singles = chars?.filter{it.Hiragana.first() == character || it.Katakana.first() == character}
            var doubles = chars?.filter{it.Hiragana == character2 || it.Katakana == character2}

            Log.e("hi:", "sing list: ${ singles}")
            Log.e("hi:", "doub list: ${ doubles}")
            if (!doubles.isNullOrEmpty()){
                challengeWordEnglish = challengeWordEnglish+doubles.first().Romaji
            }
            else if(!singles.isNullOrEmpty()){
                challengeWordEnglish = challengeWordEnglish+singles.first().Romaji
            }
        }
        Log.e("hi:", "answer: ${ challengeWordEnglish}")
        return challengeWordEnglish
    }
}