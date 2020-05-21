import me.syari.ss.discord.internal.Discord
import me.syari.ss.discord.internal.Discord.init
import me.syari.ss.discord.internal.entities.TextChannel.Companion.get
import javax.security.auth.login.LoginException

object MessageListenerExample {
    private var sendMessageCount = 0
    private const val token = DiscordToken.BOT_TOKEN

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            init(token) { event ->
                val authorUser = event.author
                if (!authorUser.isBot) {
                    val authorMember = event.member ?: return@init
                    val name = authorMember.displayName
                    val message = event.message.contentDisplay
                    val channel = event.channel
                    channel.sendMessage(
                        "Chat -> $name: $message\r\nGetTextChannel -> " + (get(
                            710828174686027790L
                        ) != null)
                    )
                } else {
                    sendMessageCount++
                    if (sendMessageCount == 2) {
                        println(">> Shutdown")
                        shutdown()
                    }
                }
            }
            Discord.awaitReady()
        } catch (e: LoginException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}