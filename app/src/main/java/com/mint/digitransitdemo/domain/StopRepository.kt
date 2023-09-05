package com.mint.digitransitdemo.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface StopRepository {
    suspend fun getStops(
        lat: Double,
        lon: Double,
        radius: Int
    ): Flow<PagingData<BaseStop>>

    suspend fun getStop(id: String): DetailStop?
}