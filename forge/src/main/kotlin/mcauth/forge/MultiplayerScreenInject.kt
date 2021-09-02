package mcauth.forge

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mcauth.common.Logger
import mcauth.common.Status
import mcauth.common.gui.AuthScreen
import mcauth.common.gui.component.AuthButton
import mcauth.forge.config.Config
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen
import net.minecraft.network.chat.TranslatableComponent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

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
        val (x, y) = Config.authButtonPosition.get()
        authButton = AuthButton(
            x, y,
            TranslatableComponent("gui.authme.multiplayer.button.auth"),
            { screen.minecraft.setScreen(AuthScreen(screen, AuthMe.sessionManager)) },
            screen,
            { x, y ->
                Logger.info("Saving point: $x, $y")
                Config.authButtonPosition.set(listOf(x, y))
                Config.authButtonPosition.save()
            }
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
