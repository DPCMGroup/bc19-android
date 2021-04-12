package com.example.tagnfckotlin


import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tagnfckotlin.parser.NdefMessageParser
import com.example.tagnfckotlin.record.ParsedNdefRecord
import com.example.tagnfckotlin.ui.login.LoginActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList


class MainActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var text: TextView? = null




    var workstationList: MutableList<WorkstationModelClass>? = null
    var recyclerView: RecyclerView? = null

    val url_json = "http://192.168.210.35:8000/"

    //ho utilizzato questo url per semplicità di test
   // val url_json="https://run.mocky.io/v3/9c30b61f-fa6d-41bf-80f1-a6ffc5274f05"


    private val client = HttpClient(url_json)


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        //non bisognerebbe usare lo stesso thread dell'applicazione per fare chiamate internet.
        //Però io ho bisogno di farlo lo stesso, quindi setto la policy in modo che non lo rilevi
        //val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        //StrictMode.setThreadPolicy(policy)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val idutente = intent.getStringExtra("id")
        val username = intent.getStringExtra("username")
        runOnUiThread {
            Toast.makeText(applicationContext, "Benvenuto " + username+"!", Toast.LENGTH_SHORT).show()
        }
        val IdUt= findViewById<TextView>(R.id.IdUt_txt)
        IdUt.text= idutente
        println(idutente)
        val igienizza = findViewById<Button>(R.id.igienizza)
        val message = findViewById<TextView>(R.id.message_txt)
        val message1 = findViewById<TextView>(R.id.message1_txt)
        val stato = findViewById<TextView>(R.id.stato)
        val tagId= findViewById<TextView>(R.id.tagId_txt)
        runOnUiThread {
            igienizza.isEnabled = false
        }


        igienizza.setOnClickListener {
            val json : JSONObject = createJsonObjactIgienizza(tagId, idutente)
            client.visIgienizza(json, this::manageOutputIgienizza)

        }

       text = findViewById<View>(R.id.text) as TextView


        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        pendingIntent = PendingIntent.getActivity(this, 0,
                Intent(this, this.javaClass)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)


        workstationList = ArrayList()
        recyclerView = findViewById(R.id.recyclerView)
        val getData = GetData()
        getData.execute()
    }




    inner class GetData : AsyncTask<String?, String?, String>() {
        override fun doInBackground(vararg p0: String?): String? {
            var current = ""
            try {
                val url: URL
                var urlConnection: HttpURLConnection? = null
                try {
                    url = URL(url_json)

                    urlConnection = url.openConnection() as HttpURLConnection
                    val `is` = urlConnection.inputStream
                    val isr = InputStreamReader(`is`)
                    var data = isr.read()
                    while (data != -1) {
                        current += data.toChar()
                        data = isr.read()
                    }
                    return current
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    urlConnection?.disconnect()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return current
        }

        override fun onPostExecute(s: String) {
            try {
                val jsonObject = JSONObject(s)
                val jsonArray = jsonObject.getJSONArray(null)

                val tagId= findViewById<TextView>(R.id.tagId_txt)
                val igienizza = findViewById<Button>(R.id.igienizza)
                val message =  findViewById<TextView>(R.id.message_txt)
                val message1 =  findViewById<TextView>(R.id.message1_txt)
                igienizza.setVisibility(View.INVISIBLE)
                message.setVisibility(View.INVISIBLE)
                message1.setVisibility(View.INVISIBLE)

                //elimino le postazioni per effettuare nuove scansioni e avere la lista di nuovo vuota
                for (i in 0 until workstationList!!.size)
                    workstationList!!.removeAt(i)

                for (i in 0 until jsonArray.length()) {
                    val jsonObject1 = jsonArray.getJSONObject(i)
                    val model = WorkstationModelClass()
                    model.tag = jsonObject1.getString("tag")
                    model.state = jsonObject1.getInt("state")
                    //model.BookedBy = jsonObject1.getString("BookedBy")

                    //if(model.WorkstationId== "00 37 00 03 95 70 04")
                    //if(model.WorkstationId== "6")
                    if(model.tag == tagId.text){
                        if(model.state == 1 || model.state == 6) {
                            // Rende visibile bottone
                            igienizza.setVisibility(View.VISIBLE)
                        }
                        if(model.state == 3 || model.state == 5) {
                            // Rende visibile text view (messaggio: postazione non accessibile)
                            message.setVisibility(View.VISIBLE)
                        }
                        if(model.state == 0) {
                            // Rende visibile text view (messaggio: prenota postazione dopo un minuto)
                            message1.setVisibility(View.VISIBLE)
                        }

                        //da aggiungere controllo con BookedBy

                    workstationList!!.add(model)
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            PutDataIntoRecyclerView(workstationList)
        }
    }

    private fun PutDataIntoRecyclerView(workstationList:List<WorkstationModelClass>?) {
        val adaptery = Adaptery(this, workstationList!!)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = adaptery
    }













    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val idutente = intent.getStringExtra("id")
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

     override  fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {


            R.id.nav_prenota -> {
                val IdUt= findViewById<TextView>(R.id.IdUt_txt)

                println(IdUt.text)
                println(IdUt.text.toString())
                var moveIntent =Intent(this, PrenotaActivity::class.java)
                moveIntent.putExtra("id", IdUt.text.toString())
                startActivity(moveIntent)
                return true
            }
            R.id.nav_guida -> {

                var moveIntent =Intent(this, GuidaActivity::class.java)
                startActivity(moveIntent)
                return true
            }
            R.id.nav_vis -> {
                val IdUt= findViewById<TextView>(R.id.IdUt_txt)
                  //  val idutente = intent.getStringExtra("id")


                    val json: JSONObject = createJsonObjact()

                client.visprenotazioni(json, this::manageOutputvis, IdUt.text.toString())
                return true
            }
            R.id.logout -> {
                var moveIntent =Intent(this, LoginActivity::class.java)
                startActivity(moveIntent)
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }



    private fun createJsonObjact(): JSONObject {
        val Settings = JSONObject()
        val idutente = intent.getStringExtra("id")
        Settings.put("id", idutente)
        return Settings
    }

    fun manageOutputvis(s: String){
    println(s)
        val idutente = intent.getStringExtra("id")
        println(idutente)
        val conversione = s.replace("\\\"","'").replace("\"", "").replace("'","\"")
        val s2= "{ \"visualizzaPrenotazioni\" : "+ conversione +"}"
/*
        if (s == "\"[]\"") {

            val num: Int = -1
            var intent =Intent(this, VisualizzaActivity::class.java)
            intent.putExtra("numeroit", num.toString())
            startActivity(intent)
            //creare textView con scritto "Nessuna prenotazione effetuata"
            }

        else {
            try {

                val oggettojson=JSONObject(s2)

                val userList = ArrayList<HashMap<String, String?>>()
                val jsonarray = oggettojson.getJSONArray("visualizzaPrenotazioni")
                var finale: String = "{ ["
                for (i in 0 until jsonarray.length()) {
                    val user = HashMap<String, String?>()
                    val obj = jsonarray.getJSONObject(i)
                    user["bookId"] = obj.getString("bookId")
                    user["workName"] = obj.getString("workName")
                    user["roomName"] = obj.getString("roomName")
                    user["start"] = obj.getString("start")
                    user["end"] = obj.getString("end")
                    userList.add(user)




                   /* val s= intent.putExtra("bookId"+ i, user["bookId"])
                    val d = intent.putExtra("workName"+i, user["workName"])
                    val f = intent.putExtra("roomName"+i, user["roomName"])
                    val e= intent.putExtra("start"+i, user["start"])
                    val c= intent.putExtra("end"+i, user["end"])
                    intent.putExtra("numeroit", i.toString())*/


                  finale = finale+"" + user["bookId"].toString()+" "+user["workName"].toString()+" "+user["roomName"].toString()+" "+user["start"].toString()+" "+user["end"].toString()+"         "




                    println(finale)
                    if (i==jsonarray.length()-1) {

                  //  }
                }



            } catch (ex: JSONException) {
                Log.e("JsonParser Example", "unexpected JSON exception", ex)
            }


*/

var intent =Intent(this, VisualizzaActivity::class.java)
val seew= intent.putExtra("json", s2)
startActivity(intent)
            //creare checkbox con "data - orainizio - orafine - postazione - stanza"
                // creare il bottone disdici
            }





    override fun onResume() {
        super.onResume()
        if (nfcAdapter != null) {
            if (!nfcAdapter!!.isEnabled) showWirelessSettings()
            nfcAdapter!!.enableForegroundDispatch(this, pendingIntent, null, null)
        }
    }

    override fun onPause() {
        super.onPause()
        if (nfcAdapter != null) {
            nfcAdapter!!.disableForegroundDispatch(this)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        resolveIntent(intent)
    }

    fun printString(s : String){
        println(s)

    }

    private fun resolveIntent(intent: Intent) {

        /*QUI CHIEDI A SERVER*/

        //client.insertRequest(json, this::printString)
        client.getRequest(this::printString)
        /*QUI CHIEDI A SERVER*/

        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val msgs: Array<NdefMessage?>
            if (rawMsgs != null) {
                msgs = arrayOfNulls(rawMsgs.size)
                for (i in rawMsgs.indices) {
                    msgs[i] = rawMsgs[i] as NdefMessage
                }
            } else {
                val empty = ByteArray(0)
                val id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
                val tag = intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag
                val payload = dumpTagData(tag).toByteArray()
                val record = NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload)
                val msg = NdefMessage(arrayOf(record))
                msgs = arrayOf(msg)

            }
            displayMsgs(msgs)
        }
    }

    private fun displayMsgs(msgs: Array<NdefMessage?>?) {
        if (msgs == null || msgs.size == 0) return
        val builder = StringBuilder()
        val records: List<ParsedNdefRecord> = msgs[0]?.let { NdefMessageParser.parse(it) } as List<ParsedNdefRecord>
        val size = records.size
        for (i in 0 until size) {
            val record = records[i]
            val str = record.str()
            builder.append(str).append("\n")
        }
        text!!.text = builder.toString()
    }

    private fun showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        startActivity(intent)
    }

    private fun dumpTagData(tag: Tag): String {
        val sb = StringBuilder()
        val id = tag.id


        //sb.append("ID (hex): ").append(toHex(id)).append('\n')

        //salvo l'id su textView (tagId_txt, settato invisible)
        val tagId= findViewById<TextView>(R.id.tagId_txt)
        tagId.text= toHex(id)
        val json : JSONObject = createJsonObjectscansione(id)
        client.scansione(json, ::manageOutputscansione)
        //scorro la lista di workstations dopo aver effettuato la scansione del tag
       // val getData = GetData()
       // getData.execute()



        /*
        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n')
        sb.append("ID (dec): ").append(toDec(id)).append('\n')
        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n')
         */



         // Rende visibile intro stato postazione
         val intro =  findViewById<TextView>(R.id.intro_txt)
        runOnUiThread {
            intro.setVisibility(View.VISIBLE)
        }

       // Rende visibile recycler view
        val layout =  findViewById<RecyclerView>(R.id.recyclerView)
        runOnUiThread {
            layout.setVisibility(View.VISIBLE)
        }


/*
        val prefix = "android.nfc.tech."
        sb.append("Tecnologie di scansione: ")
        for (tech in tag.techList) {
            sb.append(tech.substring(prefix.length))
            sb.append(", ")
        }
        sb.delete(sb.length - 2, sb.length)
        for (tech in tag.techList) {
            if (tech == MifareClassic::class.java.name) {
                sb.append('\n')
                var type = "Unknown"
                try {
                    val mifareTag = MifareClassic.get(tag)
                    when (mifareTag.type) {
                        MifareClassic.TYPE_CLASSIC -> type = "Classic"
                        MifareClassic.TYPE_PLUS -> type = "Plus"
                        MifareClassic.TYPE_PRO -> type = "Pro"
                    }
                    sb.append("Mifare Classic type: ")
                    sb.append(type)
                    sb.append('\n')
                    sb.append("Mifare size: ")
                    sb.append(mifareTag.size.toString() + " bytes")
                    sb.append('\n')
                    sb.append("Mifare sectors: ")
                    sb.append(mifareTag.sectorCount)
                    sb.append('\n')
                    sb.append("Mifare blocks: ")
                    sb.append(mifareTag.blockCount)
                } catch (e: Exception) {
                    sb.append("Mifare classic error: " + e.message)
                }
            }
            if (tech == MifareUltralight::class.java.name) {
                sb.append('\n')
                val mifareUlTag = MifareUltralight.get(tag)
                var type = "Unknown"
                when (mifareUlTag.type) {
                    MifareUltralight.TYPE_ULTRALIGHT -> type = "Ultralight"
                    MifareUltralight.TYPE_ULTRALIGHT_C -> type = "Ultralight C"
                }
                sb.append("Mifare Ultralight type: ")
                sb.append(type)
            }
        }

 */
        return sb.toString()
    }

    private fun createJsonObjectscansione(id: ByteArray): JSONObject {
        val Settings = JSONObject()
        val tagId= findViewById<TextView>(R.id.tagId_txt)
        tagId.text= toHex(id)
        Settings.put("tag", tagId.text)

        return Settings
    }

    fun manageOutputscansione(s: String) {

        val idutente = intent.getStringExtra("id")
        val conversioneobj = s.replace("\\\"","'").replace("\"", "").replace("'","\"")
        val json = JSONObject(conversioneobj)

          // String instance holding the above json
            val id =json.getInt ("workId")
            val nomepostazionejson = json.getString("workName")
            val statojson = json.getInt("workStatus")
            val evprenotjson = json.getInt("bookedToday")
                val statoIg=json.getInt("workSanitized")

            val igienizza = findViewById<Button>(R.id.igienizza)
            val message = findViewById<TextView>(R.id.message_txt)
            val message1 = findViewById<TextView>(R.id.message1_txt)
            igienizza.setVisibility(View.INVISIBLE)
            message.setVisibility(View.INVISIBLE)
            message1.setVisibility(View.INVISIBLE)

            val nomepostazione = findViewById<TextView>(R.id.nomepostazione)
            val stato = findViewById<TextView>(R.id.stato)
            val evprenotazioni = findViewById<TextView>(R.id.evprenotazioni)
        runOnUiThread {
            nomepostazione.text = nomepostazionejson
        }







            if (statojson == 0 && statoIg==1) {
                        runOnUiThread {
                            stato.text = "Libera e Igienizzata"
                        }
                        runOnUiThread {
                            message1.setVisibility(View.VISIBLE)
                        }

            } else if (statojson == 0 && statoIg==0) {
                    runOnUiThread {
                        stato.text = "Libera e non Igienizzata"
                    }
                    runOnUiThread {
                        igienizza.isEnabled = true
                    }

                    runOnUiThread {
                        igienizza.setVisibility(View.VISIBLE)
                    }

            } else if (statojson == 1 && statoIg==0) {
                    runOnUiThread {
                        stato.text = "Occupata e non Igienizzata"
                    }
                    runOnUiThread {
                        message.setVisibility(View.VISIBLE)
                    }
                    runOnUiThread {
                        igienizza.isEnabled = true
                    }

                    runOnUiThread {
                        igienizza.setVisibility(View.VISIBLE)
                    }
                }
                else if (statojson == 1 && statoIg==1) {
                        runOnUiThread {
                            stato.text = "Occupata e Igienizzata"
                        }
                        runOnUiThread {
                            message.setVisibility(View.VISIBLE)
                        }
                    }
             else if (statojson == 2 && statoIg==1) {

                runOnUiThread {stato.text = "Prenotata e Igienizzata"}
                runOnUiThread {message.setVisibility(View.VISIBLE)}}
            else if (statojson == 2 && statoIg==0) {
                runOnUiThread {stato.text = "Prenotata e non Igienizzata"}
                runOnUiThread {message.setVisibility(View.VISIBLE)}
                    runOnUiThread {
                        igienizza.isEnabled = true
                    }

                    runOnUiThread {
                        igienizza.setVisibility(View.VISIBLE)
                    }}
             else if (statojson == 3&& statoIg==1) {
                runOnUiThread {stato.text = "Guasta e Igienizzata"}
                runOnUiThread {message.setVisibility(View.VISIBLE)}}
             else if (statojson == 3 && statoIg==0) {
                runOnUiThread {stato.text = "Guasta e non Igienizzata"}
                runOnUiThread {message.setVisibility(View.VISIBLE)}
        runOnUiThread {
            igienizza.isEnabled = true
        }

        runOnUiThread {
            igienizza.setVisibility(View.VISIBLE)
        }}



        runOnUiThread { nomepostazione.setVisibility(View.VISIBLE)}
            runOnUiThread {  stato.setVisibility(View.VISIBLE)}
                runOnUiThread { evprenotazioni.setVisibility(View.VISIBLE)}

        if (evprenotjson == 0) {
            runOnUiThread {
                evprenotazioni.text = "Non ci sono prenotazioni"
            }
        }

        if(evprenotjson==1){

            try {

                val oggettojson=JSONObject(conversioneobj)

                val userList = ArrayList<HashMap<String, String?>>()
                val jsonarray = oggettojson.getJSONArray("bookings")

                for (i in 0 until 1) {
                    val user = HashMap<String, String?>()
                    val obj = jsonarray.getJSONObject(i)
                    user["bookerName"] = obj.getString("bookerName")
                    user["bookerSurname"] = obj.getString("bookerSurname")
                    user["bookerId"] = obj.getString("bookerId")
                    userList.add(user)
                    val IdUt= findViewById<TextView>(R.id.IdUt_txt)
                    if(IdUt.text==user["bookerId"]){
                        runOnUiThread {
                            evprenotazioni.text = "Postazione prenotata da te"}
                            runOnUiThread {message.setVisibility(View.INVISIBLE)}
                            runOnUiThread {message1.setVisibility(View.VISIBLE)}
                            if(jsonarray.length()>1){
                                runOnUiThread {
                                    evprenotazioni.text = "Prenotata da te. Attenzione ci sono altre prenotazioni!"}
                            }

                    }else{
                        runOnUiThread {
                            evprenotazioni.text = "Prenotata da " + user["bookerName"]+" "+user["bookerSurname"]+"!"
                        }
                    }

                }}
            catch (ex: JSONException) {
                Log.e("JsonParser Example", "unexpected JSON exception", ex)
            }}



    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createJsonObjactIgienizza(tagid: TextView, idutente: String?): JSONObject {
        val Settings = JSONObject()
        Settings.put("idUser", idutente)
        Settings.put("tag", tagid.text)
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val formatted = current.format(formatter)

       Settings.put("data",formatted)

        return Settings
    }

    fun manageOutputIgienizza(s: String) {
        val output= s.replace("\"","")
        runOnUiThread {
            Toast.makeText(applicationContext, output, Toast.LENGTH_SHORT).show()
        }
        if(output=="sanitize complete") {
            val igienizza = findViewById<Button>(R.id.igienizza)
            runOnUiThread {
                igienizza.isEnabled = true
            }
            val message = findViewById<TextView>(R.id.message_txt)
            val message1 = findViewById<TextView>(R.id.message1_txt)
            val evprenotazioni = findViewById<TextView>(R.id.evprenotazioni)
            val stato = findViewById<TextView>(R.id.stato)
            if(stato.text=="Libera e non Igienizzata"){
            runOnUiThread {
                stato.text = "Libera e Igienizzata"
            }
                runOnUiThread {
                    message.setVisibility(View.INVISIBLE)
                }
                runOnUiThread {
                    message1.setVisibility(View.VISIBLE)
                }}
            if(stato.text=="Prenotata e non Igienizzata"){
                runOnUiThread {
                    stato.text = "Prenotata e Igienizzata"
                }
            if(evprenotazioni.text=="Prenotata da te. Attenzione ci sono altre prenotazioni!" || evprenotazioni.text=="Postazione prenotata da te"){
                runOnUiThread {
                    message.setVisibility(View.INVISIBLE)
                }
                runOnUiThread {
                    message1.setVisibility(View.VISIBLE)
                }
            }

            }
            if(stato.text=="Guasta e non Igienizzata"){
                runOnUiThread {
                    stato.text = "Guasta e Igienizzata"
                }}

            runOnUiThread {
                igienizza.setVisibility(View.INVISIBLE)
            }
            runOnUiThread {
                igienizza.isEnabled=false
            }

        }

    }

    private fun toHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices.reversed()) {
            val b: Int = bytes[i].toInt() and 0xff
            if (b < 0x10) sb.append('0')
            sb.append(Integer.toHexString(b))
            if (i > 0) {
                sb.append(" ")
            }
        }
        return sb.toString()
    }

/*
    private fun toReversedHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices) {
            if (i > 0) {
                sb.append(" ")
            }
            val b: Int = bytes[i].toInt() and 0xff
            if (b < 0x10) sb.append('0')
            sb.append(Integer.toHexString(b))
        }
        return sb.toString()
    }

    private fun toDec(bytes: ByteArray): Long {
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices) {
            val value: Byte = bytes[i] and 0xffL.toByte()
            result += value * factor
            factor *= 256L
        }
        return result
    }

    private fun toReversedDec(bytes: ByteArray): Long {
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices.reversed()) {
            val value: Byte = bytes[i] and 0xffL.toByte()
            result += value * factor
            factor *= 256L
        }
        return result
    }
*/

}
