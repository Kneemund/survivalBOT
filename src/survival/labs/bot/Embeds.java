package survival.labs.bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Embeds {

    public static final int GREEN = 0x2ecc71;
    public static final int RED = 0xe74c3c;
    public static final int BLUE = 0x3498db;


    /*public static MessageEmbed successEmbed(String description, String title) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(title, null, "https://img.icons8.com/color/48/000000/ok--v1.png")
                .setColor(GREEN)
                .setDescription(description);

        MessageEmbed embed = embedBuilder.build();
        embedBuilder.clear();
        return embed;
    }*/

    public static MessageEmbed errorEmbed(String description, String title) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(title, null, "https://img.icons8.com/color/48/000000/cancel--v1.png")
                .setColor(RED)
                .setDescription(description);

        MessageEmbed embed = embedBuilder.build();
        embedBuilder.clear();
        return embed;
    }

    public static MessageEmbed logEmbed(String log) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor("LOG", null, "https://img.icons8.com/officexs/48/000000/clock.png")
                .setColor(BLUE)
                .setDescription(log);

        MessageEmbed embed = embedBuilder.build();
        embedBuilder.clear();
        return embed;
    }

    public static MessageEmbed infoEmbed(String description, String title) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(title, null, "https://img.icons8.com/color/48/000000/info--v1.png")
                .setColor(BLUE)
                .setDescription(description);

        MessageEmbed embed = embedBuilder.build();
        embedBuilder.clear();
        return embed;
    }

    public static MessageEmbed genericEmbed(String description, String title, String iconUrl, int color) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(title, null, iconUrl)
                .setColor(color)
                .setDescription(description);

        MessageEmbed embed = embedBuilder.build();
        embedBuilder.clear();
        return embed;
    }
}
