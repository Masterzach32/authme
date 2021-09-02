package mcauth.forge

import mcauth.common.Logger
import mcauth.common.accessors.SessionAccessor
import net.minecraft.client.Minecraft
import net.minecraft.client.User
import net.minecraftforge.fml.util.ObfuscationReflectionHelper
import java.lang.reflect.Field

object ForgeSessionAccessor : SessionAccessor {

    private val sessionField: Field by lazy {
        try {
            ObfuscationReflectionHelper.findField(Minecraft::class.java, "field_71449_j")
        } catch (e: Throwable) {
            Logger.warn("Could not find the session field in Minecraft.class, this may be a DEV workspace.")
            Minecraft::class.java.getDeclaredField("user").apply { isAccessible = true }
        }
    }

    override fun setSession(session: User) {
        sessionField.set(Minecraft.getInstance(), session)
    }
}
