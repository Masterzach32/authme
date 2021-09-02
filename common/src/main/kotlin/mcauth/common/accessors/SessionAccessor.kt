package mcauth.common.accessors

import net.minecraft.client.User

interface SessionAccessor {

    fun setSession(session: User)
}
