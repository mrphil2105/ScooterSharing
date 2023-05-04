package dk.itu.moapd.scootersharing.phimo.fragments

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import dk.itu.moapd.scootersharing.phimo.R
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentCameraBinding
import dk.itu.moapd.scootersharing.phimo.helpers.requestUserPermissions
import dk.itu.moapd.scootersharing.phimo.helpers.showError
import java.util.*
import kotlin.collections.ArrayList

class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private lateinit var cameraPreview: Preview
    private lateinit var imageCapture: ImageCapture

    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        val args = requireArguments()
        uid = args.getString("uid")!!

        val permissions: ArrayList<String> = ArrayList()
        permissions.add(Manifest.permission.CAMERA)

        requestUserPermissions(permissions) { success ->
            if (success) {
                setUpCamera()
            } else {
                showError("You must grant camera permission to take a picture.", true)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            takePhoto.setOnClickListener {
                val metadata = StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .setCustomMetadata("timestamp", System.currentTimeMillis().toString())
                    .build()

                val fileName = UUID.randomUUID().toString()
                val imageRef = storage.reference.child("photos/$fileName")

                imageCapture.takePicture(
                    ContextCompat.getMainExecutor(requireContext()),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val buffer = image.planes[0].buffer
                            val bytes = ByteArray(buffer.capacity()).apply { buffer.get(this) }

                            imageRef.putBytes(bytes, metadata).addOnSuccessListener {
                                val navController = Navigation.findNavController(requireView())
                                val bundle = bundleOf("uid" to uid, "last_photo" to fileName)
                                val options = NavOptions.Builder()
                                    // Prevent navigation to StartRideFragment twice via back stack.
                                    .setLaunchSingleTop(true)
                                    // Prevent the user from going back to the CameraFragment.
                                    .setPopUpTo(navController.currentDestination!!.id, true)
                                    .build()

                                navController.navigate(
                                    R.id.action_cameraFragment_to_startRideFragment,
                                    bundle,
                                    options
                                )
                            }.addOnFailureListener {
                                showError("Unable to upload the photo to storage.")
                            }

                            image.close()
                        }

                        override fun onError(exception: ImageCaptureException) {
                            showError("Unable to capture photo.")
                        }
                    })
            }
        }
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraPreview = Preview.Builder().build()

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, cameraPreview, imageCapture
                )
                cameraPreview.setSurfaceProvider(binding.photoPreview.surfaceProvider)
            } catch (e: Exception) {
                showError("An error has occurred when setting up camera preview.", true)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }
}
