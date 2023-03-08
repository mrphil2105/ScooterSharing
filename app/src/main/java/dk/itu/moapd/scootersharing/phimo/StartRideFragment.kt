package dk.itu.moapd.scootersharing.phimo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentRidesBinding

class StartRideFragment : Fragment() {
    private lateinit var ridesBinding: FragmentRidesBinding

    companion object {
        lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ridesDB = RidesDB.get(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
                    ridesDB.addScooter(name, location)

                    val scooter = ridesDB.getCurrentScooter()
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
