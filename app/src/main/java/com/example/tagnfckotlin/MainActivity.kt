package com.example.tagnfckotlin


import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tagnfckotlin.parser.NdefMessageParser
import com.example.tagnfckotlin.record.ParsedNdefRecord
import com.example.tagnfckotlin.ui.login.LoginActivity
import org.checkerframework.checker.units.qual.C
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList
import kotlin.experimental.and


class MainActivity : AppCompatActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var text: TextView? = null

    var workstationList: MutableList<WorkstationModelClass>? = null
    var recyclerView: RecyclerView? = null

    //val url = "http://192.168.210.35:8000/workstation/"

    //ho utilizzato questo url per semplicità di test
    val url_json="https://run.mocky.io/v3/c3b3bc71-d601-4c19-bd6d-fc98b45d3057"


    private val client = HttpClient(url_json)

    override fun onCreate(savedInstanceState: Bundle?) {

        //non bisognerebbe usare lo stesso thread dell'applicazione per fare chiamate internet.
        //Però io ho bisogno di farlo lo stesso, quindi setto la policy in modo che non lo rilevi
        //val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        //StrictMode.setThreadPolicy(policy)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
                val jsonArray = jsonObject.getJSONArray("workstations")

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
                    model.WorkstationId = jsonObject1.getString("WorkstationId")
                    model.Status = jsonObject1.getString("Status")
                    model.BookedBy = jsonObject1.getString("BookedBy")

                    //if(model.WorkstationId== "00 37 00 03 95 70 04")
                    //if(model.WorkstationId== "6")
                    if(model.WorkstationId == tagId.text){
                        if(model.Status == "libera e non igienizzata" || model.Status == "guasta e non igienizzata") {
                            // Rende visibile bottone
                            igienizza.setVisibility(View.VISIBLE)
                        }
                        if(model.Status == "non accessibile" || model.Status == "guasta e igienizzata") {
                            // Rende visibile text view (messaggio: postazione non accessibile)
                            message.setVisibility(View.VISIBLE)
                        }
                        if(model.Status == "libera e igienizzata") {
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

        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_prenota -> {
                var moveIntent =Intent(this, PrenotaActivity::class.java)
                startActivity(moveIntent)
                return true
            }
            R.id.nav_guida -> {
                var moveIntent =Intent(this, GuidaActivity::class.java)
                startActivity(moveIntent)
                return true
            }
            R.id.nav_vis -> {
                var moveIntent =Intent(this, VisualizzaActivity::class.java)
                startActivity(moveIntent)
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

        //scorro la lista di workstations dopo aver effettuato la scansione del tag
        val getData = GetData()
        getData.execute()



        /*
        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n')
        sb.append("ID (dec): ").append(toDec(id)).append('\n')
        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n')
         */



         // Rende visibile intro stato postazione
         val intro =  findViewById<TextView>(R.id.intro_txt)
        intro.setVisibility(View.VISIBLE)

       // Rende visibile recycler view
        val layout =  findViewById<RecyclerView>(R.id.recyclerView)
        layout.setVisibility(View.VISIBLE)


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
