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
import kotlinx.coroutines.flow.StateFlow
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

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()
    private val _state = MutableStateFlow(StopsState())
    val state = _state.asStateFlow()
    var stopsPagingData: Flow<PagingData<BaseStop>> = flowOf()

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
                stopsPagingData = getStopsUseCase.execute(
                    lat = it.currentLocation.latitude,
                    lon = it.currentLocation.longitude,
                    radius = DEFAULT_RADIUS
                ).cachedIn(viewModelScope)

                it.copy(
                    isLoading = false
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
    val selectedStop: DetailStop? = null,
    val currentLocation: Location = Location(""),
    val shouldLoadMore: Boolean = true,
    val pageId: String = ""
)

private const val DEFAULT_RADIUS = 500