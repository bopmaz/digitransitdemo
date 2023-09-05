package com.mint.digitransitdemo.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.mint.digitransitdemo.domain.BaseStop
import com.mint.digitransitdemo.ui.theme.BodyStyle
import com.mint.digitransitdemo.ui.theme.CardHeight
import com.mint.digitransitdemo.ui.theme.DefaultElevation
import com.mint.digitransitdemo.ui.theme.MapHeight
import com.mint.digitransitdemo.ui.theme.Padding16
import com.mint.digitransitdemo.ui.theme.Padding8
import com.mint.digitransitdemo.ui.theme.TitleStyle

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StopsScreen(
    viewModel: StopsViewModel,
    onSelectedStop: (id: String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val stopList = viewModel.stopsPagingData.collectAsLazyPagingItems()
    val refreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    val pullRefreshState = rememberPullRefreshState(refreshing, onRefresh = { stopList.refresh() })
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    LatLng(
                        state.currentLocation.latitude,
                        state.currentLocation.longitude
                    ), 15f
                )
            }

            Column {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MapHeight),
                    cameraPositionState = cameraPositionState
                ) {
                    stopList.itemSnapshotList.items.forEach {
                        Marker(
                            state = MarkerState(position = LatLng(it.lat, it.lon)),
                            title = it.name,
                        )
                    }

                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState)
                ) {

                    if (stopList.loadState.refresh == LoadState.Loading) {
                        item {
                            Text(
                                text = "Waiting for items to load",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }

                    items(count = stopList.itemCount) { index ->
                        val stop = stopList[index] ?: BaseStop()
                        StopCard(
                            stop = stop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(CardHeight)
                                .clickable { onSelectedStop(stop.gtfsId) }
                        )
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(alignment = Alignment.TopCenter)
        )
    }
}

@Composable
fun StopCard(stop: BaseStop, modifier: Modifier) {
    Card(
        elevation = DefaultElevation,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Padding16, vertical = Padding8
                )
        ) {
            Text(
                text = stop.name,
                style = TitleStyle
            )
            Text(
                text = "STOP ID: ${stop.gtfsId}",
                style = BodyStyle
            )
        }
    }
}