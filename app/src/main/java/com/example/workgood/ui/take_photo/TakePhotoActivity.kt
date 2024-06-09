package com.example.workgood.ui.take_photo

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.workgood.MainActivity
import com.example.workgood.R
import com.example.workgood.databinding.ActivityTakePhotoBinding
import com.example.workgood.ui.settings.SettingsFragment
import com.example.workgood.ui.settings.StartAlarmService
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfFloat
import org.opencv.core.MatOfInt
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * An activity that controls the camera for photo capturing and image comparison.
 * It manages the camera's lifecycle, permissions, photo taking, and image comparison operations.
 * The activity also provides UI to control the camera and display the taken photo.
 */
class TakePhotoActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityTakePhotoBinding

    private var imageCapture: ImageCapture? = null

    /**
     * Used to handle the logic once the photo has been taken and saved.
     * It handles the comparison of the newly taken photo with existing photos and navigates to the
     * home fragment if a similar photo is detected or displays a message if the photos are not similar.
     */
    private lateinit var cameraExecutor: ExecutorService

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            var permissionGranted = true
            for (entry in permissions.entries) {
                println(entry)
            }
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(
                    baseContext,
                    getString(R.string.permission_request_denied),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                startCamera()
            }
        }


    private var savedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        viewBinding = ActivityTakePhotoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }

        viewBinding.backToMainActivityButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    /**
     * Captures a photo and saves it to the specified location.
     * Once the photo is taken, it is displayed in a preview.
     */
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/WorkGoodApp")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, getString(R.string.photo_capture_failed, exc.message), exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = getString(R.string.photo_capture_succeeded, output.savedUri)
                    savedImageUri = output.savedUri
                    Log.d(TAG, msg)
                    compareImagesInDirectory()
                    showImagePreview(output.savedUri)
                }
            }
        )
    }

    /**
     * Compares the recently captured image against all images within the specific directory
     * and handles logic based on whether a similar image is found.
     */
    private fun compareImagesInDirectory() {
        val dir = File(Environment.getExternalStorageDirectory(), "/Pictures/WorkGoodApp")
        if (dir.exists() && dir.isDirectory) {
            val imageFiles =
                dir.listFiles { file -> file.extension == "jpg" || file.extension == "png" }

            if (imageFiles != null && imageFiles.size >= 2) {
                for (i in imageFiles.indices) {
                    for (j in i + 1 until imageFiles.size) {
                        val imagePath1 = imageFiles[i].absolutePath
                        val imagePath2 = imageFiles[j].absolutePath
                        val areImagesSimilar = compareImages(imagePath1, imagePath2)
                        val similarityText = if (areImagesSimilar) getString(R.string.similar) else getString(R.string.not_similar)
                        imageFiles[j].delete()
                        Log.d(
                            "Image Comparison",
                            getString(R.string.images_similarity, imageFiles[i].name, imageFiles[j].name, similarityText)
                        )
                        if (areImagesSimilar) {
                            val stopIntent = Intent(this, StartAlarmService::class.java).apply {
                                action = SettingsFragment.STOP_ALARM_ACTION
                            }
                            this.stopService(stopIntent)
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                baseContext,
                                getString(R.string.photo_not_similar),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Log.d("Image Comparison", getString(R.string.not_enough_images_in_the_directory))
            }
        } else {
            Log.d("Image Comparison", getString(R.string.directory_does_not_exist))
        }
    }

    /**
     * Compares two images using OpenCV to calculate the histogram similarity.
     *
     * @param imagePath1 The file path of the first image.
     * @param imagePath2 The file path of the second image.
     * @return True if the images are considered similar based on the similarity threshold, false otherwise.
     */
    fun compareImages(imagePath1: String, imagePath2: String): Boolean {
        // Load the images
        val img1 = Imgcodecs.imread(imagePath1, Imgcodecs.IMREAD_GRAYSCALE)
        val img2 = Imgcodecs.imread(imagePath2, Imgcodecs.IMREAD_GRAYSCALE)

        if (img1.empty() || img2.empty()) {
            Log.d("OpenCV", getString(R.string.failed_to_load_images))
            return false
        }

        // Compute histograms
        val histSize = MatOfInt(256)
        val histRange = MatOfFloat(0f, 256f)
        val hist1 = Mat()
        val hist2 = Mat()

        Imgproc.calcHist(listOf(img1), MatOfInt(0), Mat(), hist1, histSize, histRange)
        Imgproc.calcHist(listOf(img2), MatOfInt(0), Mat(), hist2, histSize, histRange)

        // Normalize the histograms
        Core.normalize(hist1, hist1, 0.0, 1.0, Core.NORM_MINMAX)
        Core.normalize(hist2, hist2, 0.0, 1.0, Core.NORM_MINMAX)

        // Compare histograms
        val result = Imgproc.compareHist(hist1, hist2, Imgproc.CV_COMP_CORREL)

        // Define a threshold for similarity (1.0 means identical)
        val similarityThreshold = 0.65

        return result >= similarityThreshold
    }

    /**
     * Displays a full-screen dialog with the image preview, providing the user with options
     * to save or discard the photo.
     *
     * @param uri The URI of the image to display.
     */
    private fun showImagePreview(uri: Uri?) {
        if (uri == null) return

        val dialog = FullScreenImageDialog(uri,
            onSave = { Toast.makeText(this, getString(R.string.photo_saved_at, uri), Toast.LENGTH_SHORT).show() },
            onDiscard = {
                contentResolver.delete(uri, null, null)
                savedImageUri = null
                Toast.makeText(this, getString(R.string.photo_discarded), Toast.LENGTH_SHORT).show()
            })

        dialog.show(supportFragmentManager, "FullScreenImageDialog")
    }

    /**
     * Initializes and starts the camera with the required use cases.
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, getString(R.string.use_case_binding_failed), exc)
            }

        }, ContextCompat.getMainExecutor(this))

        imageCapture = ImageCapture.Builder().build()
    }

    /**
     * Launches an ActivityResultLauncher to request all necessary permissions.
     * Permissions include camera access and (for SDK <= P) writing to and reading from external storage.
     */
    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    /**
     * Checks if all required permissions are granted.
     *
     * @return Returns true if all required permissions are granted, false otherwise.
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Called by the system when the activity is destroyed. This is the final call that the activity receives.
     * It is used to release resources and clean up any remaining tasks to avoid memory leaks.
     * In this case, it shuts down the ExecutorService used for camera operations.
     */
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "WorkGoodApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}