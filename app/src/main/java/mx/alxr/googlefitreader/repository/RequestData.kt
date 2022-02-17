package mx.alxr.googlefitreader.repository

import com.google.android.gms.fitness.HistoryClient

data class RequestData(
    val fitnessApiStuff: FitnessApiStuff,
    val historyClient: HistoryClient,
    val startSeconds: Long,
    val endSeconds: Long
)