package mcauth.common.gui.component

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent

class PasswordField(
    font: Font,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    content: Component
) : EditBox(font, x, y, width, height, content) {

    init {
        setMaxLength(256)
        setFormatter { value, limit -> TextComponent(value).withStyle { it.withObfuscated(true) }.visualOrderText }
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (!isActive || Screen.isCopy(keyCode) || Screen.isCut(keyCode))
            return false
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
}
