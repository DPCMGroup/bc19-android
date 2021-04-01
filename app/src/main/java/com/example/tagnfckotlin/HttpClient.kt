package com.example.tagnfckotlin

import java.io.IOException
import okhttp3.*

class HttpClient(val url: String){
    val client = OkHttpClient()

    fun getRequest(){
        val request = Request.Builder()
                .url(url + "list")
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {println(e)}

            override fun onResponse(call: Call, response: Response) = println(response.body()!!.string())
        })
        println("fine getRequest")
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