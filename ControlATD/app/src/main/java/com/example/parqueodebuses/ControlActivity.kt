package com.example.parqueodebuses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.parqueodebuses.databinding.ActivityControlBinding
import com.example.parqueodebuses.model.Control
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ControlActivity : AppCompatActivity() {
    private lateinit var binding: ActivityControlBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get current user
        auth = FirebaseAuth.getInstance()


        binding.btnenviar.setOnClickListener {
        sendForm()
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
    fun limpirar(){
        binding.txtunidad.text.clear()
        binding.txtramal.text.clear()
        binding.txthora.text.clear()
        binding.txtcapacidad.text.clear()
        binding.txtquedan.text.clear()
        binding.txtUbicacion.text.clear()
    }

   // funcio  para enviar el fomulario a Firestore
    fun sendForm() {
        val Unidad = binding.txtunidad.text.toString()
        val Ramal = binding.txtramal.text.toString()
        val Hora = binding.txthora.text.toString()
        val capacidad = binding.txtcapacidad.text.toString()
        val quedan = binding.txtquedan.text.toString()
        val ubicacion = binding.txtUbicacion.text.toString()
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val fecha = sdf.format(Date())

       //verificar que todos los campos esten llenos
        if (Unidad.isEmpty() || Ramal.isEmpty() || Hora.isEmpty() || capacidad.isEmpty() || quedan.isEmpty() || ubicacion.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
        }else{

            val control = Control(Unidad, Ramal, Hora, capacidad, quedan, ubicacion, fecha.toString())

            Firebase.firestore.collection("DatosPuntosControl").document(auth.currentUser!!.uid).collection("Control").add(control)
                .addOnSuccessListener {
                    //limpiar campos
                    limpirar()
                    Toast.makeText(this, "Su informacion ha sido enviada", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al enviar los datos", Toast.LENGTH_SHORT).show()
                }
        }
    }
}