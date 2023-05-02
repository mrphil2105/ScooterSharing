package dk.itu.moapd.scootersharing.phimo.helpers

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

fun Fragment.requestUserPermissions(
    permissions: List<String>, callback: ((success: Boolean) -> Unit)
) {
    val permissionsToRequest = permissionsToRequest(requireContext(), permissions)

    if (permissionsToRequest.size > 0) {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
                var allGranted = true

                for (grant in grants) {
                    if (!grant.value) {
                        allGranted = false
                        break
                    }
                }

                callback(allGranted)
            }
        requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
    } else {
        callback(true)
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
