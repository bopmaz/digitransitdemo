package com.mint.digitransitdemo.domain

data class DetailStop(
    val gtfsId: String,
    val name: String,
    val lat: Double,
    val lon: Double
)