package com.example.parqueodebuses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.parqueodebuses.databinding.ActivityRecuperapassBinding
import com.example.parqueodebuses.databinding.ActivityRegisterBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackRU.setOnClickListener {
            finish()
        }

        binding.btnenviar.setOnClickListener{
            val email = binding.txtCorreo.text.toString()
            val password = binding.txtPassword.text.toString()
            val nombre = binding.txtNombre.text.toString()
            val cedula = binding.txtCedula.text.toString()
            var role = "false"
            if (binding.rbAdmin.isChecked){
                role = "admin"
            }else if (binding.rbUsuario.isChecked){
                role = "user"
            }else
            {
             Toast.makeText(this, "Seleccione un rol", Toast.LENGTH_SHORT).show()
            }
            if (email.isEmpty() || password.isEmpty()){
                binding.txtCorreo.error = "Ingrese un correo"
                binding.txtPassword.error = "Ingrese una contraseña"
            }else{
                RegisterAccount(email, password, nombre, role, cedula)
            }

        }


    }

    fun RegisterAccount(correo:String, contraseña:String, nombre:String, Role:String, cedula:String){
        //registrar a un usuario
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo, contraseña)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //registrar usuario en la base de datos
                    val db = Firebase.firestore
                    val user = FirebaseAuth.getInstance().currentUser
                    val userId = user!!.uid
                    db.collection("Usuarios").document(userId).set(
                        mapOf(
                            "Nombre" to nombre,
                            "rol" to Role,
                            "Correo" to correo,
                            "Cedula" to cedula
                        )
                    )
                }
            }.addOnSuccessListener {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                limpiar()

            }.addOnFailureListener {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
            }


    }

    fun limpiar(){
        binding.txtCorreo.setText("")
        binding.txtPassword.setText("")
        binding.txtNombre.setText("")
        binding.rbAdmin.isChecked = false
        binding.rbUsuario.isChecked = false
        binding.txtCedula.setText("")
    }
}