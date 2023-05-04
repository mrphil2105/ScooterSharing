package dk.itu.moapd.scootersharing.phimo.fragments

import android.content.ComponentName
import android.content.ServiceConnection
import android.location.Geocoder
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dk.itu.moapd.scootersharing.phimo.R
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentStartRideBinding
import dk.itu.moapd.scootersharing.phimo.helpers.getAddressString
import dk.itu.moapd.scootersharing.phimo.models.Scooter
import dk.itu.moapd.scootersharing.phimo.services.LocationService

class StartRideFragment : Fragment() {
    private lateinit var binding: FragmentStartRideBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var geocoder: Geocoder

    private lateinit var locationServiceConn: ServiceConnection

    private var locationService: LocationService? = null

    private lateinit var uid: String
    private var scooter: Scooter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        geocoder = Geocoder(requireContext())

        val args = requireArguments()
        uid = args.getString("uid")!!

        auth.currentUser?.let { user ->
            database.reference.child("scooters")
                .child(user.uid)
                .child(uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    val value = snapshot.getValue(Scooter::class.java)

                    if (value != null) {
                        scooter = value
                        updateListItem(value)
                        updateRideButton()
                    } else {
                        showDatabaseError()
                    }
                }.addOnFailureListener {
                    showDatabaseError()
                }
        }
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

        binding = FragmentStartRideBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            startStopRide.setOnClickListener {
                scooter?.apply {
                    active = !active
                    timestamp = System.currentTimeMillis()

                    // If any of these are null then the location will not be updated.
                    locationService?.let { service ->
                        service.lastLatitude?.let { latitude ->
                            service.lastLongitude?.let { longitude ->
                                this.latitude = latitude
                                this.longitude = longitude
                            }
                        }
                    }

                    auth.currentUser?.let { user ->
                        database.reference.child("scooters")
                            .child(user.uid)
                            .child(uid)
                            .setValue(this)
                    }

                    updateListItem(this)
                    updateRideButton()
                }
            }
        }
    }

    private fun updateRideButton() {
        with(binding) {
            startStopRide.isEnabled = true
            startStopRide.text = getString(
                if (scooter!!.active) R.string.stop_ride_button
                else R.string.start_ride_button
            )
        }
    }

    private fun updateListItem(scooter: Scooter) {
        // TODO: Fix the code duplication between here and ScooterAdapter (maybe by using a Fragment that updates itself).

        with(binding.listItem) {
            scooterName.text = scooter.name
            scooterTimestamp.text = scooter.getTime()

            scooter.latitude?.let { latitude ->
                scooter.longitude?.let { longitude ->
                    geocoder.getAddressString(latitude, longitude) { addressString ->
                        scooterAddress.text = addressString
                    }
                }
            }

            val imageRef = storage.reference.child(scooter.image ?: "generic_scooter.png")

            imageRef.downloadUrl.addOnSuccessListener {
                Glide.with(requireContext())
                    .load(it)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .fitCenter()
                    .into(scooterImage)
            }
        }
    }

    private fun showDatabaseError() {
        Snackbar.make(
            requireView(), "Unable to load scooter from database.", Snackbar.LENGTH_LONG
        ).show()
    }
}
