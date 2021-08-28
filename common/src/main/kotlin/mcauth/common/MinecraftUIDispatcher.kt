package mcauth.common

import kotlinx.coroutines.*
import net.minecraft.client.*

private val dispatcher = Minecraft.getInstance().asCoroutineDispatcher()
val Dispatchers.MinecraftUI: CoroutineDispatcher
    get() = dispatcher
