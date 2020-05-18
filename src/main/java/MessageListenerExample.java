import me.syari.ss.discord.api.MessageReceivedEvent;
import me.syari.ss.discord.internal.JDA;
import me.syari.ss.discord.internal.entities.Member;
import me.syari.ss.discord.internal.entities.TextChannel;
import me.syari.ss.discord.internal.entities.User;

import javax.security.auth.login.LoginException;
import java.util.function.Consumer;

public class MessageListenerExample {
    private static JDA jda;

    private static int sendMessageCount = 0;

    private static final String token = DiscordToken.BOT_TOKEN;

    private static final Consumer<MessageReceivedEvent> messageReceivedEvent = (event) -> {
        User authorUser = event.getAuthor();
        if (!authorUser.isBot()) {
            Member authorMember = event.getMember();
            if (authorMember == null) return;
            String name = authorMember.getDisplayName();
            String message = event.getMessage().getContentDisplay();
            TextChannel channel = event.getChannel();
            channel.sendMessage("Chat -> " + name + ": " + message + "\r\n" +
                    "GetTextChannel -> " + (jda.getTextChannelById(710828174686027790L) != null)
            );
        } else {
            sendMessageCount ++;
            if(sendMessageCount == 2){
                System.out.println(">> Shutdown");
                jda.shutdown();
            }
        }
    };

    public static void main(String[] args) {
        try {
            jda = JDA.build(token, messageReceivedEvent);
            jda.awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
