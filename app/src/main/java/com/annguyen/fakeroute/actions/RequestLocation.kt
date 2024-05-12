package com.annguyen.fakeroute.actions

import android.content.Context
import android.location.Location
import android.os.Bundle
import com.yayandroid.locationmanager.LocationManager
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import com.yayandroid.locationmanager.constants.ProviderType
import com.yayandroid.locationmanager.listener.LocationListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RequestLocation(private val context: Context) {
    private val locationConfig = LocationConfiguration.Builder()
        .useDefaultProviders(
            DefaultProviderConfiguration.Builder()
                .requiredTimeInterval(10_000)
                .acceptableTimePeriod(10_000)
                .gpsMessage("Turn on GPS")
                .setWaitPeriod(ProviderType.GPS, 20_000)
                .setWaitPeriod(ProviderType.NETWORK, 20_000)
                .build()
        )
        .build()

//    private val locationManager = LocationManager.Builder(context)
//        .configuration(locationConfig)
//        .notify(this)
//        .build()


    suspend fun request() = suspendCancellableCoroutine { cont ->
        val locationManager = LocationManager.Builder(context)
            .configuration(locationConfig)
            .notify(object: DefaultLocationListener() {
                override fun onLocationChanged(location: Location?) {
                    if (location != null) {
                        cont.resume(location)
                    } else {
                        cont.resumeWithException(Exception("Location is null"))
                    }
                }

                override fun onLocationFailed(type: Int) {
                    super.onLocationFailed(type)
                    cont.resumeWithException(Exception("Location failed"))
                }
            })
            .build()
        locationManager.get()
    }
}

open class DefaultLocationListener: LocationListener {
    override fun onProcessTypeChanged(processType: Int) {

    }

    override fun onLocationChanged(location: Location?) {
    }

    override fun onLocationFailed(type: Int) {
    }

    override fun onPermissionGranted(alreadyHadPermission: Boolean) {
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

}