package com.example.fotodemoapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var currentImagePath: String? = null

    private lateinit var btnOpenCamera: Button
    private lateinit var ivPhoto: ImageView
    private lateinit var foto: Bitmap

    // Se agrega funcionalidad para poder tomar fotos en la aplicación.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            resultado ->
            if(resultado.resultCode == Activity.RESULT_OK) {
                foto = resultado.data?.extras?.get("data") as Bitmap
                ivPhoto.setImageBitmap(foto)
                cargaFotoFirebase()
            }
        }

        btnOpenCamera = findViewById(R.id.btnOpenCamera)
        ivPhoto = findViewById(R.id.ivImage)

        btnOpenCamera.setOnClickListener {
            val camaraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(camaraIntent)
        }


    }

    private fun cargaFotoFirebase() {
        val progressDialogo = ProgressDialog(this)
        progressDialogo.setMessage("Cargando...")
        progressDialogo.setCancelable(false)
        progressDialogo.show()
        val fotoId = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val ahora = Date()
        val fotoNombre = fotoId.format(ahora)
        val storageReference = FirebaseStorage.getInstance().getReference("fotos/$fotoNombre")
        val bytes = ByteArrayOutputStream()
        foto.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        storageReference.putBytes(bytes.toByteArray()).addOnSuccessListener {
            if(progressDialogo.isShowing) {
                progressDialogo.dismiss()
            }
        }.addOnCanceledListener {
            Toast.makeText(this, "Error al subir", Toast.LENGTH_LONG).show()
        }

    }

}