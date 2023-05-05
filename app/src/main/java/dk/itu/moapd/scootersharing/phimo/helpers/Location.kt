package dk.itu.moapd.scootersharing.phimo.helpers

import kotlin.math.*

// These are radius values.
const val BOUNDARY_KILOMETERS = 5.0
const val BOUNDARY_METERS = BOUNDARY_KILOMETERS * 1000

// This function is straight from ChatGPT. It's the Haversine formula.
fun distanceInKilometers(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371.0 // Earth's radius in kilometers
    val lat1Rad = Math.toRadians(lat1)
    val lon1Rad = Math.toRadians(lon1)
    val lat2Rad = Math.toRadians(lat2)
    val lon2Rad = Math.toRadians(lon2)

    val deltaLat = lat2Rad - lat1Rad
    val deltaLon = lon2Rad - lon1Rad

    val a = sin(deltaLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val distance = R * c

    return distance
}
