package com.auf.cea.beatsapp.models.shazammodels

data class Track(
    var albumadamid: String,
    var artists: List<Artist>,
    var genres: Genres,
    var hub: Hub,
    var images: ImagesX,
    var isrc: String,
    var key: String,
    var layout: String,
    var myshazam: Myshazam,
    var sections: List<Section>,
    var share: ShareX,
    var subtitle: String,
    var title: String,
    var type: String,
    var url: String,
    var urlparams: Urlparams
): java.io.Serializable