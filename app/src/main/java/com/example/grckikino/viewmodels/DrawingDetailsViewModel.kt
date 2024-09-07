package com.example.grckikino.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grckikino.models.GridNumber
import com.example.grckikino.repository.DrawingsRepository
import com.example.grckikino.utils.formatRemainingTimeForDisplay
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class DrawingDetailsViewModel(repository: DrawingsRepository) : ViewModel() {

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

    private val _saveSelectedItems: MutableLiveData<List<Int>> = MutableLiveData<List<Int>>()
    val saveSelectedItems: LiveData<List<Int>> = _saveSelectedItems

    var selectedNumbers = mutableListOf<Int>()

    var tickingJob: Job? = null

    fun startUpdatingRemainingTime(timeInMillis: Long) {
        stopUpdatingRemainingTime()
        tickingJob = viewModelScope.launch {
            var updatedValue: String
            while (true) {
                if (timeInMillis - System.currentTimeMillis() > 0)
                    updatedValue = timeInMillis.formatRemainingTimeForDisplay()
                else {
                    stopUpdatingRemainingTime()
                    updatedValue = "Odigrano"
                }
                _updateRemainingTime.value = updatedValue
                delay(1000)
            }
        }
    }

    private fun stopUpdatingRemainingTime() {
        tickingJob?.cancel()
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

        _saveSelectedItems.value = selectedNumbers
    }

    private fun clearSelectedNumber(gridNumbers: List<GridNumber>) {
        selectedNumbers.forEach {
            gridNumbers[it - 1].isSelected = false
            _removeItemOnIndex.value = it - 1
        }
        _decreaseSelectedCounter.value = selectedNumbers.size
        selectedNumbers.clear()
        _saveSelectedItems.value = selectedNumbers
    }

    fun handleSavedSelections(savedNumbers: List<Int>) {
        if (savedNumbers.isNotEmpty()) _restoreSavedSelections.value = savedNumbers
    }

    fun onResume(timeInMillis: Long) {
        startUpdatingRemainingTime(timeInMillis)
    }

    fun onPause() {
        stopUpdatingRemainingTime()
    }

    override fun onCleared() {
        super.onCleared()
        stopUpdatingRemainingTime()
    }

}