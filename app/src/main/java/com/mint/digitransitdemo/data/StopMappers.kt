package com.mint.digitransitdemo.data

import com.mint.digitransitdemo.StopQuery
import com.mint.digitransitdemo.StopsQuery
import com.mint.digitransitdemo.domain.BaseStop
import com.mint.digitransitdemo.domain.DetailStop

fun StopsQuery.StopsByRadius.toBaseStop(): List<BaseStop> {
    return this.edges.map {
        BaseStop(
            gtfsId = it?.node?.stop?.gtfsId ?: "",
            name = it?.node?.stop?.name ?: ""
        )
    }
}

fun StopQuery.Stop.toDetailStop(): DetailStop {
    return DetailStop(
        gtfsId = gtfsId,
        name = name,
        lon = lon ?: 0.0,
        lat = lat ?: 0.0
    )
}