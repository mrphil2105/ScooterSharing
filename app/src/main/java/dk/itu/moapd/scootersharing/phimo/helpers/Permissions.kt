package dk.itu.moapd.scootersharing.phimo.helpers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions

private const val ALL_PERMISSIONS_RESULT = 1011;

fun requestUserPermissions(activity: Activity) {
    val permissions: ArrayList<String> = ArrayList()
    permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
    permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

    val permissionsToRequest = permissionsToRequest(activity, permissions)

    if (permissionsToRequest.size > 0) {
        requestPermissions(activity, permissionsToRequest.toTypedArray(), ALL_PERMISSIONS_RESULT)
    }
}

private fun permissionsToRequest(context: Context, permissions: List<String>): ArrayList<String> {
    val result: ArrayList<String> = ArrayList()
    for (permission in permissions) {
        if (ActivityCompat.checkSelfPermission(
                context, permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            result.add(permission)
        }
    }
    return result
}
