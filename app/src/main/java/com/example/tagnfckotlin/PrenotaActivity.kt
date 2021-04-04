package com.example.tagnfckotlin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class PrenotaActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prenota)
        val cerca = findViewById<Button>(R.id.cerca)
        cerca.setOnClickListener {
            var moveIntent = Intent(
                    this, ListaPostazioniActivity::class.java)
            startActivity(moveIntent)
        }
        val SelezionaData = findViewById<ImageButton>(R.id.SelezionaData)
        val dataTesto = findViewById<TextView>(R.id.dataTesto)

        val now = Calendar.getInstance()
        var day = now.get(Calendar.DAY_OF_MONTH)
        var month = now.get(Calendar.MONTH)
        var year = now.get(Calendar.YEAR)

        SelezionaData.setOnClickListener {

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                dataTesto.setText("" + dayOfMonth + "/" + month + "/" + year)

            }, year, month, day)

            dpd.show()
        }

        val SelezionaInizio = findViewById<ImageButton>(R.id.SelezionaInizio)
        val inizioTesto     = findViewById<TextView>(R.id.inizioTesto)

        SelezionaInizio.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                inizioTesto.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        val SelezionaFine = findViewById<ImageButton>(R.id.SelezionaFine)
        val fineTesto     = findViewById<TextView>(R.id.fineTesto)

        SelezionaFine.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                fineTesto.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
    }

}