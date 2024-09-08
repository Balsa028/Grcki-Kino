package com.example.grckikino.repository

import com.example.grckikino.api.ApiService
import com.example.grckikino.api.Result
import com.example.grckikino.models.Drawing
import com.example.grckikino.models.DrawingResults
import retrofit2.Response

class DrawingsRepository(private val apiService: ApiService) {

    suspend fun getUpcomingDrawings(gameId: Int): Result<List<Drawing>> =
        safeApiCall { apiService.getUpcomingDrawings(gameId) }

    suspend fun getDrawingDetails(gameId: Int, drawId: Int): Result<Drawing> =
        safeApiCall { apiService.getDrawingDetails(gameId, drawId) }

    suspend fun getDrawingResults(gameId: Int, fromDate: String, toDate: String): Result<DrawingResults> =
        safeApiCall { apiService.getDrawingResults(gameId, fromDate, toDate) }


    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> =
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return Result.Success(it)
                }
            }

            Result.Error(response.message())
        } catch (e: Exception) {
            Result.Error(e.message)
        }

}