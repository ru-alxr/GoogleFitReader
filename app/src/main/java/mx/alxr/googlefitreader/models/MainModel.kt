package mx.alxr.googlefitreader.models

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import mx.alxr.googlefitreader.base.SingleEvent

data class MainModel(
    val permissionRequestViewVisible:Boolean = false,
    val explicitPermissionRequestHandled:Boolean = true,
    val permissionRequestEvent: SingleEvent<PermissionRequestArguments>? = null,
    val requestHistoryClientEvent: SingleEvent<GoogleSignInAccount>? = null,
    val errorMessageEvent: SingleEvent<String>? = null,
    val swipeEnabled:Boolean = false,
    val swipeRefreshing:Boolean = false,
    val lastSyncDate:String? = null,
    val isContentVisible:Boolean = false,
    val isDemoConsumed:Boolean = false
)