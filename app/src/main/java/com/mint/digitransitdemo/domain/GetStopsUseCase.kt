package com.mint.digitransitdemo.domain

class GetStopsUseCase(
    private val stopClient: StopClient
) {

    suspend fun execute(lat: Double, lon: Double, radius: Int): List<BaseStop> {
        return stopClient.getStops(lat, lon, radius)
    }
}