package dk.itu.moapd.scootersharing.phimo.helpers

import android.location.Address

fun Address.toAddressString() : String {
    val address = this
    val stringBuilder = StringBuilder()
    stringBuilder.apply {
        append(address.getAddressLine(0)).append("\n")
        append(address.locality).append("\n")
        append(address.postalCode).append("\n")
        append(address.countryName)
    }
    return stringBuilder.toString()
}
