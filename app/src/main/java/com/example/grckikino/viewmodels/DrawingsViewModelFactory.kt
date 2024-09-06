package com.example.grckikino.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.grckikino.repository.DrawingsRepository

class DrawingsViewModelFactory(private val repository: DrawingsRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(DrawingsViewModel::class.java) -> DrawingsViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }

}