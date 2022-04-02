package com.example.parqueodebuses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.parqueodebuses.databinding.ActivityMainBinding
import com.example.parqueodebuses.databinding.ActivityRecuperapassBinding
import com.google.firebase.auth.FirebaseAuth


class recuperapassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperapassBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecuperapassBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnEnviar.setOnClickListener {
            val email = binding.editTextTextEmailAddress.text.toString()
            //validar que email sea un email valido y no esté vacío antes de enviar
            if (email.isEmpty()) {
                binding.editTextTextEmailAddress.error = "Por favor ingrese un email valido"
                binding.editTextTextEmailAddress.requestFocus()
            } else {
                recuperarContrasena()
                Toast.makeText(this, "Se ha enviado un correo a $email", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun recuperarContrasena() {
        val email = binding.editTextTextEmailAddress.text.toString()
        if (email.isNotEmpty()) {
            auth = FirebaseAuth.getInstance()
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Se ha enviado un correo a $email", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, getText(R.string.ErrorCorreo), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}