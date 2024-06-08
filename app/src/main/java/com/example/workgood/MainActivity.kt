package com.example.workgood

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.workgood.databinding.ActivityMainBinding
import com.example.workgood.ui.take_photo.TakePhotoActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.opencv.android.OpenCVLoader
import android.util.Log

/**
 * MainActivity acts as the entry point for the application and the host for its primary navigation components.
 * It initializes and sets up the bottom navigation and the app bar configuration.
 * It also handles the initialization of OpenCV library.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /**
     * Called when the activity is starting.
     * This method performs basic application startup logic that should happen only once for the entire life of the activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     * then this Bundle contains the data most recently supplied in onSaveInstanceState(Bundle).
     * The onSaveInstanceState(Bundle) is null if the activity has never existed before.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "OpenCV initialized successfully")
        } else {
            Log.d("OpenCV", "OpenCV initialization failed")
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView


        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_settings, R.id.navigation_take_photo
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_take_photo -> {
                    val intent = Intent(this, TakePhotoActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> {
                    navController.navigate(item.itemId)
                    true
                }
            }
        }
    }
}