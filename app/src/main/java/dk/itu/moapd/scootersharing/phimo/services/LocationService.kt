package dk.itu.moapd.scootersharing.phimo.services

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*

class LocationService : Service() {
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private val binder = ServiceBinder()

    override fun onCreate() {
        super.onCreate()

        val broadcastManager = LocalBroadcastManager.getInstance(this)

        locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val intent = Intent("location_result")
                    intent.putExtra("latitude", location.latitude)
                    intent.putExtra("longitude", location.longitude)
                    broadcastManager.sendBroadcast(intent)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return START_NOT_STICKY
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10).build()

        locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class ServiceBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }
}
