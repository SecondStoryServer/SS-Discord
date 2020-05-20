import me.syari.ss.discord.api.MessageReceivedEvent
import me.syari.ss.discord.internal.JDA
import me.syari.ss.discord.internal.JDA.Companion.build
import me.syari.ss.discord.internal.entities.TextChannel.Companion.get
import javax.security.auth.login.LoginException

object MessageListenerExample {
    private var jda: JDA? = null
    private var sendMessageCount = 0
    private const val token = DiscordToken.BOT_TOKEN

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            jda = build(token) {
                val authorUser = author
                if (!authorUser.isBot) {
                    val authorMember = member ?: return@build
                    val name = authorMember.displayName
                    val message = message.contentDisplay
                    val channel = channel
                    channel.sendMessage(
                        "Chat -> $name: $message\r\nGetTextChannel -> " + (get(
                            710828174686027790L
                        ) != null)
                    )
                } else {
                    sendMessageCount++
                    if (sendMessageCount == 2) {
                        println(">> Shutdown")
                        jda?.shutdown()
                    }
                }
            }
            jda?.awaitReady()
        } catch (e: LoginException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}