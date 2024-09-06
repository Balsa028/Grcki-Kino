package com.example.grckikino.fragments.helper

import android.app.Dialog
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ProgressDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val progressBar = ProgressBar(requireActivity())
        progressBar.isIndeterminate = true

        return AlertDialog.Builder(requireActivity())
            .setView(progressBar)
            .setCancelable(false)
            .create()
    }

}