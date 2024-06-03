package com.example.workgood.ui.take_photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.workgood.databinding.FragmentTakePhotoBinding

class TakePhotoFragment : Fragment() {

    private var _binding: FragmentTakePhotoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}