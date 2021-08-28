package mcauth.fabric.mixin;

import com.mojang.realmsclient.RealmsMainScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RealmsMainScreen.class)
public interface RealmsMainScreenAccessMixin {

    @Accessor
    @Mutable
    static void setCheckedClientCompatability(boolean checked) {
    }

    @Accessor
    @Mutable
    static void setRealmsGenericErrorScreen(Screen screen) {
    }
}
