package mcauth.fabric

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mcauth.common.Status

fun setMultiplayerScreenStatus(callback: (Status) -> Unit) = GlobalScope.launch {
    callback(AuthMe.sessionManager.getStatus())
}
