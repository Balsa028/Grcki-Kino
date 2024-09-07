package com.example.grckikino.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grckikino.api.Result
import com.example.grckikino.models.Drawing
import com.example.grckikino.repository.DrawingsRepository
import com.example.grckikino.utils.GRCKI_LOTO_GAME_ID
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DrawingsViewModel(private val repository: DrawingsRepository) : ViewModel() {

    private var _drawingsListResponse = MutableLiveData<Result<List<Drawing>>>()
    val drawingListResponse: LiveData<Result<List<Drawing>>> = _drawingsListResponse

    private var _updatedDrawingList = MutableLiveData<List<Drawing>>()
    val updatedDrawingList: LiveData<List<Drawing>> = _updatedDrawingList

    private var tickingJob: Job? = null

    fun getDrawingsList() {
        _drawingsListResponse.value = Result.Loading()

        viewModelScope.launch {
            val response = repository.getUpcomingDrawings(GRCKI_LOTO_GAME_ID)
            _drawingsListResponse.value = response
        }
    }

    fun startUpdatingRemainingTime(drawingList: List<Drawing>) {
        stopUpdatingRemainingTime()
        tickingJob = viewModelScope.launch {
            while (true) {
                _updatedDrawingList.value = drawingList.filter { it.drawTime - System.currentTimeMillis() > 0 }
                delay(1000)
            }
        }
    }

    private fun stopUpdatingRemainingTime() {
        tickingJob?.cancel()
    }

    fun onPause() {
        stopUpdatingRemainingTime()
    }

    fun onResume(drawingList: List<Drawing>) {
        startUpdatingRemainingTime(drawingList)
    }

    override fun onCleared() {
        super.onCleared()
        stopUpdatingRemainingTime()
    }

}