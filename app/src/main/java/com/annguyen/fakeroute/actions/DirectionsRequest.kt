package com.annguyen.fakeroute.actions

import android.util.Log
import com.annguyen.fakeroute.screens.Location
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters
import io.ktor.http.path
import java.io.StringReader

//https://api.openrouteservice.org/v2/directions/driving-car?api_key=5b3ce3597851110001cf6248a2747594d7494cb197a533c1bd1ddb2c&start=8.681495,49.41461&end=8.687872,49.420318
object Http {
    val client = HttpClient(CIO) {
        defaultRequest {
            url("https://api.openrouteservice.org")
        }

        install(Logging) {
            logger = object: Logger {
                override fun log(message: String) {
                    Log.d("HttpClient", message)
                }
            }
            level = LogLevel.ALL
        }
    }
}

class DirectionsRequest(
    val start: Location,
    val end: Location
) {
    suspend fun exe(): List<Location> {
        val response = Http.client.get {
            url("v2/directions/driving-car")
            parameter("start", "${start.lng},${start.lat}")
            parameter("end", "${end.lng},${end.lat}")
            parameter("api_key", "5b3ce3597851110001cf6248a2747594d7494cb197a533c1bd1ddb2c")
        }
        val str = response.bodyAsText()
        val jsonOb = Klaxon().parseJsonObject(StringReader(str))
        val listCoord = (jsonOb.map["features"] as? JsonArray<*>)?.firstOrNull()?.let {
            ((it as? JsonObject)?.get("geometry") as? JsonObject)?.let {
                it["coordinates"] as? JsonArray<*>
            }
        }?.map {
            (it as? JsonArray<*>).let {lngLat ->
                Location(lngLat?.get(1) as Double, lngLat.get(0) as Double)
            }
        } ?: emptyList()
        return listCoord
    }
}