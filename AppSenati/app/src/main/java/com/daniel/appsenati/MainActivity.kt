package com.daniel.appsenati

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etUsername.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                login(email, password)
            }
        }
    }

    private fun login(email: String, password: String) {
        val url = "https://senatiasistencia.willianjc.dev/auth/login"

        val json = JSONObject()
        json.put("email", email)
        json.put("password", password)

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
                Log.e("Login", "Fallo de red: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                runOnUiThread {
                    if (response.isSuccessful && responseBody != null) {
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val token = jsonResponse.getString("access_token")
                            val user = jsonResponse.getJSONObject("user")
                            val rol = user.getString("rol")
                            val name = user.getString("name")

                            Toast.makeText(this@MainActivity, "Bienvenido $name ($rol)", Toast.LENGTH_LONG).show()

                            val sharedPref = getSharedPreferences("MiAppPrefs", MODE_PRIVATE)
                            with (sharedPref.edit()) {
                                putString("ACCESS_TOKEN", token)
                                apply()
                            }

                        } catch (e: Exception) {
                            Toast.makeText(this@MainActivity, "Error procesando respuesta", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        when (response.code) {
                            400 -> Toast.makeText(this@MainActivity, "Datos inválidos", Toast.LENGTH_SHORT).show()
                            401 -> Toast.makeText(this@MainActivity, "Credenciales inválidas o usuario inactivo", Toast.LENGTH_SHORT).show()
                            else -> Toast.makeText(this@MainActivity, "Error: ${response.code}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}
