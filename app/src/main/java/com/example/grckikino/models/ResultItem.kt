package com.example.grckikino.models

data class ResultItem(
    val gameId: Int = 0,
    val winningNumbers: WinningNumbers,
    val drawId: Int = 0,
    val drawTime: Long = 0
)