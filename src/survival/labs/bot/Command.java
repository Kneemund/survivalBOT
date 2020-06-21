package survival.labs.bot;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface Command {
    void run(String[] args, GuildMessageReceivedEvent event);
    String getHelp();
    boolean getPermission(GuildMessageReceivedEvent event);
    String getUsage();
    int getArgsAmount();
}
