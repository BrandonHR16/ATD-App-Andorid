package com.example.parqueodebuses.model

class Servicio(
    var unidad: String,
    var lucesExternas: Boolean,
    var lucesInternas: Boolean,
    var escobillas: Boolean,
    var otro: String,
    var fecha: String,
)
{
    constructor() : this("", false, false, false, "", "")
}