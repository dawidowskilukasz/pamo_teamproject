package com.example.workgood.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.workgood.R
import com.example.workgood.databinding.FragmentSettingsBinding
import com.example.workgood.ui.take_photo.TakePhotoActivity
import java.util.Locale

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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this)[SettingsViewModel::class.java]

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // this is commented out as we no longer have this binding
        // but it is a guideline of how to use such bindings:
//        val textView: TextView = binding.textSettings
//        dashboardViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        binding.setAlarmButton.setOnClickListener {
            val intent = Intent(requireContext(), SetAlarmActivity::class.java)
            startActivity(intent)
        }
        loadSavedTimes()

        // Placeholder stop button logic
        //TODO: Workout how to implement compare logic
        binding.stopAlarmButton.setOnClickListener {
            val stopIntent = Intent(context, StartAlarmService::class.java).apply {
                action = STOP_ALARM_ACTION
            }
            requireContext().stopService(stopIntent)
        }

        binding.setMainPhoto.setOnClickListener{
            val intent = Intent(requireContext(), TakePhotoActivity::class.java)
            startActivity(intent)
        }


        return root
    }

    override fun onResume() {
        super.onResume()

        loadSavedTimes()
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}