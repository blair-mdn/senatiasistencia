package com.daniel.appsenati

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import java.text.SimpleDateFormat
import java.util.*
import android.app.AlertDialog
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.text.format
import android.util.Log

class GuardActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvLastScan: TextView
    private lateinit var btnScanQR: MaterialButton
    private lateinit var btnUpdateSalida: MaterialButton
    private lateinit var btnLogout: MaterialButton
    private lateinit var btnChangePassword: MaterialButton
    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var capture: CaptureManager
    private lateinit var btnVisitantes: MaterialButton

    private val CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.opciones_guardia)

        // Inicializar vistas
        tvWelcome = findViewById(R.id.tvWelcome)
        tvLastScan = findViewById(R.id.tvLastScan)
        btnScanQR = findViewById(R.id.btnScanQR)
        btnUpdateSalida= findViewById(R.id.btnUpdateSalida)
        btnLogout = findViewById(R.id.btnLogout)
        btnVisitantes = findViewById(R.id.btnVisitantes)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        // Obtener datos del intent
        val name = intent.getStringExtra("NAME") ?: "Vigilante"

        // Configurar la interfaz
        tvWelcome.text = "Bienvenido, $name"
        tvLastScan.text = "Último escaneo: Ninguno"

        // Configurar botón para realizar la entrada
        btnScanQR.setOnClickListener {
            if (checkCameraPermission()) {
                startQRScanner()
            } else {
                requestCameraPermission()
            }
        }

        // Configurar botón para actualizar hora de salida
        btnUpdateSalida.setOnClickListener {
            if(checkCameraPermission()){
                startQRScannerForSalida()
            } else {
                requestCameraPermission()
            }
        }




        // Configurar el botón de cerrar sesión
        btnLogout.setOnClickListener {
            logout()
        }

        btnVisitantes.setOnClickListener {
            val intent = Intent(this, VisitanteActivity::class.java)
            startActivity(intent)
        }

        btnChangePassword.setOnClickListener {
            val intent = Intent(this, changePasswordActivity::class.java)
            startActivity(intent)
        }

    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    private fun startQRScanner() {
        val intent = Intent(this, ScannerActivity::class.java)
        startActivityForResult(intent, SCANNER_ENTRADA_CODE)
    }
    private fun startQRScannerForSalida() {
        val intent = Intent(this, ScannerActivity::class.java)
        startActivityForResult(intent, SCANNER_SALIDA_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val scannedDni = data?.getStringExtra("SCAN_RESULT")

            when (requestCode) {
                SCANNER_ENTRADA_CODE -> {
                    scannedDni?.let { processScannedQR(it) }
                }
                SCANNER_SALIDA_CODE -> {
                    scannedDni?.let { processScannedQRForSalida(it) }
                }
            }
        }
    }

    private fun processScannedQRForSalida(dni: String) {
        // Validar que el DNI tenga formato correcto (8 dígitos)
        if (dni.matches(Regex("\\d{8}"))) {
            // Actualizar hora de salida
            updateSalida(dni.toInt())

            // Mostrar información del escaneo
            val currentTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                .format(Date())

            tvLastScan.text = "Último escaneo (Salida):\nDNI: $dni\nFecha: $currentTime"

            Toast.makeText(
                this,
                "Hora de salida actualizada para DNI: $dni",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                this,
                "Código QR inválido. Debe contener un DNI de 8 dígitos",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    private fun processScannedQR(dni: String) {
        // Validar que el DNI tenga formato correcto (8 dígitos)
        if (dni.matches(Regex("\\d{8}"))) {
            // Registrar el acceso
            registerAccess(dni)

            // Mostrar información del escaneo
            val currentTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                .format(Date())

            tvLastScan.text = "Último escaneo:\nDNI: $dni\nFecha: $currentTime"

            registrarAsistenciaDesdeQR(dni)

            Toast.makeText(
                this,
                "Acceso registrado para DNI: $dni",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                this,
                "Código QR inválido. Debe contener un DNI de 8 dígitos",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun registerAccess(dni: String) {
        // Guardar el registro de acceso en SharedPreferences
        val sharedPref = getSharedPreferences("AccessLog", MODE_PRIVATE)
        val currentTime = System.currentTimeMillis()

        with(sharedPref.edit()) {
            putString("last_scanned_dni", dni)
            putLong("last_scan_time", currentTime)
            apply()
        }
    }

    private fun logout() {

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setMessage("¿Seguro que quieres salir?")
            .setTitle("Cerrar Sesión")
            .setPositiveButton("Ok") { dialog, which ->
                // Limpiar datos de sesión
                val sharedPref = getSharedPreferences("MiAppPrefs", MODE_PRIVATE)
                with(sharedPref.edit()) { // Elimina todos los datos guardados
                    clear()
                    apply()
                }

                // Mostrar mensaje
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()

                // Volver a la pantalla de login
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()





    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startQRScanner()
                } else {
                    Toast.makeText(
                        this,
                        "Se necesita permiso de cámara para escanear códigos QR",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    companion object {
        private const val SCANNER_REQUEST_CODE = 101
        private const val SCANNER_ENTRADA_CODE = 101
        private const val SCANNER_SALIDA_CODE = 102
    }


    private fun updateSalida(dniUser: Int){
        val sharedPref = getSharedPreferences("MiAppPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", null)
        val API= "https://senatiasistencia.willianjc.dev/registro/updateSalida"

        Log.d("UpdateSalida", "Iniciando actualización de salida para DNI: $dniUser")

        if (token == null){
            runOnUiThread {
                Toast.makeText(this, "No hay sesión activa para actualizar la salida", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val json= JSONObject().apply {
            put("userDni", dniUser)
            put("horaSalida", hora)
        }

        Log.d("UpdateSalida", "JSON enviado: $json")
        Log.d("UpdateSalida", "Token: Bearer ${token.take(20)}...")

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json.toString()
        )
        val request = Request.Builder()
            .url(API)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .patch(body)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("UpdateSalida", "Error de red: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@GuardActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("UpdateSalida", "Código de respuesta: ${response.code}")
                Log.d("UpdateSalida", "Respuesta del servidor: $responseBody")

                runOnUiThread {
                    if (response.isSuccessful && responseBody != null) {
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val message = jsonResponse.getString("message")
                            Toast.makeText(this@GuardActivity, "✅ $message", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(this@GuardActivity, "Error al parsear el JSON", Toast.LENGTH_SHORT).show()
                            Log.e("UpdateSalida", "Error al parsear JSON: ${e.message}")
                        }
                    } else {
                        // Manejo mejorado de errores
                        val errorMessage = when (response.code) {
                            400 -> "Datos inválidos o falta información requerida"
                            401 -> "Token inválido o expirado"
                            403 -> "No tienes permisos para realizar esta acción"
                            404 -> "No se encontró un registro activo para este DNI"
                            else -> "Error del servidor (${response.code})"
                        }

                        Toast.makeText(this@GuardActivity, "❌ $errorMessage", Toast.LENGTH_LONG).show()

                        if (responseBody != null) {
                            Log.e("UpdateSalida", "Error del servidor: $responseBody")
                        }
                    }
                }
            }
        })

    }


    private fun registrarAsistenciaDesdeQR(dniEstudiante: String) {
        val sharedPref = getSharedPreferences("MiAppPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", null)
        val dniGuardia = sharedPref.getString("USER_DNI", null)

        if (token == null || dniGuardia == null) {
            runOnUiThread {
                Toast.makeText(this, "No hay sesión activa para registrar", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val fecha = LocalDate.now().toString()
        val hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val ubicacion = "Entrada Principal"

        val json = JSONObject().apply {
            put("userDni", dniEstudiante.toInt())
            put("fecha", fecha)
            put("horaEntrada", hora)
            put("verificadoPorDni", dniGuardia.toInt())
            put("ubicacion", ubicacion)
        }
        println("JSON a enviar: $json")
        val body = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://senatiasistencia.willianjc.dev/registro/create") // <-- CAMBIA ESTA URL
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@GuardActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@GuardActivity, "✅ Registro exitoso", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@GuardActivity, "❌ Error al registrar: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

}