package dk.itu.moapd.scootersharing.phimo.fragments

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dk.itu.moapd.scootersharing.phimo.R
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentMapBinding
import dk.itu.moapd.scootersharing.phimo.helpers.BOUNDARY_METERS
import dk.itu.moapd.scootersharing.phimo.helpers.distanceInKilometers
import dk.itu.moapd.scootersharing.phimo.helpers.showError
import dk.itu.moapd.scootersharing.phimo.models.Scooter
import dk.itu.moapd.scootersharing.phimo.services.LocationService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val ZOOM_LEVEL = 12f

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var googleMap: GoogleMap
    private val boundaryCircles = mutableListOf<Circle>()

    private lateinit var locationServiceConn: ServiceConnection
    private val locationServiceDeferred = CompletableDeferred<LocationService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        locationServiceConn = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                val customBinder = binder as LocationService.ServiceBinder
                locationServiceDeferred.complete(customBinder.getService())
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }

        binding = FragmentMapBinding.inflate(layoutInflater, container, false)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.google_maps) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database.reference.child("scooters")
            .orderByChild("createdAt")
            .get()
            .addOnSuccessListener { snapshot ->
                val scooters = mutableMapOf<String, Scooter>()

                for (childSnapshot in snapshot.children) {
                    val scooter = childSnapshot.getValue(Scooter::class.java)

                    childSnapshot.key?.let { scooterId ->
                        if (scooter != null) {
                            scooters[scooterId] = scooter
                        }
                    }
                }

                setMapMarkers(scooters)
            }.addOnFailureListener {
                showDatabaseError()
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        this.googleMap = googleMap
        googleMap.isMyLocationEnabled = true

        googleMap.setOnMarkerClickListener { marker ->
            val scooterId = marker.tag as String
            val bundle = bundleOf("scooter_id" to scooterId)
            Navigation.findNavController(requireView())
                .navigate(R.id.action_mapFragment_to_startRideFragment, bundle)
            true
        }

        googleMap.setOnCameraMoveListener {
            updateBoundaryCircles()
        }

        lifecycleScope.launch {
            val locationService = locationServiceDeferred.await()

            locationService.lastLatitude?.let { latitude ->
                locationService.lastLongitude?.let { longitude ->
                    val location = LatLng(latitude, longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM_LEVEL))
                }
            }
        }
    }

    private fun setMapMarkers(scooters: Map<String, Scooter>) {
        lifecycleScope.launch {
            waitForGoogleMap()
            googleMap.clear()
            boundaryCircles.clear()

            scooters.forEach { entry ->
                val scooterId = entry.key
                val scooter = entry.value

                val location = LatLng(scooter.latitude, scooter.longitude)
                val options = MarkerOptions()
                    .position(location)
                val marker = googleMap.addMarker(options)
                marker?.tag = scooterId

                val boundaryCenter =
                    LatLng(scooter.initialLatitude, scooter.initialLongitude)
                val circleOptions = CircleOptions()
                    .center(boundaryCenter)
                    .radius(BOUNDARY_METERS)
                    .strokeWidth(2f)
                    .strokeColor(Color.RED)
                    .fillColor(Color.argb(50, 255, 0, 0))
                    .visible(false)
                val circle = googleMap.addCircle(circleOptions)
                boundaryCircles.add(circle)
            }

            updateBoundaryCircles()
        }
    }

    private fun updateBoundaryCircles() {
        val centerLocation = googleMap.cameraPosition.target

        var closestCircle: Circle? = null
        var smallestDistance = Float.MAX_VALUE

        for (circle in boundaryCircles) {
            val circleCenterLocation = circle.center

            val distance = (distanceInKilometers(
                centerLocation.latitude,
                centerLocation.longitude,
                circleCenterLocation.latitude,
                circleCenterLocation.longitude
            ) * 1000).toFloat()

            if (distance < smallestDistance) {
                closestCircle = circle
                smallestDistance = distance
            }
        }

        for (circle in boundaryCircles) {
            if (circle != closestCircle) {
                circle.isVisible = false
            }
        }

        closestCircle?.isVisible = true
    }

    private suspend fun waitForGoogleMap() {
        while (!::googleMap.isInitialized) {
            delay(100)
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
        showError("Unable to load scooters from database.")
    }
}
