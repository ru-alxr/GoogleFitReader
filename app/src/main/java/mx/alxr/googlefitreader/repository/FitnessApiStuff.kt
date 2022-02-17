package mx.alxr.googlefitreader.repository

import android.app.PendingIntent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType

data class FitnessApiStuff(
    val fitnessOptions: FitnessOptions,
    val account: GoogleSignInAccount,
    val pendingIntent: PendingIntent,
    val dataType: DataType
)
