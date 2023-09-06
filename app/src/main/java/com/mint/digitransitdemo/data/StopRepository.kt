package com.mint.digitransitdemo.data

import androidx.paging.PagingData
import com.mint.digitransitdemo.domain.BaseStop
import com.mint.digitransitdemo.domain.DetailStop
import kotlinx.coroutines.flow.Flow

interface StopRepository {
    suspend fun getStops(
        lat: Double,
        lon: Double,
        radius: Int
    ): Flow<PagingData<BaseStop>>

    suspend fun getStop(id: String): DetailStop?
}