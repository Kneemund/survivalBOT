package survival.labs.bot.events.commands;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import survival.labs.Database;
import survival.labs.bot.Bot;
import survival.labs.bot.Command;
import survival.labs.bot.Embeds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RemoveCommand implements Command {

    private final Database db;
    private final Bot bot;
    private final Plugin plugin;

    public RemoveCommand(Database db, Bot bot, Plugin plugin) {
        this.db = db;
        this.bot = bot;
        this.plugin = plugin;
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

        int deletedRows = db.updateCount(db.sqlRemove(uuid, userID));

        if (deletedRows == 0) {
            channel.sendMessage(Embeds.errorEmbed("No Minecraft account called '" + args[1] + "' is registered by <@" + userID + ">.", "ERROR")).queue();
            return;
        }

        try {
            ResultSet registeredAccounts = db.query(db.sqlCountByUserID(userID));
            if(registeredAccounts.next()) {
                if(registeredAccounts.getInt(1) == 0) event.getGuild().removeRoleFromMember(userID, event.getGuild().getRoleById(bot.verificationRoleID)).queue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Player player = plugin.getServer().getPlayer(UUID.fromString(uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5")));
        if (player != null) Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer(ChatColor.RED + "You have been removed from the whitelist."));

        channel.sendMessage(Embeds.genericEmbed("Removed '" + args[1] + "' from <@" + userID + ">.", "REMOVE", "https://img.icons8.com/flat_round/64/000000/minus.png", Embeds.RED)).queue();
        bot.sendLog(event.getGuild(), "'" + event.getAuthor().getName() + "' has removed '" + args[1] + "' from <@" + userID + ">.");
    }

    @Override
    public String getHelp() {
        return "Removes a Minecraft account from the specified user.";
    }

    @Override
    public boolean getPermission(GuildMessageReceivedEvent event) {
        return !Collections.disjoint(event.getMember().getRoles().stream().map(ISnowflake::getId).collect(Collectors.toList()), bot.whitelistRoleIDs);
    }

    @Override
    public String getUsage() {
        return "remove <mc_username> <userID|@user>";
    }

    @Override
    public int getArgsAmount() {
        return 2;
    }
}
