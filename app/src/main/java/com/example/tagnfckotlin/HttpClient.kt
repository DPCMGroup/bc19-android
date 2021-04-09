package com.example.tagnfckotlin

import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import kotlin.jvm.Throws

class HttpClient(val url: String){
    private val client = OkHttpClient()

    fun getRequest(then : (String) -> Unit){
        val request = Request.Builder()
                .url(url + "list")
                .build()

        sendRequest(request, then)
    }

    fun insertRequest(id : Int, xPos : Int, yPos : Int, status : String, then : (String) -> Unit){
        val json ="""
            {
                "WorkstationId": $id,
                "Xposition": $xPos,
                "Yposition": $yPos,
                "Status": "$status"
            }
        """.trimIndent()
        print(json)
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body: RequestBody = RequestBody.create(JSON, json)
        val request = Request.Builder()
                .url(url + "insert")
                .post(body)
                .build()

        sendRequest(request, then)
    }

    fun login(json: JSONObject, then : (String) -> Unit){
        println("ciao3")
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body: RequestBody = RequestBody.create(JSON, json.toString())
        val request = Request.Builder()
            .url(url + "login")
            .post(body)
            .build()

        return sendRequest(request, then)

    }


    fun deleteRequest(id : Int, then : (String) -> Unit){
        val request = Request.Builder()
                .url(url+id ) //aggiungo in fondo all'url l'id della postazione da eliminare, perché l'API accetta questo formato
                .delete()
                .build()
        sendRequest(request, then)
    }

    //General method to send a request and set the function (then) to be called when there is a response
    fun sendRequest(request : Request, then: (String) -> Unit){
        println("ciao4")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {println(e)}
            override fun onResponse(call: Call, response: Response) : Unit = then(response.body()!!.string())
        })
    }

    //This method is experimental. It does not work on another thread so, unless you set the threadPolicy to a permitAll() one, it throws an exception.
    //On the other hand, it doesn't need a callback function, it returns the result directly.
    @Throws(IOException::class)
    fun getRequest2(): String? {
        val request: Request = Request.Builder()
                .url(url + "list")
                .build()
        client.newCall(request).execute().use { response -> return response.body()!!.string() }
    }

}