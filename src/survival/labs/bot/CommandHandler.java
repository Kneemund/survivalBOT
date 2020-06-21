package survival.labs.bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.HashMap;

public class CommandHandler {

    private final Bot bot;

    public CommandHandler(Bot bot) {
        this.bot = bot;
    }

    public HashMap<String, Command> commands = new HashMap<>();

    public void handleCommand(String[] args, GuildMessageReceivedEvent event) {
        String invoke = args[0].substring(1).toLowerCase();

        if (commands.containsKey(invoke)) {
            Command command = commands.get(invoke);

            if (args.length - 1 >= command.getArgsAmount()) {
                if (command.getPermission(event)) command.run(args, event);
            } else {
                event.getChannel().sendMessage(Embeds.errorEmbed("`" + bot.prefix + command.getUsage() + "`", "USAGE")).queue();
            }
        } else if (invoke.equals("help")) {
            StringBuilder description = new StringBuilder();
            for (Command command : commands.values()) {
                if (command.getPermission(event)) {
                    description.append("`").append(bot.prefix).append(command.getUsage()).append("`\n").append(command.getHelp()).append("\n\n");
                }
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("HELP", null, "https://img.icons8.com/color/48/000000/help--v1.png")
                    .setColor(0x3498db)
                    .setDescription(description.toString());
            event.getChannel().sendMessage(embed.build()).queue();
            embed.clear();
        }
    }
}
