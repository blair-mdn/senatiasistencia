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

class GuardActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvLastScan: TextView
    private lateinit var btnScanQR: MaterialButton
    private lateinit var btnLogout: MaterialButton
    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var capture: CaptureManager

    private val CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.opciones_guardia)

        // Inicializar vistas
        tvWelcome = findViewById(R.id.tvWelcome)
        tvLastScan = findViewById(R.id.tvLastScan)
        btnScanQR = findViewById(R.id.btnScanQR)
        btnLogout = findViewById(R.id.btnLogout)

        // Obtener datos del intent
        val name = intent.getStringExtra("NAME") ?: "Vigilante"

        // Configurar la interfaz
        tvWelcome.text = "Bienvenido, $name"
        tvLastScan.text = "Último escaneo: Ninguno"

        // Configurar botón para escanear QR
        btnScanQR.setOnClickListener {
            if (checkCameraPermission()) {
                startQRScanner()
            } else {
                requestCameraPermission()
            }
        }

        // Configurar el botón de cerrar sesión
        btnLogout.setOnClickListener {
            logout()
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
        startActivityForResult(intent, SCANNER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SCANNER_REQUEST_CODE && resultCode == RESULT_OK) {
            val scannedDni = data?.getStringExtra("SCAN_RESULT")
            scannedDni?.let { processScannedQR(it) }
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
    }
}