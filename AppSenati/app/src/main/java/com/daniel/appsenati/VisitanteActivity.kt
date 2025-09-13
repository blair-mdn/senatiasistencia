package com.daniel.appsenati

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class VisitanteActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.visitante_activity) // 👈 debe coincidir con tu XML

        // Referencias a los views del XML
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etDni = findViewById<EditText>(R.id.etDni)
        val etAsunto = findViewById<EditText>(R.id.etAsunto)
        val spinnerArea = findViewById<Spinner>(R.id.spinnerArea)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val dni = etDni.text.toString().trim()
            val asunto = etAsunto.text.toString().trim()
            val area = spinnerArea.selectedItem.toString()

            if (nombre.isEmpty() || dni.isEmpty() || asunto.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registrarVisita(dni, asunto, area)
        }
    }

    private fun registrarVisita(dni: String, asunto: String, area: String) {
        val json = JSONObject()
        json.put("visitorDni", dni.toInt())
        json.put("fecha", "2025-09-05") // puedes automatizar con LocalDate.now()
        json.put("horaEntrada", "09:15:00") // puedes usar LocalTime.now()
        json.put("verificadoPorDni", 12345678) // se puede reemplazar con guardia logueado
        json.put("ubicacion", "Entrada Principal")
        json.put("areaVisited", area)
        json.put("asuntoVisita", asunto)

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())

        // Obtener el token almacenado en SharedPreferences
        val sharedPref = getSharedPreferences("MiAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", null) ?: run {
            Toast.makeText(this, "No se encontró el token de autenticación", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val request = Request.Builder()
            .url("https://senatiasistencia.willianjc.dev/registro/create/visitor") // 👈 coloca la URL real
            .addHeader("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@VisitanteActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@VisitanteActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@VisitanteActivity, "Error en servidor", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}