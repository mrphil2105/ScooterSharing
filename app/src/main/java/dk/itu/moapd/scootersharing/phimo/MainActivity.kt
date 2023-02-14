package dk.itu.moapd.scootersharing.phimo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dk.itu.moapd.scootersharing.phimo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    private lateinit var mainBinding: ActivityMainBinding

    private val scooter: Scooter = Scooter("", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)

        with(mainBinding) {
            startRideButton.setOnClickListener { view ->
                if (editTextName.text.isNotEmpty() && editTextLocation.text.isNotEmpty()) {
                    val name = editTextName.text.toString().trim()
                    val location = editTextLocation.text.toString().trim()

                    scooter.name = name;
                    scooter.location = location;

                    editTextName.text.clear()
                    editTextLocation.text.clear()

                    showMessage()
                }
            }
        }

        setContentView(mainBinding.root)
    }

    private fun showMessage() {
        Log.d(TAG, scooter.toString())
    }
}
