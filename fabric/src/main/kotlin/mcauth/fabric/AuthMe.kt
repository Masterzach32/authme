package mcauth.fabric

import mcauth.common.SessionManager
import mcauth.fabric.config.AuthMeConfig
import me.shedaniel.autoconfig.ConfigHolder
import net.fabricmc.api.ClientModInitializer

object AuthMe : ClientModInitializer {

    val config: ConfigHolder<AuthMeConfig> = AuthMeConfig.init()
    val configData: AuthMeConfig = config.get()

    val sessionManager = SessionManager(FabricSessionAccessor)

    override fun onInitializeClient() {}
}
