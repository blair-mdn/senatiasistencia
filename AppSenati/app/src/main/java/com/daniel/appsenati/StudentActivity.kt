package com.daniel.appsenati

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import android.graphics.Color
import com.google.android.material.button.MaterialButton
import android.app.AlertDialog
import android.content.Context

class StudentActivity : AppCompatActivity() {
    
    private lateinit var tvWelcome: TextView
    private lateinit var tvDni: TextView
    private lateinit var ivQrCode: ImageView
    
    private lateinit var studentDni: String
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)
        
        // Inicializar vistas
        tvWelcome = findViewById(R.id.tvWelcome)
        tvDni = findViewById(R.id.tvDni)
        ivQrCode = findViewById(R.id.ivQrCode)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)
        
        // Obtener datos del intent
        val name = intent.getStringExtra("NAME") ?: "Estudiante"
        studentDni = intent.getStringExtra("DNI") ?: ""
        
        // Configurar la interfaz
        tvWelcome.text = "Bienvenido, $name"
        tvDni.text = "DNI: $studentDni"
        
        // Generar y mostrar el código QR si tenemos el DNI
        if (studentDni.isNotEmpty()) {
            generateQRCode(studentDni)
        }
        
        // Configurar el botón de cerrar sesión
        btnLogout.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder
                .setMessage("¿Seguro que quieres salir?")
                .setTitle("Cerrar Sesión")
                .setPositiveButton("Ok") { dialog, which ->
                    // Limpiar datos de sesión
                    val sharedPref = getSharedPreferences("MiAppPrefs", MODE_PRIVATE)
                    with(sharedPref.edit()) { // Eliminar todos los datos guardados
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
    }
    
    private fun generateQRCode(content: String) {
        try {
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                512,
                512
            )
            
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            
            ivQrCode.setImageBitmap(bitmap)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
