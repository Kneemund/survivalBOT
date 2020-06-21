package survival.labs.bot.events;

import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import survival.labs.Database;
import survival.labs.bot.Bot;

public class GuildMemberLeave extends ListenerAdapter {

    private final Bot bot;
    private final Database db;

    public GuildMemberLeave(Bot bot, Database db) {
        this.bot = bot;
        this.db = db;
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        String userID = event.getUser().getId();
        int count = db.updateCount(db.sqlRemoveAll(userID));
        if (count != 0) bot.sendLog(event.getGuild(), count + " Minecraft account(s) from <@" + userID + "> has/have been removed because they were kicked/banned or left.");
    }
}
