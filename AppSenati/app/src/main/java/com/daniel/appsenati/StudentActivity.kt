package com.daniel.appsenati

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import android.graphics.Color

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
