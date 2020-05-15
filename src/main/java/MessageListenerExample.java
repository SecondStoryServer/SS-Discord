import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class MessageListenerExample extends ListenerAdapter
{
    public static void main(String[] args)
    {
        try
        {
            JDA jda = new JDABuilder("NjE0NjkwNTIwNDQyMjA4Mjky.Xr6kqg.9dRqzWGH6YcogNG8-BcsYGPYleg")
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
    public void onMessageReceived(MessageReceivedEvent event)
    {
        MessageChannel channel = event.getChannel();
        User authorUser = event.getAuthor();
        if (!authorUser.isBot()) {
            String name = authorUser.getName();
            Member authorMember = event.getMember();
            if(authorMember != null){
                String nickName = authorMember.getNickname();
                if(nickName != null){
                    name = nickName;
                }
            }
            channel.sendMessage(name + ": " + event.getMessage().getContentDisplay() + "[" + (event.getJDA().getTextChannelById(710828174686027790L) != null) + "]").queue();
        }
    }
}
