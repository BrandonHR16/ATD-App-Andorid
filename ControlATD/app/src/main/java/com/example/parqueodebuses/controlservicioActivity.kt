package com.example.parqueodebuses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.get
import com.example.parqueodebuses.databinding.ActivityControlservicioBinding
import com.example.parqueodebuses.model.Servicio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class controlservicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityControlservicioBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControlservicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnGuardar.setOnClickListener {
            enviarServicio()
        }

    }

    // Unidad, Luces Externas, Luces Internas, scobillas, otro
    //Enviar los formularios de los servicios

    fun enviarServicio(){
        try {
            var unidad = binding.txtUnidad.text.toString()
            var lucesExternas = false
            var lucesInternas = false
            var scobillas = false
            var otro = ""
            lucesExternas = binding.rbLucesExternasBuenEstado.isChecked
            lucesInternas = binding.rbLucesInternasBuenEstado.isChecked
            scobillas = binding.rbEscobillasBuenEstado.isChecked
            otro = binding.txtOtrosDescripcion.text.toString()
            //arreglo de servicios
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val fecha = sdf.format(Date())
           val dataser = Servicio(unidad, lucesExternas, lucesInternas, scobillas, otro, fecha)
            //enviar a firebase
            val db = Firebase.firestore
            db.collection("Servicios").document(auth.currentUser!!.uid).collection("servicios").add(dataser)
                .addOnSuccessListener {
                    Toast.makeText(this, "Servicio enviado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al enviar servicio", Toast.LENGTH_SHORT).show()
                }
        }catch (e: Exception){
            //imprimir error en consola
            e.printStackTrace()
            Toast.makeText(this, "Error al enviar el servicio"+e, Toast.LENGTH_SHORT).show()
        }
    }

    //limpiar los campos
    fun limpiarCampos(){
        binding.txtUnidad.setText("")
        binding.rbLucesExternasBuenEstado.isChecked = false
        binding.rbLucesInternasBuenEstado.isChecked = false
        binding.rbEscobillasBuenEstado.isChecked = false
        binding.txtOtrosDescripcion.setText("")
    }
}

//12