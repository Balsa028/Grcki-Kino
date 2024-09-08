package com.example.grckikino.utils

//Network constants
const val BASE_URL = "https://api.opap.gr/"

const val GET_UPCOMING_20 =  "draws/v3.0/{gameId}/upcoming/20"
const val GET_DRAWING_DETAILS = "draws/v3.0/{gameId}/{drawId}"
const val GET_RESULTS = "draws/v3.0/{gameId}/draw-date/{fromDate}/{toDate}"
const val LIVE_DRAWING_URL = "https://mozzartbet.com/sr/lotto-animation/26#"

//Constants
const val GRCKI_LOTO_GAME_ID = 1100
const val REMAINING_TIME_WARNING = "00:59"
const val REMAINING_TIME_DIALOG_WARNING = "00:30"
const val MAX_SELECTED_NUMBERS = 8
const val HEADER_TYPE = 0
const val RESULT_ITEM_TYPE = 1