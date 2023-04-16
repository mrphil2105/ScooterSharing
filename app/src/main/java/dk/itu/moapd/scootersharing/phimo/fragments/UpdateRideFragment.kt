package dk.itu.moapd.scootersharing.phimo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dk.itu.moapd.scootersharing.phimo.R
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentRidesBinding

// TODO: Replace this with a way to edit a specific 'Scooter' object.
class UpdateRideFragment : Fragment() {
    private lateinit var ridesBinding: FragmentRidesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            startUpdateRideButton.setText(R.string.update_ride_button)
        }
    }
}
