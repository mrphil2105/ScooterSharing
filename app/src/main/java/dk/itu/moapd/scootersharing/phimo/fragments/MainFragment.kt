package dk.itu.moapd.scootersharing.phimo.fragments

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.phimo.R
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentMainBinding
import dk.itu.moapd.scootersharing.phimo.helpers.requestUserPermissions
import dk.itu.moapd.scootersharing.phimo.services.LocationService

class MainFragment : Fragment() {
    private lateinit var mainBinding: FragmentMainBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val permissions: ArrayList<String> = ArrayList()
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        requestUserPermissions(permissions) { success ->
            if (success) {
                requireActivity().startService(
                    Intent(requireContext(), LocationService::class.java)
                )
            } else {
                Snackbar.make(
                    requireView(),
                    "You must grant location permission for the app to function properly.",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mainBinding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return mainBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(mainBinding) {
            startRideButton.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(R.id.action_mainFragment_to_startRideFragment)
            }

            updateRideButton.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(R.id.action_mainFragment_to_updateRideFragment)
            }

            listRidesButton.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(R.id.action_mainFragment_to_rideListFragment)
            }

            signOutButton.setOnClickListener {
                auth.signOut()
                Navigation.findNavController(view)
                    .navigate(R.id.action_mainFragment_to_loginActivity)
            }
        }
    }
}
