package com.example.grckikino.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.grckikino.repository.DrawingsRepository

class DrawingsViewModelFactory(private val repository: DrawingsRepository, private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(DrawingsViewModel::class.java) -> DrawingsViewModel(repository) as T
            modelClass.isAssignableFrom(DrawingDetailsViewModel::class.java) -> DrawingDetailsViewModel(repository, application) as T
            modelClass.isAssignableFrom(DrawingResultViewModel::class.java) -> DrawingResultViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }

}