package com.annguyen.fakeroute.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.annguyen.fakeroute.viewmodels.MapViewModel
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.MarkerState
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.Polyline
import com.utsman.osmandcompose.rememberCameraState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import org.osmdroid.util.GeoPoint
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapControllerView(
    viewmodel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    val cameraState = rememberCameraState { }
    val listMarker = viewmodel.state
        .mapLatest {
            listOfNotNull(
                it.start,
                it.end
            )
        }.collectAsState(initial = emptyList())
    val userLocation = viewmodel.state.map { it.currentUserLocation }
        .collectAsState(initial = null)
    val isLoading = viewmodel.state.map { it.isLoading }.collectAsState(initial = false)
    val routes = viewmodel.state.map { it.routes }.collectAsState(initial = emptyList())
    LaunchedEffect(key1 = Unit) {
        viewmodel.fetchUserLocation(context)
    }
    LaunchedEffect(key1 = userLocation.value) {
        if (userLocation.value != null) {
            cameraState.animateTo(
                point = GeoPoint(userLocation.value!!.lat, userLocation.value!!.lng),
                pZoom = 14.0
            )
        }
    }
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 100.dp,
        sheetContent = {
            if (listMarker.value.size >= 2) {
                Button(onClick = {
                    viewmodel.generateRoute()
                }) {
                    Text("Generate Route")
                }
            }
        }) {
        Column(
            modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.Top
        ) {
            Box(modifier = Modifier
                .background(Color.Red)
                .weight(1f)
                .fillMaxWidth()) {
                OpenStreetMap(
                    modifier = Modifier,
                    cameraState = cameraState,
                    onMapLongClick = { geoPoint ->
                        viewmodel.addEndLocation(
                            Location(geoPoint.latitude, geoPoint.longitude)
                        )
                    }
                ) {
                    listMarker.value.map {
                        Marker(
                            state = MarkerState(
                                geoPoint = GeoPoint(it.lat, it.lng),
                            ),
                            id = "marker-${it.lat}-${it.lng}",
                        )
                    }
                    if (routes.value.isNotEmpty()) {
                        Polyline(geoPoints = routes.value.map { GeoPoint(it.lat, it.lng) })
                    }
                }
            }
        }
    }


    if (isLoading.value) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .background(Color.White)
            ) {
                Text(text = "Loadingggg!!!!")
            }
        }
    }

}

@Composable
fun <T> Flow<T>.collectAsEffect(
    context: CoroutineContext = EmptyCoroutineContext,
    block: (T) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        onEach(block).flowOn(context).launchIn(this)
    }
}