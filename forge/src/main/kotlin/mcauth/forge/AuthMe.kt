package mcauth.forge

import mcauth.common.*
import net.minecraftforge.fml.common.*

@Mod(MOD_ID)
class AuthMe {

    companion object {
        val sessionManager = SessionManager(ForgeSessionAccessor)
    }
}
