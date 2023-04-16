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

import java.text.SimpleDateFormat
import java.util.*

/**
 * A data class that represents a scooter with a name and location.
 *
 * @param name The name of the scooter.
 * @param location The location of the scooter.
 */
// Dumb null initializers below because the Firebase API cannot call a constructor with arguments :(
data class Scooter(
    val name: String? = null,
    var location: String? = null,
    var timestamp: Long? = null
) {
    /**
     * Returns a human-readable string representation of the [timestamp].
     */
    fun getTime(): String {
        val date = Date(timestamp!!)
        val format = SimpleDateFormat("yyyy/MM/dd HH:mm")
        return format.format(date)
    }

    /**
     * Returns a human-readable string with the scooter details.
     */
    override fun toString(): String {
        return "[Scooter] $name is placed at $location. Last updated at ${getTime()}."
    }
}
