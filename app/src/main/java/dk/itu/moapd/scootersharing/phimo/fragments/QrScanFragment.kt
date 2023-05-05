package dk.itu.moapd.scootersharing.phimo.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import dk.itu.moapd.scootersharing.phimo.R
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentQrScanBinding
import dk.itu.moapd.scootersharing.phimo.helpers.requestUserPermissions
import dk.itu.moapd.scootersharing.phimo.helpers.showError
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class QrScanFragment : Fragment() {
    private lateinit var binding: FragmentQrScanBinding

    private val scooterIdDeferred = CompletableDeferred<String>()
    private lateinit var cameraPreview: Preview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissions: ArrayList<String> = ArrayList()
        permissions.add(Manifest.permission.CAMERA)

        requestUserPermissions(permissions) { success ->
            if (success) {
                setUpCamera()
            } else {
                showError("You must grant camera permission to scan QR Codes.", true)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrScanBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val scooterId = scooterIdDeferred.await()
            val navController = Navigation.findNavController(requireView())
            val bundle = bundleOf("scooter_id" to scooterId)
            val options = NavOptions.Builder()
                // Prevent the user from going back to the QrScanFragment.
                .setPopUpTo(navController.currentDestination!!.id, true)
                .build()

            navController.navigate(R.id.action_qrScanFragment_to_startRideFragment, bundle, options)
        }
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraPreview = Preview.Builder().build()

            val cameraExecutor = Executors.newSingleThreadExecutor()
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor, QRCodeAnalyzer { scooterId ->
                scooterIdDeferred.complete(scooterId)
            })

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, cameraPreview, imageAnalysis
                )
                cameraPreview.setSurfaceProvider(binding.photoPreview.surfaceProvider)
            } catch (e: Exception) {
                showError("An error has occurred when setting up camera preview.", true)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // The analyzer below is a direct copy paste from ChatGPT.
    private class QRCodeAnalyzer(private val listener: (String) -> Unit) : ImageAnalysis.Analyzer {
        private val reader = MultiFormatReader()

        override fun analyze(image: ImageProxy) {
            val buffer = image.planes[0].buffer
            val data = ByteArray(buffer.remaining())
            buffer.get(data)

            val source = PlanarYUVLuminanceSource(
                data,
                image.width,
                image.height,
                0,
                0,
                image.width,
                image.height,
                false
            )

            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

            val result = try {
                reader.decode(binaryBitmap)
            } catch (e: Exception) {
                null
            }

            result?.let {
                listener.invoke(it.text)
            }

            image.close()
        }
    }
}
