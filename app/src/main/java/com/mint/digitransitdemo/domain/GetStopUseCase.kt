package com.mint.digitransitdemo.domain

class GetStopUseCase(
    private val stopRepository: StopRepository
) {

    suspend fun execute(id: String): DetailStop? {
        return stopRepository.getStop(id)
    }
}