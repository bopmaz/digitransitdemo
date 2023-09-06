package com.mint.digitransitdemo.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.mint.digitransitdemo.StopsQuery
import com.mint.digitransitdemo.domain.BaseStop

class StopsPagingSource(
    private val apolloClient: ApolloClient,
    private val lat: Double,
    private val lon: Double,
    private val radius: Int
) : PagingSource<String, BaseStop>() {
    override fun getRefreshKey(state: PagingState<String, BaseStop>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, BaseStop> {
        val endCursor = params.key ?: ""

        return try {
            val stopsResponse = apolloClient
                .query(
                    StopsQuery(
                        lat = lat,
                        lon = lon,
                        radius = radius,
                        after = if (endCursor.isBlank()) Optional.Absent else Optional.present(
                            endCursor
                        )
                    )
                )
                .execute()

            if (stopsResponse.errors != null) {
                LoadResult.Error(Throwable(message = "${stopsResponse.errors}"))
            } else {
                val stopsModel = stopsResponse.data?.stopsByRadius?.toStopsModel()
                val prevKey = params.key
                val nextKey =
                    stopsResponse.data?.stopsByRadius?.pageInfo?.hasNextPage.let {
                        if (it == true) stopsModel?.pageInfo?.endCursor else null
                    }
                LoadResult.Page(
                    data = stopsModel?.stops ?: emptyList(),
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(Throwable(message = e.localizedMessage ?: "Unexpected error occurred"))
        }
    }
}