package me.syari.ss.discord.paper

import me.syari.ss.core.Main.Companion.console
import me.syari.ss.core.auto.OnEnable
import me.syari.ss.core.config.CreateConfig.config
import me.syari.ss.core.config.dataType.ConfigDataType
import me.syari.ss.discord.Discord
import me.syari.ss.discord.Main.Companion.discordPlugin

object DiscordConnector: OnEnable {
    override fun onEnable() {
        config(discordPlugin, console, "config.yml"){
            val token = get("bot_token", ConfigDataType.STRING)
            if(token != null){
                Discord.init(token){ event ->
                    DiscordMessageReceiveEvent(event).callEvent()
                }
                Discord.awaitReady()
            }
        }
    }
}