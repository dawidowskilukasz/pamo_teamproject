package com.example.workgood.ui.take_photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.workgood.databinding.FragmentTakePhotoBinding

/**
 * A Fragment subclass used for taking photos within the app.
 * It provides UI functionality for initiating the photo-taking process
 * and displaying relevant information or actions related to the task.
 */
class TakePhotoFragment : Fragment() {

    private var _binding: FragmentTakePhotoBinding? = null

    private val binding get() = _binding!!

    /**
     * Called to inflate the fragment's view and setup its binding.
     * Initializes the ViewModel associated with this fragment and sets up observers
     * for any LiveData that the fragment should respond to, such as text to display.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-created from a previous saved state.
     * @return The newly created view for the fragment or null if the fragment does not provide a UI.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(TakePhotoViewModel::class.java)

        _binding = FragmentTakePhotoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textTakePhoto
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
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