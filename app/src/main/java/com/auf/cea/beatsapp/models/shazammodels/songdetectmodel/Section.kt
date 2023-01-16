package com.auf.cea.beatsapp.models.shazammodels.songdetectmodel

data class Section(
    var beacondata: BeacondataX,
    var footer: String,
    var metadata: List<Metadata>,
    var metapages: List<Metapage>,
    var tabname: String,
    var text: List<String>,
    var type: String,
    var youtubeurl: Youtubeurl
)