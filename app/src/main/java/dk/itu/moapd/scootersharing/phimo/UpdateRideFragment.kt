package dk.itu.moapd.scootersharing.phimo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentRidesBinding

class UpdateRideFragment : Fragment() {
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
                    val location = editTextLocation.text.toString().trim()
                    ridesDB.updateCurrentScooter(location)

                    Navigation.findNavController(view).popBackStack()
                }
            }

            val scooter = ridesDB.getCurrentScooter()
            editTextName.setText(scooter.name)
            editTextLocation.setText(scooter.location)
            startUpdateRideButton.setText(R.string.update_ride_button)
        }
    }
}
