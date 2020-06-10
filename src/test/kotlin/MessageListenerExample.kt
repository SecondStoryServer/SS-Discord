import me.syari.discord.KtDiscord
import me.syari.discord.entity.api.TextChannel
import javax.security.auth.login.LoginException

private var sendMessageCount = 0
private const val token = DiscordToken.BOT_TOKEN
private const val testChannel = 716202262417899562L

suspend fun main() {
    try {
        KtDiscord.login(token) {
            val author = it.member
            if (!author.isBot) {
                val name = author.displayName
                val message = it.contentDisplay
                val channel = it.channel
                channel.send("$name: $message")
                sendMessageCount++
            } else {
                // if (sendMessageCount == 2) {
                //     shutdown()
                // }
            }
        }
    } catch (e: LoginException) {
        e.printStackTrace()
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
    TextChannel.get(testChannel)?.send("Login") ?: println("notFound")
}