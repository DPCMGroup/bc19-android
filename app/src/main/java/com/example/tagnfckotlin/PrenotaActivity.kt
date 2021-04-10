package com.example.tagnfckotlin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

val url_json = "http://192.168.177.15:8000/"

//ho utilizzato questo url per semplicità di test
// val url_json="https://run.mocky.io/v3/9c30b61f-fa6d-41bf-80f1-a6ffc5274f05"


private val client = HttpClient(url_json)

class PrenotaActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prenota)
        val cerca = findViewById<Button>(R.id.cerca)
        cerca.setOnClickListener {

            val json : JSONObject = createJsonObjact()
            client.cerca(json,this::manageOutput)
            var moveIntent = Intent(
                this, ListaPostazioniActivity::class.java)
            startActivity(moveIntent)
        }
        val SelezionaData = findViewById<ImageButton>(R.id.SelezionaData)
        val dataTesto = findViewById<TextView>(R.id.dataTesto)

        val now = getInstance()
        var day = now.get(DAY_OF_MONTH)
        var month = now.get(Calendar.MONTH)
        var year = now.get(YEAR)

        SelezionaData.setOnClickListener {

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val monthOfYear = monthOfYear +1
                // Display Selected date in textbox
                dataTesto.setText("" + dayOfMonth + "/" + monthOfYear + "/" + year)

            }, year, month, day)

            dpd.show()
        }

        val SelezionaInizio = findViewById<ImageButton>(R.id.SelezionaInizio)
        val inizioTesto     = findViewById<TextView>(R.id.inizioTesto)

        SelezionaInizio.setOnClickListener {
            val cal = getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(HOUR_OF_DAY, hour)
                cal.set(MINUTE, minute)
                inizioTesto.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(this, timeSetListener, cal.get(HOUR_OF_DAY), cal.get(MINUTE), true).show()
        }

        val SelezionaFine = findViewById<ImageButton>(R.id.SelezionaFine)
        val fineTesto     = findViewById<TextView>(R.id.fineTesto)

        SelezionaFine.setOnClickListener {
            val cal = getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(HOUR_OF_DAY, hour)
                cal.set(MINUTE, minute)
                fineTesto.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(this, timeSetListener, cal.get(HOUR_OF_DAY), cal.get(MINUTE), true).show()
        }
    }

    private fun createJsonObjact(): JSONObject {
        val Settings = JSONObject()
        val inizioTesto = findViewById<EditText>(R.id.inizioTesto)
        val fineTesto = findViewById<EditText>(R.id.fineTesto)
        val dataTesto = findViewById<EditText>(R.id.dataTesto)
        val stanzaTesto = findViewById<EditText>(R.id.stanzaTesto)
        val dipTesto = findViewById<EditText>(R.id.dipTesto)
        Settings.put("orainizio", inizioTesto)
        Settings.put("orafine", fineTesto)
        Settings.put("data", dataTesto)
        Settings.put("stanza", stanzaTesto)
        Settings.put("collega", dipTesto)
        println("jsonprenota")
        println(Settings)
// Convert JsonObject to String Format
// Convert JsonObject to String Format

//saveJson(Settings.toString())


        return Settings

    }
    fun manageOutput(s: String){
       /* println("ciao")
        if (s == "\"No user found\"") {
            var moveIntent =Intent(this, VisualizzaActivity::class.java)
            startActivity(moveIntent)
            //creare textView con scritto "Nessuna prenotazione effetuata"
            println("niente")
        }


        else {
            var moveIntent =Intent(this, VisualizzaActivity::class.java)
            startActivity(moveIntent)
            //creare checkbox con "data - orainizio - orafine - postazione - stanza"
            // creare il bottone disdici
            println("ok")

        }*/

    }

}