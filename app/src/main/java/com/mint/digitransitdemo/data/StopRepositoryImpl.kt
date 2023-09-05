package com.mint.digitransitdemo.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.apollographql.apollo3.ApolloClient
import com.mint.digitransitdemo.StopQuery
import com.mint.digitransitdemo.domain.DetailStop
import com.mint.digitransitdemo.domain.StopRepository

class StopRepositoryImpl(
    private val apolloClient: ApolloClient
) : StopRepository {
    override suspend fun getStops(
        lat: Double,
        lon: Double,
        radius: Int
    ) = Pager(PagingConfig(pageSize = 10)) {
        StopsPagingSource(apolloClient = apolloClient, lat = lat, lon = lon, radius = radius)
    }.flow

    override suspend fun getStop(id: String): DetailStop? {
        return apolloClient
            .query(StopQuery(gtfs_id = id))
            .execute()
            .data
            ?.stop
            ?.toDetailStop()
    }
}