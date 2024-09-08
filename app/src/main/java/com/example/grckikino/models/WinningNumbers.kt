package com.example.grckikino.models

import com.google.gson.annotations.SerializedName

data class WinningNumbers(
    @SerializedName("list")
    val winningNumbersList: List<Int>
)