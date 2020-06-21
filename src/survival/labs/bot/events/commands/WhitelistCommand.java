package survival.labs.bot.events.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import survival.labs.bot.Bot;
import survival.labs.Database;
import survival.labs.bot.Command;
import survival.labs.bot.Embeds;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WhitelistCommand implements Command {

    private final Database db;
    private final Bot bot;

    public WhitelistCommand(Database db, Bot bot) {
        this.db = db;
        this.bot = bot;
    }

    @Override
    public void run(String[] args, GuildMessageReceivedEvent event) {
        if(!event.getChannel().getId().equals(bot.whitelistChannelID)) return;

        TextChannel channel = event.getChannel();
        Message message = event.getMessage();
        String uuid = bot.getUUID(args[1]);

        if (uuid == null) {
            channel.sendMessage(Embeds.errorEmbed("A Minecraft account called '" + args[1] + "' does not exist.", "ERROR")).queue();
            return;
        }

        User author = message.getAuthor();
        String userID = author.getId();

        String accountRegisteredTo = null;
        int mcAccounts = 0;

        try {
            ResultSet accountRegistrations = db.query(db.sqlSelectByUUID(uuid));
            if(accountRegistrations.first()) accountRegisteredTo = accountRegistrations.getString("user_id");
            accountRegistrations.close();

            if(accountRegisteredTo != null) {
                channel.sendMessage(Embeds.errorEmbed("The Minecraft account '" + args[1] + "' has already been registered by <@" + accountRegisteredTo + ">.", "ERROR")).queue();
                return;
            }

            ResultSet registeredAccounts = db.query(db.sqlCountByUserID(userID));
            if(registeredAccounts.first()) mcAccounts = registeredAccounts.getInt(1);
            registeredAccounts.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        EmbedBuilder whitelistRequest = new EmbedBuilder();
        whitelistRequest.setAuthor("WHITELIST REQUEST", null, "https://img.icons8.com/flat_round/64/000000/settings--v1.png")
                .setColor(0x32bea6)
                .addField("MC-Username", args[1], false)
                .addField("UUID", uuid, true)
                .addField("Discord-Username", author.getName(), false)
                .addField("UserID", userID, true);

        if (mcAccounts != 0) whitelistRequest.setDescription("<@" + userID + "> already registered " + mcAccounts + " other Minecraft account" + (mcAccounts == 1 ? "." : "s."));

        message.delete().queue();
        channel.sendMessage(whitelistRequest.build()).queue(whitelistEmbed -> {
            whitelistEmbed.addReaction("✅").queue();
            whitelistEmbed.addReaction("❌").queue();
        });

        whitelistRequest.clear();
    }

    @Override
    public String getHelp() {
        return "Creates a whitelist request that links the specified Minecraft account to the author once it gets accepted by an admin.";
    }

    @Override
    public boolean getPermission(GuildMessageReceivedEvent event) {
        return true;
    }

    @Override
    public String getUsage() {
        return "whitelist <username>";
    }

    @Override
    public int getArgsAmount() {
        return 1;
    }
}
