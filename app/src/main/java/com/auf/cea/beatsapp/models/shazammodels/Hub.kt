package com.auf.cea.beatsapp.models.shazammodels

data class Hub(
    var actions: List<Action>,
    var displayname: String,
    var explicit: Boolean,
    var image: String,
    var options: List<Option>,
    var providers: List<Provider>,
    var type: String
): java.io.Serializable