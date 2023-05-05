package dk.itu.moapd.scootersharing.phimo.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Geocoder
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dk.itu.moapd.scootersharing.phimo.R
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentStartRideBinding
import dk.itu.moapd.scootersharing.phimo.helpers.BOUNDARY_KILOMETERS
import dk.itu.moapd.scootersharing.phimo.helpers.distanceInKilometers
import dk.itu.moapd.scootersharing.phimo.helpers.getAddressString
import dk.itu.moapd.scootersharing.phimo.helpers.showError
import dk.itu.moapd.scootersharing.phimo.models.Scooter
import dk.itu.moapd.scootersharing.phimo.services.LocationService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartRideFragment : Fragment() {
    private lateinit var binding: FragmentStartRideBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var geocoder: Geocoder

    private lateinit var locationServiceConn: ServiceConnection

    private var locationService: LocationService? = null

    private lateinit var scooterId: String
    private var newLastPhoto: String? = null
    private var scooter: Scooter? = null

    private val canRent: Boolean
        get() {
            scooter?.let { scooter ->
                return scooter.rentedBy == null
            }

            return false
        }

    private val isRentedByUser: Boolean
        get() {
            auth.currentUser?.let { user ->
                scooter?.let { scooter ->
                    return scooter.rentedBy == user.uid
                }
            }

            return false
        }

    private val isRentedByOther: Boolean
        get() {
            auth.currentUser?.let { user ->
                scooter?.let { scooter ->
                    return scooter.rentedBy != null && scooter.rentedBy != user.uid
                }
            }

            // We cannot be sure if it is rented by another user or not.
            return true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        geocoder = Geocoder(requireContext())

        val args = requireArguments()
        scooterId = args.getString("scooter_id")!!
        newLastPhoto = args.getString("last_photo")

        database.reference.child("scooters")
            .child(scooterId)
            .get()
            .addOnSuccessListener { snapshot ->
                val value = snapshot.getValue(Scooter::class.java)

                if (value != null) {
                    scooter = value
                    updateUserInterface()
                } else {
                    showDatabaseError()
                }
            }.addOnFailureListener {
                showDatabaseError()
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
                    locationService?.let { service ->
                        service.lastLatitude?.let { latitude ->
                            service.lastLongitude?.let { longitude ->
                                val distance = distanceInKilometers(
                                    latitude,
                                    longitude,
                                    initialLatitude,
                                    initialLongitude
                                )

                                if (distance > BOUNDARY_KILOMETERS) {
                                    showError(
                                        "You are outside the scooter's boundary radius of $BOUNDARY_KILOMETERS" +
                                                " km. To see your current location go to the Rides Map."
                                    )
                                    return@setOnClickListener
                                }
                            }
                        }
                    }

                    if (canRent) {
                        updateScooter()
                    } else if (!isRentedByOther) {
                        val bundle = bundleOf("scooter_id" to scooterId)
                        Navigation.findNavController(requireView())
                            .navigate(R.id.action_startRideFragment_to_cameraFragment, bundle)
                    }
                }
            }
        }

        // In case we've gotten back here via the back stack, then 'onCreate' has not been called.
        // So we need to manually update the UI, but thankfully in this case 'scooter' is not null.
        // I.e. if 'scooter' is not null, we got here via the back stack, so the fragment is reused.
        if (scooter != null) {
            updateUserInterface()
        }

        // This is such a messy way to update the scooter after taking a picture.
        // But I couldn't find a cleaner way to do it without also adding more verbose code.
        // ActivityResultContracts and Fragment Results and what not.
        // So I just used what I already know - the Navigation component with arguments on navigate.
        // If I had more time I could've improved this.
        if (newLastPhoto != null) {
            lifecycleScope.launch {
                // It is very likely null here. More ugly code :(
                while (scooter == null) {
                    delay(100)
                }

                updateScooter()
            }
        }
    }

    private fun updateScooter() {
        val userId = auth.currentUser?.uid ?: return

        scooter?.apply {
            rentedBy = if (isRentedByUser) null else userId
            timestamp = System.currentTimeMillis()

            newLastPhoto?.let { lastPhoto ->
                this.lastPhoto = lastPhoto
            }

            // If any of these are null then the location will not be updated.
            locationService?.let { service ->
                service.lastLatitude?.let { latitude ->
                    service.lastLongitude?.let { longitude ->
                        this.latitude = latitude
                        this.longitude = longitude
                    }
                }
            }

            database.reference.child("scooters")
                .child(scooterId)
                .setValue(this)

            updateUserInterface()
        }
    }

    private fun updateUserInterface() {
        updateListItem()
        updateRideButton()
        updateLastPhotoView()
    }

    private fun updateRideButton() {
        with(binding) {
            startStopRide.isEnabled = !isRentedByOther
            startStopRide.text = getString(
                if (canRent) R.string.start_ride_button
                else R.string.stop_ride_button
            )
        }
    }

    private fun updateListItem() {
        val scooter = scooter ?: return

        with(binding.listItem) {
            scooterName.text = scooter.name
            scooterTimestamp.text = scooter.getTime()

            geocoder.getAddressString(scooter.latitude, scooter.longitude) { addressString ->
                scooterAddress.text = addressString
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

    private fun updateLastPhotoView() {
        val scooter = scooter ?: return

        scooter.lastPhoto?.let { fileName ->
            val imageRef = storage.reference.child("photos/$fileName")

            with(binding) {
                imageRef.downloadUrl.addOnSuccessListener {
                    Glide.with(requireContext())
                        .load(it)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(lastPhoto)
                }

                lastPhoto.visibility = View.VISIBLE
                lastPhotoMissing.visibility = View.GONE
            }
        } ?: with(binding) {
            lastPhoto.visibility = View.GONE
            lastPhotoMissing.visibility = View.VISIBLE
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

    private fun showDatabaseError() {
        showError("Unable to load scooter from database.")
    }
}
