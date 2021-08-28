package mcauth.common

import com.mojang.authlib.*
import com.mojang.authlib.exceptions.*
import com.mojang.authlib.minecraft.*
import com.mojang.authlib.yggdrasil.*
import com.mojang.util.*
import kotlinx.coroutines.*
import mcauth.common.accessors.*
import net.minecraft.client.*
import java.util.*

class SessionManager(private val sessionAccessor: SessionAccessor) {

    // Session status cache
    private var lastStatus = Status.UNKNOWN
    private var lastStatusCheck: Long = 0

    private val authenticationService: AuthenticationService = YggdrasilAuthenticationService(
        Minecraft.getInstance().proxy,
        UUID.randomUUID().toString()
    )
    private val userAuthentication: UserAuthentication = authenticationService.createUserAuthentication(Agent.MINECRAFT)
    private val sessionService: MinecraftSessionService = authenticationService.createMinecraftSessionService()

    val currentSession: User
        get() = Minecraft.getInstance().user

    suspend fun getStatus(): Status {
        if (System.currentTimeMillis() - lastStatusCheck < STATUS_TTL)
            return lastStatus

        return withContext(Dispatchers.IO) {
            val session = currentSession
            val profile = session.gameProfile
            val token = session.accessToken
            val id = UUID.randomUUID().toString()

            lastStatus = try {
                sessionService.joinServer(profile, token, id)
                if (sessionService.hasJoinedServer(profile, id, null).isComplete) {
                    Logger.debug("Session is valid!")
                    Status.VALID
                } else {
                    Logger.debug("Session is invalid!")
                    Status.INVALID
                }
            } catch (e: AuthenticationException) {
                Logger.warn("Unable to validate the session:", e)
                Status.INVALID
            }

            lastStatusCheck = System.currentTimeMillis()
            lastStatus
        }
    }

    suspend fun login(username: String, password: String): User = withContext(Dispatchers.IO) {
        userAuthentication.setUsername(username)
        userAuthentication.setPassword(password)
        userAuthentication.logIn()

        val name = userAuthentication.selectedProfile.name
        val uuid = UUIDTypeAdapter.fromUUID(userAuthentication.selectedProfile.id)
        val token = userAuthentication.authenticatedToken
        val type = userAuthentication.userType.name

        userAuthentication.logOut()

        val newSession = User(name, uuid, token, type)
        setSession(newSession)

        Logger.info("New session login successful! Logged in as $uuid ($name)")
        newSession
    }

    fun login(username: String): User {
        return try {
            val uuid = UUID.nameUUIDFromBytes("offline:$username".toByteArray())
            val newSession = User(username, uuid.toString(), "invalidtoken", User.Type.LEGACY.name)
            setSession(newSession)

            Logger.info("Offline session login successful!")
            newSession
        } catch (e: Throwable) {
            Logger.error("Offline session login failed:", e)
            currentSession
        }
    }

    private fun setSession(session: User) {
        sessionAccessor.setSession(session)

        // Cached status is now stale
        lastStatus = Status.UNKNOWN
        lastStatusCheck = 0
    }

    companion object {
        const val STATUS_TTL = 60000L
    }
}
