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

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    private var _location = MutableStateFlow(Location(""))
    private var _selectedStop = MutableStateFlow<DetailStop?>(null)
    private var _stopsPagingData: Flow<PagingData<BaseStop>> = flowOf()

    val uiState = _uiState.asStateFlow()
    val location = _location.asStateFlow()
    val selectedStop = _selectedStop.asStateFlow()
    val stopsPagingData: Flow<PagingData<BaseStop>>
        get() = _stopsPagingData

    fun selectStop(id: String) {
        viewModelScope.launch {
            _selectedStop.update {
                getStopUseCase.execute(id)
            }
        }
    }

    fun clearSelectedStop() {
        viewModelScope.launch {
            _selectedStop.update {
                null
            }
        }
    }

    fun updateCurrentLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.update {
                UIState.Loading
            }
            _location.update {
                it.latitude = lat
                it.longitude = lon
                it
            }
            _stopsPagingData = getStopsUseCase.execute(
                lat = _location.value.latitude,
                lon = _location.value.longitude,
                radius = DEFAULT_RADIUS
            ).cachedIn(viewModelScope)
            _uiState.update {
                UIState.Success
            }
        }
    }
}

sealed class UIState {
    object Loading : UIState()
    object Refreshing : UIState()
    object Success : UIState()
}

private const val DEFAULT_RADIUS = 1500