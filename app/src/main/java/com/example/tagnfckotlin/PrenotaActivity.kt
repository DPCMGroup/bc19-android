package com.example.tagnfckotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class PrenotaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prenota)
        val cerca = findViewById<Button>(R.id.cerca)
        cerca.setOnClickListener {
            var moveIntent = Intent(
                this, ListaPostazioniActivity::class.java)
            startActivity(moveIntent)
        }
    }
}