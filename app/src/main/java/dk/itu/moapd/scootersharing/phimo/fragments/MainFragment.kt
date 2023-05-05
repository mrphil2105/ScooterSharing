package dk.itu.moapd.scootersharing.phimo.fragments

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.phimo.R
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentMainBinding
import dk.itu.moapd.scootersharing.phimo.helpers.requestUserPermissions
import dk.itu.moapd.scootersharing.phimo.helpers.showError
import dk.itu.moapd.scootersharing.phimo.services.LocationService

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

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
                showError(
                    "You must grant location permission for the app to function properly.",
                    true
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            addRide.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(R.id.action_mainFragment_to_addRideFragment)
            }

            listRides.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(R.id.action_mainFragment_to_rideListFragment)
            }

            ridesMap.setOnClickListener {
                Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_mapFragment)
            }

            scanQr.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(R.id.action_mainFragment_to_qrScanFragment)
            }

            signOut.setOnClickListener {
                auth.signOut()
                Navigation.findNavController(view)
                    .navigate(R.id.action_mainFragment_to_loginActivity)
            }
        }
    }
}
