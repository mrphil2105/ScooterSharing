package dk.itu.moapd.scootersharing.phimo.helpers

import android.location.Address
import android.location.Geocoder
import android.os.Build

fun Address.toAddressString(): String {
    val address = this
    val stringBuilder = StringBuilder()
    stringBuilder.apply {
        append(address.thoroughfare)
        append(" ")
        append(address.subThoroughfare ?: featureName)
        append(", ")
        append(address.postalCode)
        append(" ")
        append(address.subLocality ?: locality)
    }
    return stringBuilder.toString()
}

fun Geocoder.getAddressString(
    latitude: Double, longitude: Double, resultCallback: ((addressString: String) -> Unit)
) {
    if (Build.VERSION.SDK_INT >= 33) {
        val geocodeListener = Geocoder.GeocodeListener { addresses ->
            addresses.firstOrNull()?.toAddressString()?.let { addressString ->
                resultCallback(addressString)
            }
        }
        getFromLocation(latitude, longitude, 1, geocodeListener)
    } else {
        getFromLocation(latitude, longitude, 1)?.let { addresses ->
            addresses.firstOrNull()?.toAddressString()?.let { addressString ->
                resultCallback(addressString)
            }
        }
    }
}
