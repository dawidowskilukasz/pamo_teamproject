package com.example.workgood.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.workgood.R
import com.example.workgood.databinding.FragmentHomeBinding
import java.util.Calendar
import java.util.Locale

/**
 * A Fragment subclass representing the home screen of the application.
 * It manages the display of the countdown timer.
 */
class HomeFragment : Fragment() {
    companion object {
        const val ALARM_PREFS_KEY = "alarm_prefs"
        const val END_HOUR_KEY = "end_hour"
        const val END_MINUTE_KEY = "end_minute"
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    /**
     * Called to have the fragment instantiate.
     * Initializes the home ViewModel, timer text and updating timer handler.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        val defaultTimerText = getString(R.string.default_timer_text)
        homeViewModel.updateTimerText(defaultTimerText)

        val timerTextView: TextView = binding.timerTextView

        homeViewModel.text.observe(viewLifecycleOwner) { timerText ->
            timerTextView.text = timerText
        }

        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                updateTimer()
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)

        return root
    }

    /**
     * Clean up any resources that will not be needed after the view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacks(runnable)
    }

    /**
     * Updates the timer display with the time remaining until the alarm is set.
     * If the alarm time is past the current time, a message indicating the end of the time is displayed.
     */
    private fun updateTimer() {
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences(ALARM_PREFS_KEY, Context.MODE_PRIVATE)
        val endHour = sharedPreferences.getInt(END_HOUR_KEY, 0)
        val endMinute = sharedPreferences.getInt(END_MINUTE_KEY, 0)

        val currentTime = Calendar.getInstance()
        val nextAlarmTime = Calendar.getInstance()

        nextAlarmTime.set(Calendar.HOUR_OF_DAY, endHour)
        nextAlarmTime.set(Calendar.MINUTE, endMinute)

        val remainingTimeMillis = nextAlarmTime.timeInMillis - currentTime.timeInMillis
        if (remainingTimeMillis > 0) {
            val hours = remainingTimeMillis / (1000 * 60 * 60)
            val minutes = (remainingTimeMillis / (1000 * 60)) % 60
            val timeFormat = resources.getString(R.string.time_format)
            val prefix = getString(R.string.timer_prefix)

            val timerText = String.format(Locale.getDefault(), timeFormat, hours, minutes)
            homeViewModel.updateTimerText("$prefix $timerText")
        } else {
            val endTimerText = getString(R.string.end_timer_text)
            homeViewModel.updateTimerText(endTimerText)

        }
    }
}


