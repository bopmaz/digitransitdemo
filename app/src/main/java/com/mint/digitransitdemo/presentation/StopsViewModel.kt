package com.mint.digitransitdemo.presentation

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mint.digitransitdemo.domain.BaseStop
import com.mint.digitransitdemo.domain.DetailStop
import com.mint.digitransitdemo.domain.GetStopUseCase
import com.mint.digitransitdemo.domain.GetStopsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StopsViewModel @Inject constructor(
    private val getStopUseCase: GetStopUseCase,
    private val getStopsUseCase: GetStopsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(StopsState())
    private var _stopsPagingData: Flow<PagingData<BaseStop>> = flowOf()

    val state = _state.asStateFlow()
    val stopsPagingData: Flow<PagingData<BaseStop>>
        get() = _stopsPagingData

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
        }
    }

    fun getStops() {
        viewModelScope.launch {
            _state.update {
                _stopsPagingData = getStopsUseCase.execute(
                    lat = it.currentLocation.latitude,
                    lon = it.currentLocation.longitude,
                    radius = DEFAULT_RADIUS
                ).cachedIn(viewModelScope)

                it.copy(
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }
    }

    fun selectStop(id: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedStop = getStopUseCase.execute(id)
                )
            }
        }
    }

    fun clearSelectedStop() {
        viewModelScope.launch {
            _state.update {
                it.copy(selectedStop = null)
            }
        }
    }

    fun updateCurrentLocation(lat: Double, lon: Double) {
        val updatedLocation = _state.value.currentLocation
        updatedLocation.latitude = lat
        updatedLocation.longitude = lon

        viewModelScope.launch {
            _state.update {
                it.copy(
                    currentLocation = updatedLocation
                )
            }
        }
    }
}

data class StopsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val selectedStop: DetailStop? = null,
    val currentLocation: Location = Location(""),
    val pageId: String = ""
)

private const val DEFAULT_RADIUS = 1500