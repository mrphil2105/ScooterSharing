/*
MIT License

Copyright (c) 2023 Philip Mørch

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package dk.itu.moapd.scootersharing.phimo.models

import com.google.firebase.database.Exclude
import java.text.SimpleDateFormat
import java.util.*

/**
 * A data class that represents a scooter with a name and location.
 *
 * @param createdAt The timestamp for the creation of the scooter.
 * @param timestamp A timestamp for the last update.
 * @param name The name of the scooter.
 * @param image The name of the image for the scooter.
 * @param latitude The latitude of the scooter.
 * @param longitude The longitude of the scooter.
 * @param initialLatitude The initial latitude of the scooter.
 * @param initialLongitude The initial longitude of the scooter.
 * @param rentedBy The user id of the current renter.
 * @param lastPhoto The last photo of the scooter taken by a user.
 */
// Dumb default values below because the Firebase API cannot call a constructor with arguments :(
data class Scooter(
    val createdAt: Long = 0,
    var timestamp: Long = 0,
    val name: String? = null,
    var image: String? = null,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    val initialLatitude: Double = 0.0,
    val initialLongitude: Double = 0.0,
    var rentedBy: String? = null,
    var lastPhoto: String? = null
) {
    /**
     * Returns a human-readable string representation of the [timestamp].
     */
    @Exclude
    fun getTime(): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("yyyy/MM/dd HH:mm")
        return format.format(date)
    }

    /**
     * Returns a human-readable string with the scooter details.
     */
    override fun toString(): String {
        return "[Scooter] $name is placed at (latitude: $latitude, longitude: $longitude)." +
                " Last updated at ${getTime()}."
    }
}
