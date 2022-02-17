package mx.alxr.googlefitreader.models

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.FitnessOptions

data class PermissionRequestArguments(
    val fitnessOptions: FitnessOptions,
    val account: GoogleSignInAccount
)
