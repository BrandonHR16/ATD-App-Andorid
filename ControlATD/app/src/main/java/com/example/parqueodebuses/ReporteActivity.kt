package com.example.parqueodebuses


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.parqueodebuses.Mail.AppExecutors
import com.example.parqueodebuses.databinding.ActivityReporteBinding
import com.example.parqueodebuses.model.ModelReporteServicio
import com.example.parqueodebuses.model.ModeloReporte
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.util.*



class ReporteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReporteBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    lateinit var appExecutors: AppExecutors



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReporteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appExecutors = AppExecutors()

        binding.etDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    binding.etDate.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year)
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        //crea un array de strings con jusdon16@gmail.com hola y hol2


        try {

        } catch (e: Exception) {
            Log.d("Error", e.message.toString())
        }

        auth = FirebaseAuth.getInstance()

        binding.btnXD.setOnClickListener {
            if (binding.rbControlServicio.isChecked) {
                getdatosReviciones()
            } else if (binding.rbPuntoControl.isChecked) {
                getdatosPutosControl()
            } else {
                Toast.makeText(this, "Seleccione una opcion", Toast.LENGTH_SHORT).show()
            }
        }

    }


    var nombreControlador = ""
    var nombreControlador2 = ""
    fun getdatosPutosControl() {
        val array = arrayListOf<ModeloReporte>()
        //otener los datos de Firestore
        val db = Firebase.firestore
        val docRef = db.collection("DatosPuntosControl")
        val document = Document(PageSize.LETTER.rotate())
        //guardar el archivo
        //si el archivo existe lo borra
        val file = File(getExternalFilesDir(null), "reporte2.pdf")
        if (file.exists()) {
            file.delete()
        }
        val fileUri = file.toUri()
        val docu = PdfWriter.getInstance(document, file.outputStream())

        document.open()
        //recorrer todos los documentos
        docRef.get().addOnSuccessListener { data1 ->
            for (documentSnapshot in data1) {
                val data = db.collection("DatosPuntosControl").document(documentSnapshot.id)
                    .collection("Control").get()
                //otener el nombre de esta direccion /Usuarios/4HoFHBJc91RgJOU2FBkV3FF9cbP2
                val docRef2 = db.collection("Usuarios").document(documentSnapshot.id)
                docRef2.get().addOnSuccessListener { dataNombre ->
                    nombreControlador = dataNombre.get("Nombre").toString()
                }
                data.addOnSuccessListener { data2 ->
                    for (documentSnapshot2 in data2) {
                        //imprimir los datos
                        val dataFinal = documentSnapshot2.data
                        val control = ModeloReporte(
                            dataFinal["unidad"].toString(),
                            dataFinal["ramal"].toString(),
                            dataFinal["hora"].toString(),
                            dataFinal["capacidad"].toString(),
                            dataFinal["estado"].toString(),
                            dataFinal["ubicacion"].toString(),
                            dataFinal["fecha"].toString(),
                            nombreControlador.toString()

                        )
                        //20/2/2022 01:34:52
                        //trasformar la fecha a un formato donde solo se guarde 20/2/2022

                        try {
                            val fecha = control.fecha.split(" ")
                            val fecha2 = fecha[0].split("/")
                            val fecha3 = fecha2[0] + "/" + fecha2[1] + "/" + fecha2[2]
                            val datapiker = binding.etDate.text.toString()
                            if (fecha3 == datapiker) {
                                array.add(control)
                            }
                        } catch (
                            e: Exception
                        ) {

                        }


                    }
                }.addOnCompleteListener {
                    try {
                        val document = Document(PageSize.A1)
                        //guardar el archivo
                        //si el archivo existe lo borra
                        val file = File(getExternalFilesDir(null), "reporte2.pdf")
                        if (file.exists()) {
                            file.delete()
                        }
                        val fileUri = file.toUri()
                        val docu = PdfWriter.getInstance(document, file.outputStream())
                        array.sortBy { it.fecha }
                        document.open()
                        val table = PdfPTable(8)
                        table.addCell("Capacidad")
                        table.addCell("Estado")
                        table.addCell("Fecha")
                        table.addCell("Hora")
                        table.addCell("Ramal")
                        table.addCell("Ubicacion")
                        table.addCell("Unidad")
                        table.addCell("Controlador")
                        table.completeRow()
                        for (i in array) {
                            table.addCell(i.capacidad.toString())
                            table.addCell(i.estado.toString())
                            table.addCell(i.fecha.toString())
                            table.addCell(i.Hora.toString())
                            table.addCell(i.Ramal.toString())
                            table.addCell(i.ubicacion.toString())
                            table.addCell(i.Unidad.toString())
                            table.addCell(i.controlador.toString())
                            table.completeRow()
                        }
                        //hacer la letra mas grande y aplicarla a la tabla
                        val font = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)
                        table.setWidthPercentage(100f)
                        document.addTitle("Reporte control de" + Date().toString())
                        document.add(table);
                        document.close()
                        //eviar a storage el pdf
                        val storage = FirebaseStorage.getInstance()
                        val storageRef = storage.reference

                        val path = "ReportesPuntosDeControl/" + Date().toString() + ".pdf"
                        val fileRef = storageRef.child(path)
                        fileRef.putFile(fileUri)
                            .addOnSuccessListener {
                                Log.d("Reporte", "File Uploaded")

                                Toast.makeText(
                                    this,
                                    "Reporte generado",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {
                                Log.d("Reporte", "File not Uploaded")
                                Toast.makeText(
                                    this,
                                    "Error al generar el reporte",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        // enviar un correo a jusdon16@gmail.com con el pdf
                        val emailIntent = Intent(Intent.ACTION_SEND)
                        emailIntent.type = "application/pdf"


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }

    fun getdatosReviciones() {
        val array = arrayListOf<ModelReporteServicio>()
        //otener los datos de Firestore
        val db = Firebase.firestore
        val docRef = db.collection("Servicios")
        val document = Document(PageSize.LETTER.rotate())
        //guardar el archivo
        //si el archivo existe lo borra
        val file = File(getExternalFilesDir(null), "reporte2.pdf")
        if (file.exists()) {
            file.delete()
        }
        val fileUri = file.toUri()
        val docu = PdfWriter.getInstance(document, file.outputStream())

        document.open()
        //recorrer todos los documentos
        docRef.get().addOnSuccessListener { data1 ->
            for (documentSnapshot in data1) {
                val data = db.collection("Servicios").document(documentSnapshot.id)
                    .collection("servicios").get()
                //otener el nombre de esta direccion /Usuarios/4HoFHBJc91RgJOU2FBkV3FF9cbP2
                val docRef2 = db.collection("Usuarios").document(documentSnapshot.id)
                docRef2.get().addOnSuccessListener { dataNombre ->
                    nombreControlador2 = dataNombre.get("Nombre").toString()
                }
                data.addOnSuccessListener { data2 ->
                    for (documentSnapshot2 in data2) {
                        //imprimir los datos
                        val dataFinal = documentSnapshot2.data
                        val control = ModelReporteServicio(
                            dataFinal["unidad"].toString(),
                            if (dataFinal["escobillas"].toString().toBoolean()) {
                                "Buen estado"
                            } else {
                                "Malestado"
                            },
                            if (dataFinal["lucesExternas"].toString().toBoolean()) {
                                "Buen estado"
                            } else {
                                "Malestado"
                            },
                            if (dataFinal["lucesInternas"].toString().toBoolean()) {
                                "Buen estado"
                            } else {
                                "Malestado"
                            },
                            dataFinal["otro"].toString(),
                            dataFinal["fecha"].toString(),
                            nombreControlador2.toString()
                            //rempplazar los dato true por si

                        )

                        try {
                            val fecha = control.fecha.split(" ")
                            val fecha2 = fecha[0].split("/")
                            val fecha3 = fecha2[0] + "/" + fecha2[1] + "/" + fecha2[2]
                            val datapiker = binding.etDate.text.toString()
                            if (fecha3 == datapiker) {
                                array.add(control)
                            }
                        } catch (
                            e: Exception
                        ) {
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                        }


                    }
                }.addOnCompleteListener {
                    try {
                        val document = Document(PageSize.A1)
                        //guardar el archivo
                        //si el archivo existe lo borra
                        val file = File(getExternalFilesDir(null), "reporte2.pdf")
                        if (file.exists()) {
                            file.delete()
                        }
                        val fileUri = file.toUri()
                        val docu = PdfWriter.getInstance(document, file.outputStream())

                        document.open()
                        //ordenar el array por fecha
                        array.sortBy { it.fecha }
                        val table = PdfPTable(7)
                        table.addCell("Unidad")
                        table.addCell("Escobillas")
                        table.addCell("LucesExternas")
                        table.addCell("LucesInternas")
                        table.addCell("Otro")
                        table.addCell("Fecha")
                        table.addCell("Controlador")
                        table.completeRow()
                        for (i in array) {
                            table.addCell(i.unidad)
                            table.addCell(i.escobillas.toString())
                            table.addCell(i.lucesExternas.toString())
                            table.addCell(i.lucesInternas.toString())
                            table.addCell(i.otro)
                            table.addCell(i.fecha)
                            table.addCell(i.controlador)
                            table.completeRow()
                        }
                        //hacer la letra mas grande y aplicarla a la tabla
                        val font = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)
                        table.setWidthPercentage(100f)
                        document.addTitle("Reporte de control" + Date().toString())
                        document.add(table);
                        document.close()
                        //eviar a storage el pdf
                        val storage = FirebaseStorage.getInstance()
                        val storageRef = storage.reference

                        val path = "ReportesControl/" + Date().toString() + ".pdf"
                        val fileRef = storageRef.child(path)
                        fileRef.putFile(fileUri)
                            .addOnSuccessListener {
                                Log.d("Reporte", "File Uploaded")
                                //enviar un corre a jusdon16@gmail.com

                            }
                            .addOnFailureListener {
                                Log.d("Reporte", "File not Uploaded")
                            }


                        Log.d("Reporte", "Reporte enviado")


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }



}


