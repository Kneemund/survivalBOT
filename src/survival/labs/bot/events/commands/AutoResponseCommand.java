package survival.labs.bot.events.commands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONObject;
import survival.labs.bot.AutoResponseHandler;
import survival.labs.bot.Bot;
import survival.labs.bot.Command;
import survival.labs.bot.Embeds;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AutoResponseCommand implements Command {

    private final Bot bot;
    private final AutoResponseHandler autoResponseHandler;
    private final EventWaiter waiter;

    public AutoResponseCommand(Bot bot, AutoResponseHandler autoResponseHandler, EventWaiter waiter) {
        this.bot = bot;
        this.autoResponseHandler = autoResponseHandler;
        this.waiter = waiter;
    }

    @Override
    public void run(String[] args, GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        JSONObject autoResponses = autoResponseHandler.getAutoResponses();

        switch (args[1]) {
            case "add": {
                if (args.length < 3 || (!args[2].equals("equal") && !args[2].equals("include"))) {
                    channel.sendMessage(Embeds.errorEmbed("`" + bot.prefix + getUsage() + "`", "USAGE")).queue();
                    return;
                }

                channel.sendMessage("Enter the trigger:").queue(triggerMessage -> waiter.waitForEvent(
                        GuildMessageReceivedEvent.class,
                        e1 -> e1.getAuthor().equals(event.getAuthor()) && e1.getChannel().equals(event.getChannel()) && !e1.getMessage().equals(event.getMessage()),
                        e1 -> {
                            String key = e1.getMessage().getContentRaw();
                            triggerMessage.delete().queue();

                            channel.sendMessage("Enter the response:").queue(responseMessage -> waiter.waitForEvent(GuildMessageReceivedEvent.class,
                                    e2 -> e2.getAuthor().equals(event.getAuthor()) && e2.getChannel().equals(event.getChannel()) && !e2.getMessage().equals(event.getMessage()),
                                    e2 -> {
                                        String value = e2.getMessage().getContentRaw();
                                        responseMessage.delete().queue();

                                        JSONObject obj = (JSONObject) autoResponses.get(args[2]);
                                        obj.put(key.toLowerCase().trim(), value);
                                        autoResponses.replace(args[2], obj);

                                        autoResponseHandler.setAutoResponses(autoResponses);

                                        e2.getChannel().sendMessage(Embeds.genericEmbed("Added an automatic response of the type '" + args[2] + "' triggered by '" + key.toLowerCase().trim() + "'.", "AUTO RESPONSE", "https://img.icons8.com/color/48/000000/send-letter--v1.png", Embeds.BLUE)).queue();
                                        bot.sendLog(event.getGuild(), "'" + e2.getAuthor().getName() + "' added an automatic response of the type '" + args[2] + "' triggered by '" + key.toLowerCase().trim() + "'.");
                                    },
                                    1, TimeUnit.MINUTES,
                                    () -> channel.sendMessage("Stopped listening after 1 minute.").queue()));
                        },
                        1, TimeUnit.MINUTES,
                        () -> channel.sendMessage("Stopped listening after 1 minute.").queue()
                ));
                break;
            }
            case "remove": {
                if (args.length < 4 || (!args[2].equals("equal") && !args[2].equals("include"))) {
                    channel.sendMessage(Embeds.errorEmbed("`" + bot.prefix + getUsage() + "`", "USAGE")).queue();
                    return;
                }

                JSONObject obj = (JSONObject) autoResponses.get(args[2]);
                obj.remove(args[3].toLowerCase().trim());
                autoResponses.replace(args[2], obj);

                autoResponseHandler.setAutoResponses(autoResponses);

                channel.sendMessage(Embeds.genericEmbed("Removed an automatic response of the type '" + args[2] + "' triggered by '" + args[3].toLowerCase().trim() + "'.", "AUTO RESPONSE", "https://img.icons8.com/color/48/000000/send-letter--v1.png", Embeds.BLUE)).queue();
                bot.sendLog(event.getGuild(), "'" + event.getAuthor().getName() + "' removed an automatic response of the type '" + args[2] + "' triggered by '" + args[3].toLowerCase().trim() + "'.");
                break;
            }
            case "list": {
                JSONObject equalMessage = (JSONObject) autoResponses.get("equal");
                StringBuilder equalList = new StringBuilder();
                for (Object str : equalMessage.keySet()) equalList.append(str.toString()).append("\n");

                JSONObject includeMessage = (JSONObject) autoResponses.get("include");
                StringBuilder includeList = new StringBuilder();
                for (Object str : includeMessage.keySet()) includeList.append(str.toString()).append("\n");

                EmbedBuilder embed = new EmbedBuilder()
                        .setAuthor("AUTO RESPONSE", null, "https://img.icons8.com/color/48/000000/send-letter--v1.png")
                        .setColor(0x3498db)
                        .addField("EQUAL", equalList.toString().equals("") ? "NONE" : equalList.toString(), false)
                        .addField("INCLUDE", includeList.toString().equals("") ? "NONE" : includeList.toString(), false);
                channel.sendMessage(embed.build()).queue();
                embed.clear();
                break;
            }
            default:
                channel.sendMessage(Embeds.errorEmbed("`" + bot.prefix + getUsage() + "`", "USAGE")).queue();
                break;
        }
    }

    @Override
    public String getHelp() {
        return "Adds, removes or lists auto responses.";
    }

    @Override
    public boolean getPermission(GuildMessageReceivedEvent event) {
        return !Collections.disjoint(event.getMember().getRoles().stream().map(ISnowflake::getId).collect(Collectors.toList()), bot.whitelistRoleIDs);
    }

    @Override
    public String getUsage() {
        return "autoResponse <add [equal|include]|remove [equal|include] <key>|list>";
    }

    @Override
    public int getArgsAmount() {
        return 1;
    }
}
