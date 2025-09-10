package com.daniel.appsenati
import android.content.Context
import android.content.Intent
import android.widget.Button
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import androidx.lifecycle.lifecycleScope
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class changePasswordActivity: AppCompatActivity() {
    private lateinit var etCurrentPassword: TextInputEditText
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnCancel: Button
    private lateinit var btnChangePassword: Button

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepassword)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        etCurrentPassword = findViewById(R.id.etCurrentPassword)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        btnCancel = findViewById(R.id.btnCancel)
    }

    private fun setupClickListeners() {
        btnChangePassword.setOnClickListener {
            validateAndChangePassword()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun validateAndChangePassword() {
        val currentPassword = etCurrentPassword.text.toString()
        val newPassword = etNewPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (currentPassword.isEmpty()) {
            etCurrentPassword.error = "Ingresa tu contraseña actual"
            return
        }
        if (newPassword.isEmpty()) {
            etNewPassword.error = "Ingresa una nueva contraseña"
            return
        }
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Confirma tu nueva contraseña"
            return
        }
        if (newPassword != confirmPassword) {
            etConfirmPassword.error = "Las contraseñas no coinciden"
            return
        }
        if (currentPassword == newPassword) {
            etNewPassword.error = "La nueva contraseña debe ser diferente a la actual"
            return
        }

        if (newPassword.length < 8) {
            etNewPassword.error = "La nueva contraseña debe tener al menos 8 caracteres"
            return
        }

        changePassword(currentPassword, newPassword)
    }

    private fun changePassword(currentPassword: String, newPassword: String) {
        val url = "https://senatiasistencia.willianjc.dev/auth/changePassword"

        // Obtener el token almacenado en SharedPreferences
        val sharedPref = getSharedPreferences("MiAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", null) ?: run {
            Toast.makeText(this, "No se encontró el token de autenticación", Toast.LENGTH_SHORT)
                .show()
            return
        }


        print("TOKEN: $token")

        // Crear el JSON para la solicitud
        val json = JSONObject()
        json.put("currentPassword", currentPassword)
        json.put("newPassword", newPassword)

        // Configurar el cuerpo de la solicitud con el JSON
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json.toString()
        )

        // Crear la solicitud HTTP con el encabezado de autorización
        val request = Request.Builder()
            .url(url)
            .patch(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $token")
            .build()

        // Ejecutar la solicitud de forma asíncrona
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@changePasswordActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
                Log.e("ChangePassword", "Fallo de red: ${e.message}")
            }

            // Manejar la respuesta del servidor
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                runOnUiThread {
                    if (response.isSuccessful && responseBody != null) {
                        try {
                            // Parsear la respuesta JSON
                            val jsonResponse = JSONObject(responseBody)
                            val message = jsonResponse.getString("message")
                            Toast.makeText(this@changePasswordActivity, "$message", Toast.LENGTH_SHORT).show()
                            finish()

                        } catch (e: Exception) {
                            // Manejar errores de parsing JSON
                            Log.e("ChangePassword", "Error al parsear JSON: ${e.message}")
                            Toast.makeText(this@changePasswordActivity, "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Manejar errores específicos según el código de estado
                        when (response.code) {
                            400 -> Toast.makeText(this@changePasswordActivity, "Datos inválidos o usuario no encontrado", Toast.LENGTH_SHORT).show()
                            401 -> {
                                if (responseBody?.contains("contraseña actual es incorrecta", ignoreCase = true) == true) {
                                    Toast.makeText(this@changePasswordActivity, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@changePasswordActivity, "Token inválido o expirado", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        })




    }



}
