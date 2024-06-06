package com.example.workgood.ui.take_photo

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.workgood.R

class FullScreenImageDialog(private val imageUri: Uri, private val onSave: () -> Unit, private val onDiscard: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_full_screen_image, null)

        val imageView = view.findViewById<ImageView>(R.id.imageView)
        imageView.setImageURI(imageUri)

        val builder = AlertDialog.Builder(requireContext(), R.style.FullScreenDialog)
            .setView(view)
            .setTitle("Do you want to save this photo?")
            .setPositiveButton("Save") { _, _ -> onSave() }
            .setNegativeButton("Discard") { _, _ -> onDiscard() }

        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}