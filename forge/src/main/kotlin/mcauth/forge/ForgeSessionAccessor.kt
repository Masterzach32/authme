package mcauth.forge

import mcauth.common.*
import mcauth.common.accessors.*
import net.minecraft.client.*
import net.minecraftforge.fml.util.*
import java.lang.reflect.*

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
