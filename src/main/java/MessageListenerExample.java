import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.internal.entities.Member;
import me.syari.ss.discord.internal.entities.TextChannel;
import me.syari.ss.discord.internal.entities.User;

import javax.security.auth.login.LoginException;

public class MessageListenerExample {
    private static JDA jda;

    private static int sendMessageCount = 0;

    public static void main(String[] args) {
        try {
            jda = new JDABuilder("NjE0NjkwNTIwNDQyMjA4Mjky.Xr6kqg.9dRqzWGH6YcogNG8-BcsYGPYleg", (event) -> {
                User authorUser = event.getAuthor();
                if (!authorUser.isBot()) {
                    Member authorMember = event.getMember();
                    if (authorMember == null) return;
                    String name = authorMember.getDisplayName();
                    String message = event.getMessage().getContentDisplay();
                    TextChannel channel = event.getChannel();
                    channel.sendMessage("Chat -> " + name + ": " + message + "\r\n" +
                            "GetTextChannel -> " + (jda.getTextChannelById(710828174686027790L) != null)
                    ).queue();
                } else {
                    sendMessageCount ++;
                    if(sendMessageCount == 2){
                        System.out.println(">> Shutdown");
                        jda.shutdown();
                    }
                }
            }).build();
            jda.awaitReady();
            System.out.println("Finished Building JDA!");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
