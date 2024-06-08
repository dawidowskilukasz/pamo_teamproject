package com.example.workgood.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.workgood.R
import com.example.workgood.databinding.FragmentSettingsBinding
import com.example.workgood.ui.take_photo.TakePhotoActivity
import java.io.File
import java.util.Locale

/**
 * A Fragment subclass representing the settings screen of the application.
 * It allows users to set alarm times and stop alarms or navigate to take photo screen.
 */
class SettingsFragment : Fragment() {
    companion object {
        const val ALARM_PREFS_KEY = "alarm_prefs"
        const val START_HOUR_KEY = "start_hour"
        const val START_MINUTE_KEY = "start_minute"
        const val END_HOUR_KEY = "end_hour"
        const val END_MINUTE_KEY = "end_minute"
        const val STOP_ALARM_ACTION = "STOP_ALARM_ACTION"
    }

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    /**
     * Called to have the fragment instantiate.
     * Initializes the settings ViewModel, sets up UI components and click listeners.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null if the fragment does not provide a UI.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this)[SettingsViewModel::class.java]

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.setAlarmButton.setOnClickListener {
            val intent = Intent(requireContext(), SetAlarmActivity::class.java)
            startActivity(intent)
        }
        loadSavedTimes()

        binding.setMainPhoto.setOnClickListener {
            val dir = File(Environment.getExternalStorageDirectory(), "/Pictures/WorkGoodApp")
            if (dir.exists() && dir.isDirectory) {
                val imageFiles =
                    dir.listFiles { file -> file.extension == "jpg" || file.extension == "png" }
                for (imageFile in imageFiles!!) {
                    imageFile.delete()
                }
            }
            val intent = Intent(requireContext(), TakePhotoActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    /**
     * Reloads any saved times when returning to the fragment.
     */
    override fun onResume() {
        super.onResume()

        loadSavedTimes()
    }

    /**
     * Loads saved start and end times of the alarm from shared preferences
     * and updates the corresponding UI elements.
     */
    private fun loadSavedTimes() {
        val sharedPreferences =
            requireContext().getSharedPreferences(ALARM_PREFS_KEY, Context.MODE_PRIVATE)
        val startHour = sharedPreferences.getInt(START_HOUR_KEY, -1)
        val startMinute = sharedPreferences.getInt(START_MINUTE_KEY, -1)
        val endHour = sharedPreferences.getInt(END_HOUR_KEY, -1)
        val endMinute = sharedPreferences.getInt(END_MINUTE_KEY, -1)

        val defaultLocale = Locale.getDefault()

        if (startHour != -1 && startMinute != -1) {
            val startTimeFormat = resources.getString(R.string.start_time_format)
            binding.startTimeText.text =
                String.format(defaultLocale, startTimeFormat, startHour, startMinute)
        }

        if (endHour != -1 && endMinute != -1) {
            val endTimeFormat = resources.getString(R.string.end_time_format)
            binding.endTimeText.text =
                String.format(defaultLocale, endTimeFormat, endHour, endMinute)
        }
    }

    /**
     * Cleans up any resources that will not be needed after the view is destroyed.
     * It also resets the _binding variable to null.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}