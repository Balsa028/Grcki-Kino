package com.example.grckikino.repository

import androidx.core.content.ContextCompat
import com.example.grckikino.R
import com.example.grckikino.api.ApiService
import com.example.grckikino.api.Result
import com.example.grckikino.models.Drawing
import retrofit2.Response

class DrawingsRepository(private val apiService: ApiService) {

    suspend fun getUpcomingDrawings(gameId: Int): Result<List<Drawing>> =
        safeApiCall { apiService.getUpcomingDrawings(gameId) }

    suspend fun getDrawingDetails(gameId: Int, drawId: Int): Result<Drawing> =
        safeApiCall { apiService.getDrawingDetails(gameId, drawId) }


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