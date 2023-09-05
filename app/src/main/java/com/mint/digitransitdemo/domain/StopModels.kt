package com.mint.digitransitdemo.domain

import com.mint.digitransitdemo.type.WheelchairBoarding

data class StopsModel(
    val stops: List<BaseStop>,
    val pageInfo: PageInfo
)

data class BaseStop(
    val gtfsId: String = "",
    val name: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val wheelchairBoarding: WheelchairBoardingType = WheelchairBoardingType.UNKNOWN
)

data class PageInfo(
    val hasNextPage: Boolean,
    val endCursor: String?
)

enum class WheelchairBoardingType {
    UNKNOWN,
    NO_INFORMATION,
    NOT_POSSIBLE,
    POSSIBLE;

    companion object {
        fun fromWheelchairBoarding(type: WheelchairBoarding) = when (type) {
            WheelchairBoarding.NO_INFORMATION -> NO_INFORMATION
            WheelchairBoarding.POSSIBLE -> POSSIBLE
            WheelchairBoarding.NOT_POSSIBLE -> NOT_POSSIBLE
            WheelchairBoarding.UNKNOWN__ -> UNKNOWN
        }
    }
}