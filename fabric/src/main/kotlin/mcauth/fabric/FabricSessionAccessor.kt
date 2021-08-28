package mcauth.fabric

import mcauth.common.accessors.*
import mcauth.fabric.mixin.*
import net.minecraft.client.*

object FabricSessionAccessor : SessionAccessor {

    override fun setSession(session: User) {
        (Minecraft.getInstance() as MinecraftSessionAccessorMixin).setUser(session)

        RealmsMainScreenAccessMixin.setCheckedClientCompatability(false)
        RealmsMainScreenAccessMixin.setRealmsGenericErrorScreen(null)
    }
}
