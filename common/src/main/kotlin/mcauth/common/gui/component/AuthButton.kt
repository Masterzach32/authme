package mcauth.common.gui.component

import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class AuthButton(
    x: Int,
    y: Int,
    text: Component,
    action: OnPress,
    private val screen: Screen,
    private val onSetPos: (Int, Int) -> Unit
) : ImageButton(
    x,
    y,
    20,
    20,
    0,
    146,
    20,
    ResourceLocation("minecraft:textures/gui/widgets.png"),
    256,
    256,
    action,
    text
) {

    private var didDrag = false

    override fun mouseClicked(x: Double, y: Double, button: Int): Boolean =
        isValidClickButton(button) && clicked(x, y)

    override fun mouseReleased(x: Double, y: Double, button: Int): Boolean =
        if (didDrag) {
            onSetPos(this.x, this.y)
            didDrag = false
            didDrag
        } else
            super.mouseClicked(x, y, button)

    override fun onDrag(x: Double, y: Double, deltaX: Double, deltaY: Double) {
        // Move the button with the drag
        setPosition(
            0.coerceAtLeast(x.toInt() - width / 2).coerceAtMost(screen.width - width),
            0.coerceAtLeast(y.toInt() - width / 2).coerceAtMost(screen.height - height)
        )
        didDrag = true

        super.onDrag(x, y, deltaX, deltaY)
    }
}
