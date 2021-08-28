package mcauth.common

import net.minecraft.client.resources.language.*

enum class Status(val langKey: String, val color: Int) {
    VALID("gui.authme.status.valid", 0x00FF00),
    INVALID("gui.authme.status.invalid", 0xFF0000),
    UNKNOWN("gui.authme.status.unknown", 0xFFFF00);

    override fun toString(): String = I18n.get(langKey)
}
