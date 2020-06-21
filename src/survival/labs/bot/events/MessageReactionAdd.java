package survival.labs.bot.events;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import survival.labs.Database;
import survival.labs.bot.Bot;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MessageReactionAdd extends ListenerAdapter {

    private final Bot bot;
    private final Database db;
    private final User botUser;

    public MessageReactionAdd(Bot bot, Database db, User botUser) {
        this.bot = bot;
        this.db = db;
        this.botUser = botUser;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot() || !event.getChannel().getId().equals(bot.whitelistChannelID)) return;

        if (Collections.disjoint(event.getMember().getRoles().stream().map(ISnowflake::getId).collect(Collectors.toList()), bot.whitelistRoleIDs)) {
            event.getReaction().removeReaction(event.getUser()).queue();
            return;
        }

        Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        MessageEmbed embed = message.getEmbeds().get(0);

        if (embed.getAuthor().getName().equals("WHITELIST REQUEST")) {
            Boolean approved = event.getReactionEmote().getName().equals("✅");
            Boolean rejected = event.getReactionEmote().getName().equals("❌");

            if (message.retrieveReactionUsers("✅").complete().contains(botUser) && message.retrieveReactionUsers("❌").complete().contains(botUser) && (approved || rejected)) {
                List<MessageEmbed.Field> fields = embed.getFields();
                String name = fields.get(0).getValue();

                if (approved) {
                    String userID = fields.get(3).getValue();
                    db.update(db.sqlAdd(fields.get(1).getValue(), userID));
                    event.getGuild().addRoleToMember(userID, event.getGuild().getRoleById(bot.verificationRoleID)).queue();

                    bot.sendLog(event.getGuild(),"Whitelist request from '" + name + "' accepted by '" + event.getUser().getName() + "'.");
                } else {
                    bot.sendLog(event.getGuild(),"Whitelist request from '" + name + "' rejected by '" + event.getUser().getName() + "'.");
                }

                message.removeReaction("✅", botUser).queue();
                message.removeReaction("❌", botUser).queue();
            } else {
                event.getReaction().removeReaction(event.getUser()).queue();
            }
        }
    }
}
