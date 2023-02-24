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

package dk.itu.moapd.scootersharing.phimo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.phimo.databinding.ActivityStartRideBinding

class StartRideActivity : AppCompatActivity() {
    private lateinit var startRideBinding: ActivityStartRideBinding

    private val scooter: Scooter = Scooter("", "", System.currentTimeMillis())

    /**
     * Performs initialization of the activity, by creating view bindings and setting the content view.
     *
     * @param savedInstanceState The saved instance state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startRideBinding = ActivityStartRideBinding.inflate(layoutInflater)

        with (startRideBinding) {
            startRideButton.setOnClickListener { view ->
                if (editTextName.text.isNotEmpty() && editTextLocation.text.isNotEmpty()) {
                    val name = editTextName.text.toString().trim()
                    val location = editTextLocation.text.toString().trim()

                    scooter.name = name
                    scooter.location = location

                    editTextName.text.clear()
                    editTextLocation.text.clear()

                    showMessage(view)
                }
            }
        }

        setContentView(startRideBinding.root)
    }

    /**
     * Displays a [Snackbar] at the bottom indicating ride start using specified scooter.
     */
    private fun showMessage(view: View) {
        val message = "Ride started using scooter ${scooter.name} at location ${scooter.location}."
        val snackbar = Snackbar.make(view, message, BaseTransientBottomBar.LENGTH_SHORT)
        snackbar.show()
    }
}
