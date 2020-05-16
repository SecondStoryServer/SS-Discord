import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.MessageChannel;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.events.message.MessageReceivedEvent;
import me.syari.ss.discord.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

public class MessageListenerExample extends ListenerAdapter
{
    private static JDA jda;

    public static void main(String[] args)
    {
        try
        {
            jda = new JDABuilder("NjE0NjkwNTIwNDQyMjA4Mjky.Xr6kqg.9dRqzWGH6YcogNG8-BcsYGPYleg")
                    .addEventListeners(new MessageListenerExample())
                    .build();
            jda.awaitReady();
            System.out.println("Finished Building JDA!");
        }
        catch (LoginException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        User authorUser = event.getAuthor();
        if (!authorUser.isBot()) {
            Member authorMember = event.getMember();
            if(authorMember == null) return;
            String name = authorMember.getDisplayName();
            String message = event.getMessage().getContentDisplay();
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Chat -> " + name + ": " + message + "\r\n" +
                            "GetTextChannel -> " + (jda.getTextChannelById(710828174686027790L) != null)
            ).queue();
        }
    }
}
