package com.mint.digitransitdemo.data

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.mint.digitransitdemo.StopQuery
import com.mint.digitransitdemo.StopsQuery
import com.mint.digitransitdemo.domain.DetailStop
import com.mint.digitransitdemo.domain.PageInfo
import com.mint.digitransitdemo.domain.ShopRepository
import com.mint.digitransitdemo.domain.StopsModel

class ShopRepositoryImpl(
    private val apolloClient: ApolloClient
) : ShopRepository {
    override suspend fun getStops(
        lat: Double,
        lon: Double,
        radius: Int,
        after: String
    ): StopsModel {
        return apolloClient
            .query(
                StopsQuery(
                    lat = lat,
                    lon = lon,
                    radius = radius,
                    after = if (after.isBlank()) Optional.Absent else Optional.present(after)
                )
            )
            .execute()
            .data
            ?.stopsByRadius
            ?.toStopsModel() ?: StopsModel(
            stops = emptyList(),
            pageInfo = PageInfo(hasNextPage = false, endCursor = null)
        )
    }

    override suspend fun getStop(id: String): DetailStop? {
        return apolloClient
            .query(StopQuery(gtfs_id = id))
            .execute()
            .data
            ?.stop
            ?.toDetailStop()
    }
}