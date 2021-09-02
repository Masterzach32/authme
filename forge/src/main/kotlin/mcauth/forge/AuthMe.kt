package mcauth.forge

import mcauth.common.MOD_ID
import mcauth.common.SessionManager
import mcauth.forge.config.Config
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod

@Mod(MOD_ID)
class AuthMe {

    init {
        ModLoadingContext.get().also { context ->
            Config.register(context)
        }
    }

    companion object {
        val sessionManager = SessionManager(ForgeSessionAccessor)
    }
}
