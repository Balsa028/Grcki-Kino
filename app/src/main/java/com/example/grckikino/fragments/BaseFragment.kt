package com.example.grckikino.fragments

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.grckikino.api.RetrofitInstance
import com.example.grckikino.fragments.helper.ProgressDialogFragment
import com.example.grckikino.repository.DrawingsRepository
import com.example.grckikino.viewmodels.DrawingsViewModelFactory

open class BaseFragment : Fragment() {

    private var progressDialog: ProgressDialogFragment? = null

    private val repository: DrawingsRepository by lazy {
        DrawingsRepository(RetrofitInstance.api)
    }

    protected val viewModelFactory: DrawingsViewModelFactory by lazy {
        DrawingsViewModelFactory(repository)
    }

    protected fun showProcessing() {
        if (progressDialog == null)
            progressDialog = ProgressDialogFragment()

        progressDialog!!.show(parentFragmentManager, "progress_dialog")
    }

    protected fun stopProcessing() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    protected fun showAlertDialog(title: String, message: String, buttonText: String) {
        AlertDialog.Builder(requireActivity())
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(buttonText) { dialogInterface, _ -> dialogInterface.dismiss() }
            .create().show()
    }

}