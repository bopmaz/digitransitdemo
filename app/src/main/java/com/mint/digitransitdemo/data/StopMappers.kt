package com.mint.digitransitdemo.data

import com.mint.digitransitdemo.StopQuery
import com.mint.digitransitdemo.StopsQuery
import com.mint.digitransitdemo.domain.BaseStop
import com.mint.digitransitdemo.domain.DetailStop
import com.mint.digitransitdemo.domain.ItineryDetail
import com.mint.digitransitdemo.domain.PageInfo
import com.mint.digitransitdemo.domain.StopsModel
import com.mint.digitransitdemo.domain.WheelchairBoardingType
import com.mint.digitransitdemo.type.WheelchairBoarding

fun StopsQuery.StopsByRadius.toStopsModel(): StopsModel {
    val stopList =
        this.edges?.filter { !(it?.node?.stop?.gtfsId?.contains("MATKA") ?: true) }?.map {
            BaseStop(
                gtfsId = it?.node?.stop?.gtfsId ?: "",
                name = it?.node?.stop?.name ?: "",
                lat = it?.node?.stop?.lat ?: 0.0,
                lon = it?.node?.stop?.lon ?: 0.0,
                wheelchairBoarding = WheelchairBoardingType.fromWheelchairBoarding(
                    it?.node?.stop?.wheelchairBoarding ?: WheelchairBoarding.UNKNOWN__
                )
            )
        } ?: emptyList()

    val pageInfo = PageInfo(
        hasNextPage = this.pageInfo.hasNextPage,
        endCursor = this.pageInfo.endCursor
    )

    return StopsModel(stops = stopList, pageInfo = pageInfo)
}

fun StopQuery.Stop.toDetailStop(): DetailStop {
    val itineraryList = this.stoptimesWithoutPatterns?.map {
        ItineryDetail(
            arrival = it?.realtimeArrival ?: 0,
            departure = it?.realtimeDeparture ?: 0,
            headsign = it?.headsign ?: ""
        )
    } ?: emptyList()
    return DetailStop(
        name = name,
        itineraryList = itineraryList
    )
}