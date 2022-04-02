package com.example.parqueodebuses.model

class ModelReporteServicio(    var unidad: String,
                               var lucesExternas: String,
                               var lucesInternas: String,
                               var escobillas: String,
                               var otro: String,
                               var fecha: String,
var controlador: String,){

    constructor() : this("","","","","","","")
}