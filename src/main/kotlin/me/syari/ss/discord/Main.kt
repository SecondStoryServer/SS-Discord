package me.syari.ss.discord

import me.syari.ss.discord.entities.TextChannel
import org.bukkit.plugin.java.JavaPlugin
import javax.security.auth.login.LoginException

class Main : JavaPlugin() {
    private var sendMessageCount = 0
    private val token = ""
    private val testChannel = 716202262417899562L

    override fun onEnable() {
        try {
            println("init")
            Discord.init(token) { event ->
                val authorUser = event.author
                if (!authorUser.isBot) {
                    val authorMember = event.member ?: return@init
                    val name = authorMember.displayName
                    val message = event.message.contentDisplay
                    val channel = event.channel
                    channel.sendMessage("$name: $message")
                    sendMessageCount++
                } else {
                    if (sendMessageCount == 2) {
                        shutdown()
                    }
                }
            }
        } catch (e: LoginException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        println("awaitReady")
        Discord.awaitReady()
        TextChannel.get(testChannel)?.sendMessage("Login") ?: println("notFound")
    }
}