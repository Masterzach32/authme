package mcauth.common.gui

import com.mojang.authlib.exceptions.*
import com.mojang.blaze3d.vertex.*
import kotlinx.coroutines.*
import mcauth.common.*
import mcauth.common.gui.component.*
import net.minecraft.*
import net.minecraft.client.gui.*
import net.minecraft.client.gui.components.*
import net.minecraft.client.gui.screens.*
import net.minecraft.network.chat.*

class AuthScreen(private val parent: Screen, private val sessionManager: SessionManager) :
    Screen(TranslatableComponent("gui.authme.auth.title")),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private var lastUsername: String = sessionManager.currentSession.name

    private lateinit var usernameField: EditBox
    private lateinit var passwordField: EditBox

    private lateinit var loginButton: Button
    private lateinit var cancelButton: Button

    private var greeting: Component = getGreetingComponent(lastUsername)
    private var message: Component? = null

    private val canSubmit: Boolean
        get() = usernameField.value.isNotEmpty() || passwordField.value.isNotEmpty()

    override fun init() {
        super.init()
        usernameField = EditBox(
            minecraft!!.font,
            width / 2 - 100,
            76,
            200,
            20,
            TranslatableComponent("gui.authme.auth.field.username")
        )
        usernameField.apply {
            setMaxLength(128)
            setSuggestion(lastUsername)
            setResponder {
                usernameField.setSuggestion(if (it.isEmpty()) lastUsername else "")
                loginButton.active = canSubmit
            }
        }
        addRenderableWidget(usernameField)

        passwordField = PasswordField(
            minecraft!!.font,
            width / 2 - 100,
            116,
            200,
            20,
            TranslatableComponent("gui.authme.auth.field.password")
        )
        passwordField.setResponder {
            loginButton.message = TranslatableComponent(
                "gui.authme.auth.button.login.${if (it.isEmpty()) "offline" else "online"}"
            )
            loginButton.active = canSubmit
            cancelButton.message = TranslatableComponent("gui.authme.auth.button.cancel")
        }
        addRenderableWidget(passwordField)

        loginButton = Button(
            width / 2 - 100,
            height / 4 + 96 + 18,
            200,
            20,
            TranslatableComponent("gui.authme.auth.button.login.offline"),
            { submit() }
        )
        addRenderableWidget(loginButton)
        cancelButton = Button(
            width / 2 - 100,
            height / 4 + 120 + 18,
            200,
            20,
            TranslatableComponent("gui.authme.auth.button.cancel"),
            { onClose() }
        )
        addRenderableWidget(cancelButton)
    }

    override fun shouldCloseOnEsc() = !usernameField.isFocused && !passwordField.isFocused

    override fun removed() {
        minecraft?.keyboardHandler?.setSendRepeatsToGui(false)
    }

    override fun render(poseStack: PoseStack, x: Int, y: Int, delta: Float) {
        renderBackground(poseStack)
        val client = minecraft!!

        drawCenteredString(poseStack, client.font, title, width / 2, 17, 16777215)
        drawCenteredString(poseStack, client.font, greeting, width / 2, 34, 16777215)

        message?.let {
            GuiComponent.drawCenteredString(poseStack, client.font, it, width / 2, height / 4 + 86, 16777215)
        }

        drawString(
            poseStack,
            client.font,
            TranslatableComponent("gui.authme.auth.field.username"),
            width / 2 - 100,
            64,
            10526880
        )
        drawString(
            poseStack,
            client.font,
            TranslatableComponent("gui.authme.auth.field.password"),
            width / 2 - 100,
            104,
            10526880
        )

        usernameField.render(poseStack, x, y, delta)
        passwordField.render(poseStack, x, y, delta)

        super.render(poseStack, x, y, delta)
    }

    fun submit() {
        if (!loginButton.active)
            return

        loginButton.active = false

        val username: String = usernameField.value.ifEmpty { lastUsername }
        val password: String = passwordField.value

        if (password.isEmpty()) {
            val newOfflineSession = sessionManager.login(username)

            lastUsername = newOfflineSession.name
            greeting = getGreetingComponent(lastUsername)
            message = TranslatableComponent("gui.authme.auth.message.success.offline")
                .withStyle { it.withBold(true).withColor(ChatFormatting.AQUA) }

            usernameField.value = ""
            passwordField.value = ""
            cancelButton.message = TranslatableComponent("gui.authme.auth.button.return")
        } else {
            launch {
                try {
                    val newSession = sessionManager.login(username, password)

                    lastUsername = newSession.name
                    greeting = getGreetingComponent(lastUsername)

                    message = TranslatableComponent("gui.authme.auth.message.success")
                        .withStyle { it.withBold(true).withColor(ChatFormatting.GREEN) }

                    usernameField.value = ""
                    passwordField.value = ""
                    cancelButton.message = TranslatableComponent("gui.authme.auth.button.return")
                } catch (e: Throwable) {
                    loginButton.active = true

                    val text = if (e.cause is InvalidCredentialsException)
                        TranslatableComponent("gui.authme.auth.message.failed.credentials")
                    else
                        TranslatableComponent("gui.authme.auth.message.failed.generic", e.cause?.message)

                    message = text.withStyle { it.withBold(true).withColor(ChatFormatting.RED) }

                    Logger.warn("Login failed:", e)
                }
            }
        }
    }

    override fun onClose() {
        this.cancel("AuthScreen was closed.")
        minecraft?.setScreen(parent)
    }

    private fun getGreetingComponent(username: String) = TranslatableComponent(
        "gui.authme.auth.greeting",
        TextComponent(username).withStyle { it.withColor(ChatFormatting.YELLOW) }
    ).withStyle { it.withColor(ChatFormatting.GRAY) }
}
