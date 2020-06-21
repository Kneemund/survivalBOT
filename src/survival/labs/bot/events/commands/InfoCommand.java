package survival.labs.bot.events.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import survival.labs.Database;
import survival.labs.bot.Bot;
import survival.labs.bot.Command;
import survival.labs.bot.Embeds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class InfoCommand implements Command {

    private final Database db;
    private final Bot bot;

    public InfoCommand(Database db, Bot bot) {
        this.db = db;
        this.bot = bot;
    }

    @Override
    public void run(String[] args, GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        List<User> mentions = event.getMessage().getMentionedUsers();
        String userID = mentions.isEmpty() ? args[1] : mentions.get(0).getId();

        StringBuilder accountList = new StringBuilder();

        try {
            ResultSet accounts = db.query(db.sqlSelectByUserID(userID));
            while (accounts.next()) {
                String uuid = accounts.getString("mc_uuid");
                accountList.append(bot.getCurrentUsername(uuid)).append(" (").append(uuid).append(")\n");
            }
            accounts.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        channel.sendMessage(Embeds.infoEmbed(accountList.toString().equals("") ? "<@" + userID + "> does not have any registered Minecraft accounts." : accountList.toString(), "INFO")).queue();
    }

    @Override
    public String getHelp() {
        return "Returns information about all Minecraft accounts linked to the specified user.";
    }

    @Override
    public boolean getPermission(GuildMessageReceivedEvent event) {
        return true;
    }

    @Override
    public String getUsage() {
        return "info <userID|@user>";
    }

    @Override
    public int getArgsAmount() {
        return 1;
    }
}
