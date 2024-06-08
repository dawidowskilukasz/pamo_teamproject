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

/**
 * A DialogFragment subclass that displays an image in full screen inside a dialog.
 * The dialog offers the user the option to save or discard the image.
 * 
 * @param imageUri The URI of the image to display.
 * @param onSave Lambda function to execute when the user opts to save the image.
 * @param onDiscarp Lambda function to execute when the user opts to discard the image.
 */
class FullScreenImageDialog(private val imageUri: Uri, private val onSave: () -> Unit, private val onDiscard: () -> Unit) : DialogFragment() {

    /**
     * Called to create the dialog. Inflates the image view layout, sets the image URI, and defines the save and discard actions.
     *
     * @param savedInstanceState If the dialog is being re-initialized after previously being shut down, 
     * this parameter provides the previous saved state, if any.
     * @return Returns a new AlertDialog with configured actions and view.
     */
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

    /**
     * Configures the dialog window to use the full screen layout size.
     */
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}