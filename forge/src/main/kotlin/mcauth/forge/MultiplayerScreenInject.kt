package mcauth.forge

import kotlinx.coroutines.*
import mcauth.common.*
import mcauth.common.gui.*
import mcauth.common.gui.component.*
import net.minecraft.*
import net.minecraft.client.gui.*
import net.minecraft.client.gui.screens.multiplayer.*
import net.minecraft.network.chat.*
import net.minecraftforge.api.distmarker.*
import net.minecraftforge.client.event.*
import net.minecraftforge.eventbus.api.*
import net.minecraftforge.fml.common.*

@Mod.EventBusSubscriber(Dist.CLIENT)
object MultiplayerScreenInject {

    private lateinit var authButton: AuthButton

    private var status = Status.UNKNOWN

    @SubscribeEvent
    @JvmStatic
    fun onGuiPostInit(event: GuiScreenEvent.InitGuiEvent.Post) {
        val screen = event.gui as? JoinMultiplayerScreen
            ?: return

        Logger.info("Injecting auth button into multiplayer menu")
        authButton = AuthButton(
            6, 6,
            TranslatableComponent("gui.authme.multiplayer.button.auth"),
            { screen.minecraft.setScreen(AuthScreen(screen, AuthMe.sessionManager)) },
            screen,
            { _, _ -> }
        )
        event.addWidget(authButton)

        GlobalScope.launch {
            status = AuthMe.sessionManager.getStatus()
        }
    }

    @SubscribeEvent
    @JvmStatic
    fun onGuiRender(event: GuiScreenEvent.DrawScreenEvent.Post) {
        val screen = event.gui as? JoinMultiplayerScreen
            ?: return

        GuiComponent.drawCenteredString(
            event.matrixStack,
            screen.minecraft.font,
            ChatFormatting.BOLD.toString() + status.toString(),
            authButton.x + authButton.width,
            authButton.y - 1,
            status.color
        )
    }
}
