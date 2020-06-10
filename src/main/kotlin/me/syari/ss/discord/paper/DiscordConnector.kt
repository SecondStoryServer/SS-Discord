package me.syari.ss.discord.paper

import me.syari.ss.core.Main.Companion.console
import me.syari.ss.core.auto.OnEnable
import me.syari.ss.core.config.CreateConfig.config
import me.syari.ss.core.config.dataType.ConfigDataType
import me.syari.ss.core.scheduler.CreateScheduler.run
import me.syari.discord.KtDiscord
import me.syari.ss.discord.Main.Companion.discordPlugin

object DiscordConnector: OnEnable {
    override fun onEnable() {
        config(discordPlugin, console, "config.yml", default = mapOf("bot_token" to "")) {
            val token = get("bot_token", ConfigDataType.STRING)
            if (token != null) {
                KtDiscord.loginAsync(token) { event ->
                    run(discordPlugin) {
                        DiscordMessageReceiveEvent(event).callEvent()
                    }
                }
            }
        }
    }
}