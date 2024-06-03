package com.example.workgood.ui.take_photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TakePhotoViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Take Photo Fragment"
    }
    val text: LiveData<String> = _text
}