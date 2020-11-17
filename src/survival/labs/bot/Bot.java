package survival.labs.bot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import survival.labs.Database;
import survival.labs.Main;
import survival.labs.bot.events.GuildMemberLeave;
import survival.labs.bot.events.GuildMessageReceived;
import survival.labs.bot.events.MessageReactionAdd;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Bot {

    private JDA jda;
    private final Database db;

    public String token, logChannelID, prefix, whitelistChannelID, verificationRoleID;
    public int autoResponseCooldown;
    public List<String> whitelistRoleIDs;

    public Bot(Database db) {
        this.db = db;
    }

    public void startBot(FileConfiguration config) {
        Plugin plugin = Main.getPlugin(Main.class);

        token = config.getString("Bot.Token");
        logChannelID = config.getString("Bot.LogChannelID");
        whitelistChannelID = config.getString("Bot.WhitelistChannelID");
        whitelistRoleIDs = config.getStringList("Bot.WhitelistRoleIDs");
        prefix = config.getString("Bot.Prefix");
        autoResponseCooldown = config.getInt("Bot.AutoResponseCooldown");
        verificationRoleID = config.getString("Bot.VerificationRoleID");

        EventWaiter waiter = new EventWaiter();

        if (token == null || token.length() == 0) {
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "PLEASE SPECIFY A TOKEN IN THE CONFIG FILE");
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        }

        try {
            jda = JDABuilder.createDefault(token).setActivity(Activity.playing(config.getString("Bot.Activity"))).build();

            jda.addEventListener(new GuildMemberLeave(this, db));
            jda.addEventListener(new GuildMessageReceived(this, db, waiter, plugin));
            jda.addEventListener(new MessageReactionAdd(this, db, jda.getSelfUser()));
            jda.addEventListener(waiter);
        } catch (LoginException e) {
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "FAILED TO LOG IN, IS THE TOKEN CORRECT?");
            e.printStackTrace();
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    public void stopBot() {
        jda.shutdown();
    }

    public String getUUID(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();

            if (status == 204) {
                con.disconnect();
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject response = (JSONObject) parser.parse(content.toString());
            return response.get("id").toString().replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCurrentUsername(String uuid) {
        try {
            URL url = new URL("https://api.mojang.com/user/profiles/" + uuid.replaceAll("-", "") + "/names");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();

            if (status == 204) {
                con.disconnect();
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            JSONParser parser = new JSONParser();
            JSONArray response = (JSONArray) parser.parse(content.toString());
            JSONObject currentName = (JSONObject) response.get(response.size() - 1);
            return currentName.get("name").toString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendLog(Guild guild, String log) {
        if (!logChannelID.equals("")) guild.getTextChannelById(logChannelID).sendMessage(Embeds.logEmbed(log)).queue();
    }
}
