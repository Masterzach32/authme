package mcauth.fabric

import kotlinx.coroutines.*
import mcauth.common.*

fun setMultiplayerScreenStatus(callback: (Status) -> Unit) = GlobalScope.launch {
    callback(AuthMe.sessionManager.getStatus())
}
