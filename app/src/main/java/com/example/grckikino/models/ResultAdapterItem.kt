package com.example.grckikino.models

data class ResultAdapterItem(
    val type: Int,
    val drawTime: String? = null,
    val drawId: Int? = null,
    val winningNumbers: List<Int>? = null
)
