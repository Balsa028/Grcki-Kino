package com.example.grckikino.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grckikino.api.Result
import com.example.grckikino.models.DrawingResults
import com.example.grckikino.models.ResultAdapterItem
import com.example.grckikino.models.ResultItem
import com.example.grckikino.repository.DrawingsRepository
import com.example.grckikino.utils.GRCKI_LOTO_GAME_ID
import com.example.grckikino.utils.HEADER_TYPE
import com.example.grckikino.utils.RESULT_ITEM_TYPE
import com.example.grckikino.utils.formatDrawingTimeForDisplay
import com.example.grckikino.utils.formatForResults
import kotlinx.coroutines.launch
import java.util.Date

class DrawingResultViewModel(private val repository: DrawingsRepository) : ViewModel() {

    private var _resultsListResponse = MutableLiveData<Result<DrawingResults>>()
    val resultsListResponse: LiveData<Result<DrawingResults>> = _resultsListResponse

    fun getDrawingResults(fromDate: String, toDate: String) {
        _resultsListResponse.value = Result.Loading()

        viewModelScope.launch {
            val response = repository.getDrawingResults(GRCKI_LOTO_GAME_ID, fromDate, toDate)
            _resultsListResponse.value = response
        }
    }

    fun prepareResultItems(resultList: List<ResultItem>): List<ResultAdapterItem> {
        val adapterItemList: MutableList<ResultAdapterItem> = mutableListOf()
        resultList.forEach { item ->
            val resultDrawTime = "${Date().formatForResults()}   ${item.drawTime.formatDrawingTimeForDisplay()}"
            adapterItemList.add(ResultAdapterItem(type = HEADER_TYPE, drawTime = resultDrawTime, drawId = item.drawId))
            adapterItemList.add(ResultAdapterItem(type = RESULT_ITEM_TYPE, winningNumbers = item.winningNumbers.winningNumbersList))
        }
        return adapterItemList
    }
}