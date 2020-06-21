package survival.labs.bot.events.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import survival.labs.bot.Bot;
import survival.labs.bot.Command;
import survival.labs.bot.Embeds;

public class GetUsernameCommand implements Command {

    private final Bot bot;

    public GetUsernameCommand(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void run(String[] args, GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        String username = bot.getCurrentUsername(args[1]);

        if(username == null) {
            channel.sendMessage(Embeds.errorEmbed("'" + args[1] + "' is an invalid UUID.", "ERROR")).queue();
            return;
        }

        channel.sendMessage(Embeds.infoEmbed(username, "USERNAME")).queue();
    }

    @Override
    public String getHelp() {
        return "Returns the Minecraft account of the specified UUID.";
    }

    @Override
    public boolean getPermission(GuildMessageReceivedEvent event) {
        return true;
    }

    @Override
    public String getUsage() {
        return "getUsername <uuid>";
    }

    @Override
    public int getArgsAmount() {
        return 1;
    }
}
