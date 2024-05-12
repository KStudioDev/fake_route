package com.annguyen.fakeroute.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.annguyen.fakeroute.actions.DirectionsRequest
import com.annguyen.fakeroute.actions.RequestLocation
import com.annguyen.fakeroute.screens.Location
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapViewState(
    val currentUserLocation: Location? = null,
    val start: Location? = null,
    val end: Location? = null,
    val routes: List<Location> = emptyList(),
    val isLoading: Boolean = false
)

class MapViewModel: ViewModel() {
    private val _state = MutableStateFlow(MapViewState())
    val state = _state.asStateFlow()

    private val _exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e("TAG", "Error ", throwable )
    }

    fun fetchUserLocation(context: Context) {
        viewModelScope.launch(_exceptionHandler) {
            val locationProvider = RequestLocation(context.applicationContext)
            _state.update { it.copy(isLoading = true) }
            val location = locationProvider.request()
            _state.update {
                it.copy(
                    currentUserLocation = Location(location.latitude, location.longitude),
                    start = Location(location.latitude, location.longitude),
                    isLoading = false
                )
            }
        }
    }

    fun addEndLocation(location: Location) {
        viewModelScope.launch {
            _state.emit(
                _state.value.copy(
                    end = location
                )
            )
        }
    }

    fun generateRoute() {
        val start = _state.value.start ?: return
        val end = _state.value.end ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val routes = DirectionsRequest(
                start = start,
                end = end
            ).exe()
            _state.update {
                it.copy(
                    routes = routes,
                    isLoading = false
                )
            }
        }
    }
}