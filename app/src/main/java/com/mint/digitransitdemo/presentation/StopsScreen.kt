package com.mint.digitransitdemo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.mint.digitransitdemo.R
import com.mint.digitransitdemo.domain.BaseStop
import com.mint.digitransitdemo.domain.DetailStop
import com.mint.digitransitdemo.domain.WheelchairBoardingType
import com.mint.digitransitdemo.ui.theme.BodyStyle
import com.mint.digitransitdemo.ui.theme.CardHeight
import com.mint.digitransitdemo.ui.theme.Corner6
import com.mint.digitransitdemo.ui.theme.DefaultElevation
import com.mint.digitransitdemo.ui.theme.MapHeight
import com.mint.digitransitdemo.ui.theme.Padding16
import com.mint.digitransitdemo.ui.theme.Padding8
import com.mint.digitransitdemo.ui.theme.TitleStyle
import com.mint.digitransitdemo.util.toFormattedTime

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StopsScreen(
    viewModel: StopsViewModel
) {

    val state by viewModel.state.collectAsState()
    val stopList = viewModel.stopsPagingData.collectAsLazyPagingItems()
    val pullRefreshState =
        rememberPullRefreshState(state.isRefreshing, onRefresh = {
            stopList.refresh()
        })
    val composeScope = rememberCoroutineScope()

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
                    ), 14f
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
                                .clickable {
                                    viewModel.selectStop(stop.gtfsId)
                                }
                        )
                    }
                }
            }

            state.selectedStop?.let {
                StopDialog(
                    detailStop = it,
                    onDismiss = {
                        viewModel.clearSelectedStop()
                    },
                    Modifier
                        .clip(RoundedCornerShape(Corner6))
                        .background(Color.White)
                        .padding(Padding16)
                        .size(300.dp, 500.dp)
                )
            }
        }

        PullRefreshIndicator(
            refreshing = state.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(alignment = Alignment.TopCenter)
        )
    }
}

@Composable
fun StopDialog(
    detailStop: DetailStop,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(modifier = modifier) {
            Text(
                text = detailStop.name,
                style = TitleStyle
            )
            Spacer(
                modifier = Modifier.height(Padding16)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(detailStop.itineraryList) {
                    Text(
                        text = it.headsign,
                        style = TitleStyle
                    )
                    Text(
                        text = "Arrival: ${it.arrival.toFormattedTime()}",
                        style = BodyStyle
                    )
                    Text(
                        text = "Departure: ${it.departure.toFormattedTime()}",
                        style = BodyStyle
                    )
                }
            }
        }
    }
}

@Composable
fun StopCard(stop: BaseStop, modifier: Modifier) {
    Card(
        shape = RectangleShape,
        elevation = DefaultElevation,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Padding16, vertical = Padding8
                )
        ) {
            Column {
                Text(
                    text = stop.name,
                    style = TitleStyle
                )
                Text(
                    text = "STOP ID: ${stop.gtfsId}",
                    style = BodyStyle
                )
            }
            Spacer(modifier = Modifier.weight(1.0f))
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),
                painter = if (stop.wheelchairBoarding == WheelchairBoardingType.POSSIBLE)
                    painterResource(id = R.drawable.baseline_accessible_24) else
                    painterResource(id = R.drawable.baseline_not_accessible_24),
                contentDescription = if (stop.wheelchairBoarding == WheelchairBoardingType.POSSIBLE)
                    "Wheelchair Accessible" else
                    "Wheelchair Not Available"
            )
        }
    }
}