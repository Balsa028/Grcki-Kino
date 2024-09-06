package com.example.grckikino.api

import com.example.grckikino.models.Drawing
import com.example.grckikino.utils.GET_UPCOMING_20
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET(GET_UPCOMING_20)
    suspend fun getUpcomingDrawings(@Path("gameId") gameId: Int): Response<List<Drawing>>

}