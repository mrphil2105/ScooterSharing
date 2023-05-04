package dk.itu.moapd.scootersharing.phimo.helpers

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.showError(message: String, indefinite: Boolean = false) {
    Snackbar.make(
        requireView(),
        message,
        if (indefinite) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_LONG
    ).show()
}
