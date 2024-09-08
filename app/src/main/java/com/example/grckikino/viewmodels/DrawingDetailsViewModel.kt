package com.example.grckikino.viewmodels

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.grckikino.R
import com.example.grckikino.api.Result
import com.example.grckikino.models.Drawing
import com.example.grckikino.models.GridNumber
import com.example.grckikino.repository.DrawingsRepository
import com.example.grckikino.utils.GRCKI_LOTO_GAME_ID
import com.example.grckikino.utils.formatRemainingTimeForDisplay
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class DrawingDetailsViewModel(private val repository: DrawingsRepository, application: Application) : AndroidViewModel(application) {

    private val _drawingResponse: MutableLiveData<Result<Drawing>> = MutableLiveData<Result<Drawing>>()
    val drawingResponse: LiveData<Result<Drawing>> = _drawingResponse

    private val _updateRemainingTime: MutableLiveData<String> = MutableLiveData<String>()
    val updateRemainingTime: LiveData<String> = _updateRemainingTime

    private val _updateRandomSelectedItems: MutableLiveData<List<Int>> = MutableLiveData<List<Int>>()
    val updateRandomSelectedItems: LiveData<List<Int>> = _updateRandomSelectedItems

    private val _restoreSavedSelections: MutableLiveData<List<Int>> = MutableLiveData<List<Int>>()
    val restoreSavedSelections: LiveData<List<Int>> = _restoreSavedSelections

    private val _removeItemOnIndex: MutableLiveData<Int> = MutableLiveData<Int>()
    val removeItemOnIndex: LiveData<Int> = _removeItemOnIndex

    private val _increaseSelectedCounter: MutableLiveData<Int> = MutableLiveData<Int>()
    val increaseSelectedCounter: LiveData<Int> = _increaseSelectedCounter

    private val _decreaseSelectedCounter: MutableLiveData<Int> = MutableLiveData<Int>()
    val decreaseSelectedCounter: LiveData<Int> = _decreaseSelectedCounter

    private val _savedSelectedItems: MutableLiveData<List<Int>> = MutableLiveData<List<Int>>()
    val savedSelectedItems: LiveData<List<Int>> = _savedSelectedItems

    private var selectedNumbers = mutableListOf<Int>()
    private var currentDrawing: Drawing? = null
    private var tickingJob: Job? = null

    fun getDrawingDetails(drawId: Int) {
        _drawingResponse.value = Result.Loading()

        viewModelScope.launch {
            val response = repository.getDrawingDetails(GRCKI_LOTO_GAME_ID, drawId)
            _drawingResponse.value = response
        }
    }

    fun startUpdatingRemainingTime(drawTime: Long) {
        stopUpdatingRemainingTime()
        tickingJob = viewModelScope.launch {
            var updatedValue: String
            while (true) {
                if (drawTime - System.currentTimeMillis() > 0)
                    updatedValue = drawTime.formatRemainingTimeForDisplay()
                else {
                    stopUpdatingRemainingTime()
                    updatedValue = ContextCompat.getString(getApplication(), R.string.past)
                }
                _updateRemainingTime.value = updatedValue
                delay(1000)
            }
        }
    }

    private fun stopUpdatingRemainingTime() {
        tickingJob?.cancel()
    }

    fun saveCurrentDrawing(drawing: Drawing) {
        currentDrawing = drawing
    }

    fun selectRandomNumbers(quantity: Int, gridNumbers: List<GridNumber>) {
        clearSelectedNumber(gridNumbers)
        repeat(quantity) {
            val generatedNumber = Random.nextInt(1, 80)
            gridNumbers[generatedNumber - 1].isSelected = true
            selectedNumbers.add(generatedNumber)
        }
        _updateRandomSelectedItems.value = selectedNumbers
    }

    fun handleSelectingBehavior(number: GridNumber) {
        if (selectedNumbers.size == 8 && !number.isSelected) return

        if (number.isSelected) {
            number.isSelected = false
            selectedNumbers.remove(number.value)
            _decreaseSelectedCounter.value = 1
        } else {
            number.isSelected = true
            selectedNumbers.add(number.value)
            _increaseSelectedCounter.value = 1
        }

        _savedSelectedItems.value = selectedNumbers
    }

    fun clearSelectedNumber(gridNumbers: List<GridNumber>) {
        selectedNumbers.forEach {
            gridNumbers[it - 1].isSelected = false
            _removeItemOnIndex.value = it - 1
        }
        _decreaseSelectedCounter.value = selectedNumbers.size
        selectedNumbers.clear()
        _savedSelectedItems.value = selectedNumbers
    }

    fun handleSavedSelections() {
        _savedSelectedItems.value?.let {
            if (it.isNotEmpty()) _restoreSavedSelections.value = it
        }
    }

    fun updateSavedSelections(selectedList: List<Int>) {
        _savedSelectedItems.value = selectedList
    }

    fun onResume() {
        currentDrawing?.let { startUpdatingRemainingTime(it.drawTime) }
    }

    fun onPause() {
        stopUpdatingRemainingTime()
    }

    override fun onCleared() {
        super.onCleared()
        stopUpdatingRemainingTime()
    }

}