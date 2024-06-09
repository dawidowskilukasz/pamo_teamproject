package com.example.workgood.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel associated with the HomeFragment.
 * It manages the timer text that is observed by the fragment to update the UI when the timer text changes.
 */
class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    /**
     * Updates the timer text with the new text value.
     *
     * @param newText The new text value to update the timer text with.
     */
    fun updateTimerText(newText: String) {
        _text.value = newText
    }
}
