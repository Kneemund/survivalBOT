package survival.labs.bot.events.commands;

import net.dv8tion.jda.api.entities.ISnowflake;
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

public class RemoveAllCommand implements Command {

    private final Database db;
    private final Bot bot;
    private final Plugin plugin;

    public RemoveAllCommand(Database db, Bot bot, Plugin plugin) {
        this.db = db;
        this.bot = bot;
        this.plugin = plugin;
    }

    @Override
    public void run(String[] args, GuildMessageReceivedEvent event) {
        TextChannel channel = event.getMessage().getTextChannel();
        List<User> mentions = event.getMessage().getMentionedUsers();
        String userID = mentions.isEmpty() ? args[1] : mentions.get(0).getId();

        StringBuilder accountList = new StringBuilder();

        try {
            ResultSet registeredMcAccounts = db.query(db.sqlSelectByUserID(userID));
            while (registeredMcAccounts.next()) {
                String uuid = registeredMcAccounts.getString("mc_uuid");
                accountList.append(bot.getCurrentUsername(uuid)).append(" (").append(uuid).append(")\n");

                Player player = plugin.getServer().getPlayer(UUID.fromString(uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5")));
                if (player != null) Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer(ChatColor.RED + "You have been removed from the whitelist."));
            }
            registeredMcAccounts.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (accountList.toString().equals("")) {
            channel.sendMessage(Embeds.errorEmbed("There are no accounts registered by <@" + userID + ">.", "ERROR")).queue();
            return;
        }

        int count = db.updateCount(db.sqlRemoveAll(userID));
        event.getGuild().removeRoleFromMember(userID, event.getGuild().getRoleById(bot.verificationRoleID)).queue();

        channel.sendMessage(Embeds.genericEmbed("Removed " + count + " registered accounts from <@" + userID + ">:\n" + accountList, "REMOVE", "https://img.icons8.com/flat_round/64/000000/minus.png", Embeds.RED)).queue();
        bot.sendLog(event.getGuild(), "'" + event.getAuthor().getName() + "' removed " + count + " registered account(s) from <@" + userID + ">:\n" + accountList);
    }

    @Override
    public String getHelp() {
        return "Removes all Minecraft accounts from the specified user.";
    }

    @Override
    public boolean getPermission(GuildMessageReceivedEvent event) {
        return !Collections.disjoint(event.getMember().getRoles().stream().map(ISnowflake::getId).collect(Collectors.toList()), bot.whitelistRoleIDs);
    }

    @Override
    public String getUsage() {
        return "removeAll <userID|@user>";
    }

    @Override
    public int getArgsAmount() {
        return 1;
    }
}
