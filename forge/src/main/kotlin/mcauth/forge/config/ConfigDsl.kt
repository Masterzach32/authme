package mcauth.forge.config

import net.minecraftforge.common.ForgeConfigSpec
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@DslMarker
annotation class ConfigDsl

@ConfigDsl
inline fun config(name: String, config: ForgeConfigSpec.Builder.() -> Unit): ForgeConfigSpec {
    contract { callsInPlace(config, InvocationKind.EXACTLY_ONCE) }
    return ForgeConfigSpec.Builder()
        .apply { section(name, config) }
        .build()
}

@ConfigDsl
inline fun ForgeConfigSpec.Builder.section(name: String, config: ForgeConfigSpec.Builder.() -> Unit) {
    contract { callsInPlace(config, InvocationKind.EXACTLY_ONCE) }
    push(name)
    config()
    pop()
}
