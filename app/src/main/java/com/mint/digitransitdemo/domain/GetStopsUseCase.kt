package com.mint.digitransitdemo.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class GetStopsUseCase(
    private val stopRepository: StopRepository
) {

    suspend fun execute(lat: Double, lon: Double, radius: Int): Flow<PagingData<BaseStop>> {
        return stopRepository.getStops(lat, lon, radius)
    }
}