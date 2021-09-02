package mcauth.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import net.minecraft.client.Minecraft

private val dispatcher = Minecraft.getInstance().asCoroutineDispatcher()
val Dispatchers.MinecraftUI: CoroutineDispatcher
    get() = dispatcher
