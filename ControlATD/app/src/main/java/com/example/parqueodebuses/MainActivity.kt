package com.example.parqueodebuses

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.parqueodebuses.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        verificarRol()

        binding.btnPuntoControl.setOnClickListener {
            val intent = Intent(this, ControlActivity::class.java)
            startActivity(intent)
        }

        binding.btnServico.setOnClickListener {
            val intent = Intent(this, controlservicioActivity::class.java)
            startActivity(intent)
        }

        binding.enviarReporte.setOnClickListener {
            val intent = Intent(this, ReporteActivity::class.java)
            startActivity(intent)
        }

        binding.registrarusuarios.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    //verificar cual es el rol del usuario para mostrar los botones correspondientes
    private fun verificarRol(){
        val user = FirebaseAuth.getInstance().currentUser
        val db = Firebase.firestore
        val docRef = db.collection("Usuarios").document(user!!.uid)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val rol = document.get("Rol")
                if (rol == "admin"){
                    binding.btnPuntoControl.visibility = android.view.View.VISIBLE
                    binding.btnServico.visibility = android.view.View.VISIBLE
                    binding.enviarReporte.visibility = android.view.View.VISIBLE
                    binding.registrarusuarios.visibility = android.view.View.VISIBLE
                }else{
                    binding.btnPuntoControl.visibility = android.view.View.VISIBLE
                    binding.btnServico.visibility = android.view.View.VISIBLE
                }
            }
        }
    }

    //


    }



