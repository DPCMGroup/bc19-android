package com.example.tagnfckotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import org.json.JSONException
import org.json.JSONObject

class VisualizzaActivity : AppCompatActivity() {

    val url_json = "http://192.168.210.35:8000/"

    private val client = HttpClient(url_json)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizza)
        val json = intent.getStringExtra("json")
        val jsonObject = JSONObject(json)
if(json!= "{ \"visualizzaPrenotazioni\" : []}"){



    val jsonArray = jsonObject.getJSONArray("visualizzaPrenotazioni")
        println(jsonArray.toString())
        //elimino le postazioni per effettuare nuove scansioni e avere la lista di nuovo vuota
        //  for (i in 0 until workstationList!!.size)
        //    workstationList!!.removeAt(i)
        var listView = findViewById<ListView>(R.id.prenotalist)
        var list = mutableListOf<Model>()
        try {
            for (i in 0 until jsonArray.length()) {
                val jsonObject1 = jsonArray.getJSONObject(i)
                val listafin = list.add(
                    Model(
                        jsonObject1.getInt("bookId")
                            .toString() + " - " + jsonObject1.getString("workName") + " - " + jsonObject1.getString(
                            "roomName"
                        ) + " - " + jsonObject1.getString("start") + " - " + jsonObject1.getString("end")
                    )
                )


            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }


        listView.adapter = myAdapter(this, R.layout.row, list)
    }
        else{

            val notprenota = findViewById<TextView>(R.id.notprenota)


                notprenota.setVisibility(View.VISIBLE)

            //mettere textview con Non ci sono prenotazioni
        }
}
    }


