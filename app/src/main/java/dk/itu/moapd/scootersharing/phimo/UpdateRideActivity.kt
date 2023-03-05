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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dk.itu.moapd.scootersharing.phimo.databinding.ActivityUpdateRideBinding

class UpdateRideActivity : AppCompatActivity() {
    private lateinit var updateRideBinding: ActivityUpdateRideBinding

    companion object {
        lateinit var ridesDB: RidesDB
    }

    /**
     * Performs initialization of the activity, by creating view bindings and setting the content view.
     *
     * @param savedInstanceState The saved instance state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ridesDB = RidesDB.get(this)

        updateRideBinding = ActivityUpdateRideBinding.inflate(layoutInflater)

        with (updateRideBinding) {
            updateRideButton.setOnClickListener {
                if (editTextName.text.isNotEmpty() && editTextLocation.text.isNotEmpty()) {
                    val location = editTextLocation.text.toString().trim()
                    ridesDB.updateCurrentScooter(location)
                }
            }

            val scooter = ridesDB.getCurrentScooter()
            editTextName.setText(scooter.name)
            editTextLocation.setText(scooter.location)
        }

        setContentView(updateRideBinding.root)
    }
}
