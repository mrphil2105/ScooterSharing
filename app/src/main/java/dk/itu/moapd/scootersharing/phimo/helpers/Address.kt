package dk.itu.moapd.scootersharing.phimo.helpers

import android.location.Address

fun Address.toAddressString() : String {
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
