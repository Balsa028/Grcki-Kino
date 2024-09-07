package com.example.grckikino.fragments

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.grckikino.api.RetrofitInstance
import com.example.grckikino.fragments.helper.ProgressDialogFragment
import com.example.grckikino.interfaces.BaseCoordinator
import com.example.grckikino.repository.DrawingsRepository
import com.example.grckikino.viewmodels.DrawingsViewModelFactory

open class BaseFragment : Fragment() {

    protected var coordinator: BaseCoordinator? = null
    protected var selectedNumbers: List<Int> = emptyList()
    private var progressDialog: ProgressDialogFragment? = null

    private val repository: DrawingsRepository by lazy {
        DrawingsRepository(RetrofitInstance.api)
    }

    protected val viewModelFactory: DrawingsViewModelFactory by lazy {
        DrawingsViewModelFactory(repository)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseCoordinator)
            this.coordinator = context
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