package com.example.grckikino.api

import com.example.grckikino.models.Drawing
import com.example.grckikino.models.DrawingResults
import com.example.grckikino.utils.GET_DRAWING_DETAILS
import com.example.grckikino.utils.GET_RESULTS
import com.example.grckikino.utils.GET_UPCOMING_20
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET(GET_UPCOMING_20)
    suspend fun getUpcomingDrawings(@Path("gameId") gameId: Int): Response<List<Drawing>>

    @GET(GET_DRAWING_DETAILS)
    suspend fun getDrawingDetails(@Path("gameId") gameId: Int, @Path("drawId") drawId: Int): Response<Drawing>

    @GET(GET_RESULTS)
    suspend fun getDrawingResults(@Path("gameId") gameId: Int, @Path("fromDate") fromDate: String, @Path("toDate") toDate: String): Response<DrawingResults>
}