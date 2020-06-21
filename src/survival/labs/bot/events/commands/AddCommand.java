package survival.labs.bot.events.commands;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import survival.labs.Database;
import survival.labs.bot.Bot;
import survival.labs.bot.Command;
import survival.labs.bot.Embeds;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AddCommand implements Command {

    private final Database db;
    private final Bot bot;

    public AddCommand(Database db, Bot bot) {
        this.db = db;
        this.bot = bot;
    }

    @Override
    public void run(String[] args, GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        TextChannel channel = message.getTextChannel();
        List<User> mentions = message.getMentionedUsers();

        String uuid = bot.getUUID(args[1]);
        String userID = mentions.isEmpty() ? args[2] : mentions.get(0).getId();

        if (uuid == null) {
            channel.sendMessage(Embeds.errorEmbed("A Minecraft account called '" + args[1] + "' does not exist.", "ERROR")).queue();
            return;
        }

        if (userID.length() < 18) {
            channel.sendMessage(Embeds.errorEmbed("The user ID '" + userID + "' is invalid.", "ERROR")).queue();
            return;
        }

        db.update(db.sqlAdd(uuid, userID));
        event.getGuild().addRoleToMember(userID, event.getGuild().getRoleById(bot.verificationRoleID)).queue();

        channel.sendMessage(Embeds.genericEmbed("Added '" + args[1] + "' to <@" + userID + ">.", "ADD", "https://img.icons8.com/color/48/000000/add.png", Embeds.GREEN)).queue();
        bot.sendLog(event.getGuild(), "'" + event.getAuthor().getName() + "' added '" + args[1] + "' to <@" + userID + ">.");
    }

    @Override
    public String getHelp() {
        return "Links a Minecraft account to the specified user.";
    }

    @Override
    public boolean getPermission(GuildMessageReceivedEvent event) {
        return !Collections.disjoint(event.getMember().getRoles().stream().map(ISnowflake::getId).collect(Collectors.toList()), bot.whitelistRoleIDs);
    }

    @Override
    public String getUsage() {
        return "add <mc_username> <userID|@user>";
    }

    @Override
    public int getArgsAmount() {
        return 2;
    }
}
