package survival.labs.bot.events;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.plugin.Plugin;
import survival.labs.Database;
import survival.labs.bot.AutoResponseHandler;
import survival.labs.bot.Bot;
import survival.labs.bot.CommandHandler;
import survival.labs.bot.events.commands.*;

public class GuildMessageReceived extends ListenerAdapter {

    private final Bot bot;

    private final AutoResponseHandler autoResponseHandler;
    private final CommandHandler handler;

    public GuildMessageReceived(Bot bot, Database db, EventWaiter waiter, Plugin plugin) {
        this.bot = bot;

        autoResponseHandler = new AutoResponseHandler();

        handler = new CommandHandler(bot);
        handler.commands.put("add", new AddCommand(db, bot));
        handler.commands.put("getusername", new GetUsernameCommand(bot));
        handler.commands.put("getuuid", new GetUUIDCommand(bot));
        handler.commands.put("info", new InfoCommand(db, bot));
        handler.commands.put("removeall", new RemoveAllCommand(db, bot, plugin));
        handler.commands.put("remove", new RemoveCommand(db, bot, plugin));
        handler.commands.put("whitelist", new WhitelistCommand(db, bot));
        handler.commands.put("autoresponse", new AutoResponseCommand(bot, autoResponseHandler, waiter));
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.isWebhookMessage()) return;

        if(event.getMessage().getContentRaw().startsWith(bot.prefix)) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            handler.handleCommand(args, event);
        } else {
            autoResponseHandler.handleResponse(event, bot.autoResponseCooldown);
        }
    }
}
