package com.auf.cea.beatsapp.models.shazammodels

data class Option(
    var actions: List<ActionX>,
    var beacondata: Beacondata,
    var caption: String,
    var colouroverflowimage: Boolean,
    var image: String,
    var listcaption: String,
    var overflowimage: String,
    var providername: String,
    var type: String
)