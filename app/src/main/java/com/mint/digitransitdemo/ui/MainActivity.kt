package com.mint.digitransitdemo.ui

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mint.digitransitdemo.presentation.StopsScreen
import com.mint.digitransitdemo.presentation.StopsViewModel
import com.mint.digitransitdemo.ui.theme.DigitransitDemoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContent {
            DigitransitDemoTheme {
                val permissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )

                val lifecycleOwner = LocalLifecycleOwner.current
                val viewModel = hiltViewModel<StopsViewModel>()

                DisposableEffect(
                    key1 = lifecycleOwner,
                    effect = {
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_START) {
                                permissionsState.launchMultiplePermissionRequest()
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose {
                            lifecycleOwner.lifecycle.removeObserver(observer)
                        }
                    }
                )

                StopsScreen(
                    viewModel = viewModel
                )
                permissionsState.permissions.forEach { perm ->
                    when (perm.permission) {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION -> {
                            if (permissionsState.allPermissionsGranted) {
                                setCurrentLocation {
                                    viewModel.updateCurrentLocation(
                                        lat = it.latitude,
                                        lon = it.longitude
                                    )
                                    viewModel.getStops()
                                }
                            } else {
                                viewModel.updateCurrentLocation(lat = 60.1699, lon = 24.9384)
                                viewModel.getStops()
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setCurrentLocation(callback: (Location) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            callback(it)
        }
    }
}