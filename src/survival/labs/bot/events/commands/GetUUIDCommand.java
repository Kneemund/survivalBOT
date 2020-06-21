package survival.labs.bot.events.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import survival.labs.bot.Bot;
import survival.labs.bot.Command;
import survival.labs.bot.Embeds;

public class GetUUIDCommand implements Command {

    private final Bot bot;

    public GetUUIDCommand(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void run(String[] args, GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        String uuid = bot.getUUID(args[1]);

        if(uuid == null) {
            channel.sendMessage(Embeds.errorEmbed("A Minecraft account called '" + args[1] + "' does not exist.", "ERROR")).queue();
            return;
        }

        channel.sendMessage(Embeds.infoEmbed(uuid, "UUID")).queue();
    }

    @Override
    public String getHelp() {
        return "Returns the UUID of the specified Minecraft account.";
    }

    @Override
    public boolean getPermission(GuildMessageReceivedEvent event) {
        return true;
    }

    @Override
    public String getUsage() {
        return "getUUID <uuid>";
    }

    @Override
    public int getArgsAmount() {
        return 1;
    }
}
