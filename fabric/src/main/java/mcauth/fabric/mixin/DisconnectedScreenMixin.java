package mcauth.fabric.mixin;

import mcauth.common.AuthMeLoggerKt;
import mcauth.common.gui.AuthScreen;
import mcauth.fabric.AuthMe;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public abstract class DisconnectedScreenMixin extends Screen {

    @Shadow
    @Final
    private Component reason;

    @Shadow
    @Final
    private Screen parent;

    protected DisconnectedScreenMixin(Component title) {
        super(title);
    }

    /**
     * Returns the translation key for a text component.
     *
     * @param component text component
     * @return translation key of translation text component else empty string
     */
    private static String getTranslationKey(Component component) {
        return component instanceof TranslatableComponent ? ((TranslatableComponent) component).getKey() : "";
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        // Determine if the disconnection reason is session related
        if (reason == null || !getTranslationKey(reason).startsWith("disconnect.loginFailed")) return;

        final Button backButton = (Button) ((ScreenChildAccessMixin) this).getChildren().get(0);

        // Inject the authentication button where the back button was
        AuthMeLoggerKt.getLogger().debug("Injecting authentication button into disconnection screen");
        this.addRenderableWidget(
            new Button(
                backButton.x,
                backButton.y,
                backButton.getWidth(),
                20,
                new TranslatableComponent("gui.authme.disconnect.button.auth"),
                button -> minecraft.setScreen(new AuthScreen(parent, AuthMe.INSTANCE.getSessionManager()))
            )
        );

        // Move back button below
        backButton.y += 26;
    }
}
