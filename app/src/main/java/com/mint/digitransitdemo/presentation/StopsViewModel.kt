package com.mint.digitransitdemo.presentation

import androidx.lifecycle.ViewModel
import com.mint.digitransitdemo.domain.GetStopUseCase
import com.mint.digitransitdemo.domain.GetStopsUseCase

class StopViewModel(
    private val getStopUseCase: GetStopUseCase,
    private val getStopsUseCase: GetStopsUseCase
) : ViewModel() {
    

}