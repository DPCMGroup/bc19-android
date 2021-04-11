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

    }
}