package mcauth.fabric

import mcauth.common.accessors.SessionAccessor
import mcauth.fabric.mixin.MinecraftSessionAccessorMixin
import mcauth.fabric.mixin.RealmsMainScreenAccessMixin
import net.minecraft.client.Minecraft
import net.minecraft.client.User

object FabricSessionAccessor : SessionAccessor {

    override fun setSession(session: User) {
        (Minecraft.getInstance() as MinecraftSessionAccessorMixin).setUser(session)

        RealmsMainScreenAccessMixin.setCheckedClientCompatability(false)
        RealmsMainScreenAccessMixin.setRealmsGenericErrorScreen(null)
    }
}
