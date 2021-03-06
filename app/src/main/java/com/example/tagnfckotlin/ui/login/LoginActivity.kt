package com.example.tagnfckotlin.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tagnfckotlin.HttpClient
import com.example.tagnfckotlin.MainActivity
import com.example.tagnfckotlin.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.nio.file.Paths.get


class LoginActivity : AppCompatActivity() {

    val url_json = "http://192.168.210.35:8000/user/"

    //ho utilizzato questo url per semplicit√† di test
    //val url_json="https://run.mocky.io/v3/f5d1fded-df4c-433f-8be6-b880014c13e3"


    private val client = HttpClient(url_json)


    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)



        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)
        val errore =  findViewById<TextView>(R.id.errore)
        errore.setVisibility(View.INVISIBLE)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }



        }
        login.setOnClickListener {
            val json : JSONObject = createJsonObjact()
            client.login(json, ::manageOutput)
        }

    }


    fun manageOutput(s: String) {
        //   saveJson(s)
        if (s == "\"No user found\"") {
            //errore.setVisibility(View.VISIBLE)

            val errore = findViewById<TextView>(R.id.errore)

            runOnUiThread {

                errore.visibility = View.VISIBLE
            }


        } else {
            val json = JSONObject(s)

            // String instance holding the above json
            val idutente = json.getInt("id")
            val username = findViewById<EditText>(R.id.username)

            var intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.putExtra("username", username.text.toString())
            intent.putExtra("id", idutente.toString())
            startActivity(intent)

        }
    }
/*
    private fun read_json() {
        var json: String? = null

        try {
            val inputStream: InputStream = assets.open("Prova.json")
            json = inputStream.bufferedReader().use{it.readText()}
            val jsonprova = findViewById<TextView>(R.id.jsonprova)
            jsonprova.text =json+ "ciao"
        }
        catch(e: IOException){

        }


    }

*/
    private fun createJsonObjact(): JSONObject {

    val Settings = JSONObject()
val username = findViewById<EditText>(R.id.username)
val password = findViewById<EditText>(R.id.password)
Settings.put("username", username.text)
Settings.put("password", password.text)

// Convert JsonObject to String Format
// Convert JsonObject to String Format

// saveJson(Settings.toString())


return Settings
}
/*
private fun saveJson(jsonString: String) {
val output: Writer
val file = createFile()
output = BufferedWriter(FileWriter(file))
output.write(jsonString)
output.close()
}

private fun createFile(): File {
val fileName = "Loginutente"
val storageDir =getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

    if (storageDir != null) {
        storageDir.mkdir()
    }

return File.createTempFile(
    fileName,
    ".json",
    this.createFile()

)


}
*/



private fun updateUiWithUser(model: LoggedInUserView) {
    val welcome = getString(R.string.welcome)
    val username = findViewById<EditText>(R.id.username)
    val user = username.text.toString()
//val displayName = model.displayName
// TODO : initiate successful logged in experience
    //Toast.makeText(applicationContext, "benvenuto", Toast.LENGTH_SHORT).show()


}

private fun showLoginFailed(@StringRes errorString: Int) {
Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
}


}



/**
* Extension function to simplify setting an afterTextChanged action to EditText components.
*/
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
this.addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(editable: Editable?) {
        afterTextChanged.invoke(editable.toString())
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
})
}