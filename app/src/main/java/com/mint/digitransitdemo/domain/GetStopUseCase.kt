package com.mint.digitransitdemo.domain

import com.mint.digitransitdemo.data.StopRepository

class GetStopUseCase(
    private val stopRepository: StopRepository
) {

    suspend fun execute(id: String): DetailStop? {
        return stopRepository.getStop(id)
    }
}