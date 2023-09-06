package com.mint.digitransitdemo.domain

data class DetailStop(
    val name: String,
    val itineraryList: List<ItineryDetail>
)

data class ItineryDetail(
    val arrival: Int,
    val departure: Int,
    val headsign: String
)
