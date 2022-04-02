package com.example.parqueodebuses.model

class ModeloReporte(var Unidad: String, var Ramal: String, var Hora: String, var capacidad: String,
                    var estado: String, var ubicacion: String,var fecha: String, var controlador: String){

    constructor() : this("", "", "", "", "", "", "", "")
}