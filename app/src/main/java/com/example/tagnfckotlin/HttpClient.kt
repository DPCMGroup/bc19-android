package com.example.tagnfckotlin

import okhttp3.*
import java.io.IOException
import kotlin.jvm.Throws

class HttpClient(val url: String){
    private val client = OkHttpClient()



    fun getRequest(then : (String) -> Unit){
        val request = Request.Builder()
                .url(url + "list")
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {println(e)}

            override fun onResponse(call: Call, response: Response) : Unit = then(response.body()!!.string())
        })
        println("fine getRequest")
    }


    @Throws(IOException::class)
    fun getRequest2(): String? {
        val request: Request = Request.Builder()
                .url(url + "list")
                .build()
        client.newCall(request).execute().use { response -> return response.body()!!.string() }
    }

    private fun deleteRequest(id : String){
        val request = Request.Builder()
                .url(url+id ) //aggiungo in fondo all'url l'id della postazione da eliminare, perché l'API accetta questo formato
                .delete()
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {println("failure")}
            //scrivo il risultato sul responseTextView
            override fun onResponse(call: Call, response: Response) = println(response.body()!!.string())
        })
    }

}