package com.example.tagnfckotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import org.json.JSONObject

class VisualizzaActivity : AppCompatActivity() {

    val url_json = "http://192.168.210.35:8000/"

    private val client = HttpClient(url_json)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizza)
        val num = intent.getStringExtra("num")
        println(num)
        for (i in 0 until num.toInt()+1){
            val bookId = intent.getStringExtra("bookId"+i)
            val workName = intent.getStringExtra("workName"+i)
            val roomName = intent.getStringExtra("roomName"+i)
            val start = intent.getStringExtra("start"+i)
            val end = intent.getStringExtra("end"+i)
            println(end)
            println(start)
            println(roomName)
            println(bookId)
            println(workName)

        }

    }
}