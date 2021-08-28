package mcauth.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mcauth.common.AuthMeLoggerKt;
import mcauth.common.Status;
import mcauth.common.gui.AuthScreen;
import mcauth.common.gui.component.AuthButton;
import mcauth.fabric.AuthMe;
import mcauth.fabric.MultiplayerScreenStatusKt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JoinMultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {

    private static Status status = Status.UNKNOWN;
    private static ImageButton authButton;

    protected MultiplayerScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void init(CallbackInfo info) {
        // Inject the authenticate button at top left, using lock texture or fallback text
        AuthMeLoggerKt.getLogger().debug("Injecting authentication button into multiplayer screen");
        authButton = new AuthButton(
            AuthMe.INSTANCE.getConfigData().authButton.x,
            AuthMe.INSTANCE.getConfigData().authButton.y,
            new TranslatableComponent("gui.authme.multiplayer.button.auth"),
            button -> minecraft.setScreen(new AuthScreen(this, AuthMe.INSTANCE.getSessionManager())),
            this,
            (x, y) -> {
                // Sync configuration with updated button position
                AuthMe.INSTANCE.getConfigData().authButton.x = x;
                AuthMe.INSTANCE.getConfigData().authButton.y = y;
                AuthMe.INSTANCE.getConfig().save();
                return null;
            }
        );
        this.addRenderableWidget(authButton);

        // Fetch current session status
        MultiplayerScreenMixin.status = Status.UNKNOWN;

        MultiplayerScreenStatusKt.setMultiplayerScreenStatus(status -> {
            MultiplayerScreenMixin.status = status;
            return null;
        });
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        // Draw status text/icon on button
        drawCenteredString(
            matrices,
            minecraft.font,
            ChatFormatting.BOLD + status.toString(),
            authButton.x + authButton.getWidth(),
            authButton.y - 1,
            status.getColor());
    }
}
