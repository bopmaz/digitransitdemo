package com.mint.digitransitdemo.domain

interface ShopRepository {
    suspend fun getStops(lat: Double, lon: Double, radius: Int, after: String = ""): StopsModel
    suspend fun getStop(id: String): DetailStop?
}