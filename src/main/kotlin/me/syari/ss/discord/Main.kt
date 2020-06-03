package me.syari.ss.discord

import me.syari.ss.core.auto.OnEnable
import me.syari.ss.discord.paper.DiscordConnector
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
    companion object {
        internal lateinit var discordPlugin: JavaPlugin
    }

    override fun onEnable() {
        discordPlugin = this
        OnEnable.register(DiscordConnector)
    }
}