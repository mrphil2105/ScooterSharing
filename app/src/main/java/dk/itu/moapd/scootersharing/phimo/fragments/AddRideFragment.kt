package dk.itu.moapd.scootersharing.phimo.fragments

import android.content.*
import android.location.Geocoder
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentAddRideBinding
import dk.itu.moapd.scootersharing.phimo.models.Scooter
import dk.itu.moapd.scootersharing.phimo.services.LocationService

private const val ITU_LATITUDE = 55.6598883
private const val ITU_LONGITUDE = 12.59119

class AddRideFragment : Fragment() {
    private lateinit var binding: FragmentAddRideBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var geocoder: Geocoder

    private lateinit var locationServiceConn: ServiceConnection
    private var locationService: LocationService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        broadcastManager = LocalBroadcastManager.getInstance(requireContext())
        geocoder = Geocoder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        locationServiceConn = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                val customBinder = binder as LocationService.ServiceBinder
                locationService = customBinder.getService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                locationService = null
            }
        }

        binding = FragmentAddRideBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            addUpdateRide.setOnClickListener {
                if (scooterName.text.isEmpty()) {
                    return@setOnClickListener
                }

                val name = scooterName.text.toString().trim()
                addScooter(name)
                Navigation.findNavController(view).popBackStack()
            }
        }
    }

    private fun addScooter(name: String) {
        val scooter = Scooter(
            name,
            locationService?.lastLatitude ?: ITU_LATITUDE,
            locationService?.lastLongitude ?: ITU_LONGITUDE,
            System.currentTimeMillis()
        )

        auth.currentUser?.let { user ->
            database.reference.child("scooters")
                .child(user.uid)
                .push()
                .setValue(scooter)
        }
    }

    override fun onResume() {
        super.onResume()

        val intent = Intent(requireContext(), LocationService::class.java)
        requireActivity().bindService(intent, locationServiceConn, Context.BIND_AUTO_CREATE)
    }

    override fun onPause() {
        super.onPause()

        requireActivity().unbindService(locationServiceConn)
    }
}
