package dk.itu.moapd.scootersharing.phimo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dk.itu.moapd.scootersharing.phimo.models.Scooter
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentRidesBinding

class StartRideFragment : Fragment() {
    private lateinit var ridesBinding: FragmentRidesBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        ridesBinding = FragmentRidesBinding.inflate(layoutInflater, container, false)
        return ridesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(ridesBinding) {
            startUpdateRideButton.setOnClickListener {
                if (editTextName.text.isNotEmpty() && editTextLocation.text.isNotEmpty()) {
                    val name = editTextName.text.toString().trim()
                    val location = editTextLocation.text.toString().trim()
                    val scooter = Scooter(name, location, System.currentTimeMillis())

                    auth.currentUser?.let { user ->
                        val uid = database.reference.child("scooters")
                            .child(user.uid)
                            .push()
                            .key

                        uid?.let {
                            database.reference.child("scooters")
                                .child(user.uid)
                                .child(it)
                                .setValue(scooter)
                        }
                    }

                    showMessage(scooter, view)
                    Navigation.findNavController(view).popBackStack()
                }
            }

            editTextName.isEnabled = true
        }
    }

    private fun showMessage(scooter: Scooter, view: View) {
        val message = "Ride started using scooter ${scooter.name} at location ${scooter.location}."
        val snackbar = Snackbar.make(view, message, BaseTransientBottomBar.LENGTH_SHORT)
        snackbar.show()
    }
}
