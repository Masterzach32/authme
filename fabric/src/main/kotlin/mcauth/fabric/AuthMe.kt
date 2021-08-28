package mcauth.fabric

import mcauth.common.*
import mcauth.fabric.config.*
import me.shedaniel.autoconfig.*
import net.fabricmc.api.*

object AuthMe : ClientModInitializer {

    val config: ConfigHolder<AuthMeConfig> = AuthMeConfig.init()
    val configData: AuthMeConfig = config.get()

    val sessionManager = SessionManager(FabricSessionAccessor)

    override fun onInitializeClient() {}
}
