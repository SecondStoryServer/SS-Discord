import me.syari.ss.discord.Discord
import me.syari.ss.discord.Discord.init
import me.syari.ss.discord.entities.TextChannel
import javax.security.auth.login.LoginException

object MessageListenerExample {
    private var sendMessageCount = 0
    private const val token = DiscordToken.BOT_TOKEN
    private const val testChannel = 710828174686027790L

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            println("init")
            init(token) { event ->
                val authorUser = event.author
                if (!authorUser.isBot) {
                    val authorMember = event.member ?: return@init
                    val name = authorMember.displayName
                    val message = event.message.contentDisplay
                    val channel = event.channel
                    channel.sendMessage("Chat -> $name: $message")
                } else {
                    sendMessageCount++
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