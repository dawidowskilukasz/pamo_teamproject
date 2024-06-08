package com.example.workgood.ui.take_photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel for the TakePhotoFragment.
 * It stores and manages UI-related data in a lifecycle-conscious way, allowing data to survive
 * configuration changes such as screen rotations. The sample text is exposed as LiveData
 * to be observed by the associated fragment.
 */
class TakePhotoViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Take Photo Fragment"
    }
    val text: LiveData<String> = _text
}