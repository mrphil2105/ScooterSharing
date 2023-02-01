package dk.itu.moapd.scootersharing.phimo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    private lateinit var scooterName: EditText
    private lateinit var scooterLocation: EditText
    private lateinit var startRideButton: Button

    private val scooter: Scooter = Scooter("", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scooterName = findViewById(R.id.edit_text_name)
        scooterLocation = findViewById(R.id.edit_text_location)
        startRideButton = findViewById(R.id.start_ride_button)

        startRideButton.setOnClickListener {
            if (scooterName.text.isNotEmpty() && scooterLocation.text.isNotEmpty()) {
                val name = scooterName.text.toString().trim()
                val location = scooterLocation.text.toString().trim()

                scooter.setName(name)
                scooter.setLocation(location)

                scooterName.text.clear()
                scooterLocation.text.clear()

                showMessage()
            }
        }
    }

    private fun showMessage() {
        Log.d(TAG, scooter.toString())
    }
}
