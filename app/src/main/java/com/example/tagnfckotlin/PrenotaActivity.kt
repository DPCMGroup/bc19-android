package com.example.tagnfckotlin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
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

        val idutente = intent.getStringExtra("id")

        cerca.setOnClickListener {

            val json: JSONObject = createJsonObjact()
            client.cerca(json, this::manageOutput)
            var moveIntent = Intent(this, ListaPostazioniActivity::class.java)
            moveIntent.putExtra("id", idutente.toString())
            startActivity(moveIntent)

        }
        val SelezionaData = findViewById<ImageButton>(R.id.SelezionaData)
        val dataTesto = findViewById<TextView>(R.id.dataTesto)

        val now = getInstance()
        var day = now.get(DAY_OF_MONTH)
        var month = now.get(Calendar.MONTH)
        var year = now.get(YEAR)

        SelezionaData.setOnClickListener {

            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val monthOfYear = monthOfYear + 1
                    // Display Selected date in textbox
                    dataTesto.setText("" + dayOfMonth + "/" + monthOfYear + "/" + year)

                },
                year,
                month,
                day
            )

            dpd.show()
        }

        val SelezionaInizio = findViewById<ImageButton>(R.id.SelezionaInizio)
        val inizioTesto = findViewById<TextView>(R.id.inizioTesto)

        SelezionaInizio.setOnClickListener {
            val cal = getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(HOUR_OF_DAY, hour)
                cal.set(MINUTE, minute)
                inizioTesto.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(HOUR_OF_DAY),
                cal.get(MINUTE),
                true
            ).show()
        }

        val SelezionaFine = findViewById<ImageButton>(R.id.SelezionaFine)
        val fineTesto = findViewById<TextView>(R.id.fineTesto)

        SelezionaFine.setOnClickListener {
            val cal = getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(HOUR_OF_DAY, hour)
                cal.set(MINUTE, minute)
                fineTesto.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(HOUR_OF_DAY),
                cal.get(MINUTE),
                true
            ).show()
        }
    }

    private fun createJsonObjact(): JSONObject {
        val Settings = JSONObject()
        val inizioTesto = findViewById<EditText>(R.id.inizioTesto)
        val fineTesto = findViewById<EditText>(R.id.fineTesto)
        val dataTesto = findViewById<EditText>(R.id.dataTesto)
        val stanzaTesto = findViewById<EditText>(R.id.stanzaTesto)
        val dipTesto = findViewById<EditText>(R.id.dipTesto)
        Settings.put("orainizio", inizioTesto.text)
        Settings.put("orafine", fineTesto.text)
        Settings.put("data", dataTesto.text)
        Settings.put("stanza", stanzaTesto.text)
        Settings.put("collega", dipTesto.text)
        println("jsonprenota")
        println(Settings)
// Convert JsonObject to String Format
// Convert JsonObject to String Format

//saveJson(Settings.toString())


        return Settings

    }

    fun manageOutput(s: String) {


        println(s)
        /*
        val idutente = intent.getStringExtra("id")
        val conversione = s.replace("\\\"", "'").replace("\"", "").replace("'", "\"")
        val s2 = "{ \"visualizzaPostazioni\" : " + conversione + "}"

        if (s == "\"[]\"") {
            ""
            //creare textView con scritto "Nessuna prenotazione effetuata"
        } else {
            try {
                val lv = findViewById<ListView>(R.id.view_bookings)
                val oggettojson = JSONObject(s2)
                val userList = ArrayList<HashMap<String, String?>>()
                val jsonarray = oggettojson.getJSONArray("visualizzaPostazioni")
                for (i in 0 until jsonarray.length()) {
                    val user = HashMap<String, String?>()
                    val obj = jsonarray.getJSONObject(i)
                    user["bookId"] = obj.getString("bookId")
                    user["workName"] = obj.getString("workName")
                    user["roomName"] = obj.getString("roomName")
                    user["start"] = obj.getString("start")
                    user["end"] = obj.getString("end")
                    userList.add(user)
                }
                val adapter: ListAdapter = SimpleAdapter(
                    this, userList, R.layout.list_bookings,
                    arrayOf("bookId", "workName", "roomName", "start", "end"), intArrayOf(
                        R.id.bookId,
                        R.id.workName,
                        R.id.roomName,
                        R.id.start,
                        R.id.end
                    )
                )

                lv.adapter = adapter
                println(lv.adapter)
            } catch (ex: JSONException) {
                Log.e("JsonParser Example", "unexpected JSON exception", ex)
            }

            var moveIntent = Intent(this, ListaPostazioniActivity::class.java)
            moveIntent.putExtra("id", idutente.toString())
            startActivity(moveIntent)
}
*/

}

}