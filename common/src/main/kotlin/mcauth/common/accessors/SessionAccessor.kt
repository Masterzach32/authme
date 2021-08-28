package mcauth.common.accessors

import net.minecraft.client.*

interface SessionAccessor {

    fun setSession(session: User)
}
