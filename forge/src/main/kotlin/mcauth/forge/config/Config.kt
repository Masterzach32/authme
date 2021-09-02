package mcauth.forge.config

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig

object Config {

    val configSpec: ForgeConfigSpec
    val authButtonPosition: ForgeConfigSpec.ConfigValue<List<Int>>

    init {
        configSpec = config("mcauth") {
            authButtonPosition = comment("The position of the auth button in the multiplayer screen")
                .define("authButtonPosition", listOf(6, 6))
        }
    }

    fun register(ctx: ModLoadingContext) {
        ctx.registerConfig(ModConfig.Type.CLIENT, configSpec, "mcauth.toml")
    }
}
