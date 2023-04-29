package dk.itu.moapd.scootersharing.phimo.fragments

import android.content.*
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.phimo.R
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentMainBinding
import dk.itu.moapd.scootersharing.phimo.helpers.requestUserPermissions
import dk.itu.moapd.scootersharing.phimo.helpers.toAddressString
import dk.itu.moapd.scootersharing.phimo.services.LocationService
import java.util.*

class MainFragment : Fragment() {
    private lateinit var mainBinding: FragmentMainBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var geocoder: Geocoder

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            setAddress(latitude, longitude)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        broadcastManager = LocalBroadcastManager.getInstance(requireContext())
        geocoder = Geocoder(requireContext(), Locale.getDefault())
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

    override fun onResume() {
        super.onResume()

        requestUserPermissions(requireActivity())

        requireActivity().startService(Intent(requireContext(), LocationService::class.java))

        val filter = IntentFilter("location_result")
        broadcastManager.registerReceiver(locationReceiver, filter)
    }

    override fun onPause() {
        super.onPause()

        requireActivity().stopService(Intent(requireContext(), LocationService::class.java))

        broadcastManager.unregisterReceiver(locationReceiver)
    }

    private fun setAddress(latitude: Double, longitude: Double) {
        if (Build.VERSION.SDK_INT >= 33) {
            val geocodeListener = Geocoder.GeocodeListener { addresses ->
                addresses.firstOrNull()?.toAddressString()?.let { address ->
                    with(mainBinding) {
                        addressTextView.text = address
                    }
                }
            }
            geocoder.getFromLocation(latitude, longitude, 1, geocodeListener)
        } else {
            geocoder.getFromLocation(latitude, longitude, 1)?.let { addresses ->
                addresses.firstOrNull()?.toAddressString()?.let { address ->
                    with(mainBinding) {
                        addressTextView.text = address
                    }
                }
            }
        }
    }
}
